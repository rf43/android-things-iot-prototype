package net.rf43.androidthingsiot_lightbulbs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView mRedLightImage;
    private ImageView mGreenLightImage;
    private ImageView mBlueLightImage;

    private DatabaseReference mRefLightRed;
    private DatabaseReference mRefLightGreen;
    private DatabaseReference mRefLightBlue;

    private Gpio mLedGpio01;
    private Gpio mLedGpio02;
    private Gpio mLedGpio03;

    private static final String GPIO_PIN_RED_LIGHT = "BCM23";
    private static final String GPIO_PIN_GRN_LIGHT = "BCM24";
    private static final String GPIO_PIN_BLU_LIGHT = "BCM25";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_things_main);

        initFirebaseRefs();
        initEventListeners();
        initUiElements();
        initGpio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the Gpio pin.
        Log.i(TAG, "Closing LED GPIO pin");
        try {
            mLedGpio01.close();
            mLedGpio02.close();
            mLedGpio03.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpio01 = null;
            mLedGpio02 = null;
            mLedGpio03 = null;
        }
    }

    private void initFirebaseRefs() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference baseRef = database.getReference().child(FirebaseConsts.KEY_LIGHT_STATE);

        mRefLightRed = baseRef.child(FirebaseConsts.KEY_LIGHT_RED).child(FirebaseConsts.KEY_STATE);
        mRefLightGreen = baseRef.child(FirebaseConsts.KEY_LIGHT_GREEN).child(FirebaseConsts.KEY_STATE);
        mRefLightBlue = baseRef.child(FirebaseConsts.KEY_LIGHT_BLUE).child(FirebaseConsts.KEY_STATE);
    }

    private void initUiElements() {
        mRedLightImage = (ImageView) findViewById(R.id.image_view_red_light);
        mGreenLightImage = (ImageView) findViewById(R.id.image_view_green_light);
        mBlueLightImage = (ImageView) findViewById(R.id.image_view_blue_light);
    }

    private void initEventListeners() {
        mRefLightRed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                final String lightStateRef = dataSnapshot.getValue(String.class);
                final boolean lightState = TextUtils.equals(FirebaseConsts.VALUE_ON, lightStateRef);
                bindViewData(FirebaseConsts.KEY_LIGHT_RED, lightState);
                toggleGpioState(mLedGpio01, lightState);
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        mRefLightGreen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                final String lightStateRef = dataSnapshot.getValue(String.class);
                final boolean lightState = TextUtils.equals(FirebaseConsts.VALUE_ON, lightStateRef);
                bindViewData(FirebaseConsts.KEY_LIGHT_GREEN, lightState);
                toggleGpioState(mLedGpio02, lightState);
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        mRefLightBlue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                final String lightStateRef = dataSnapshot.getValue(String.class);
                final boolean lightState = TextUtils.equals(FirebaseConsts.VALUE_ON, lightStateRef);
                bindViewData(FirebaseConsts.KEY_LIGHT_BLUE, lightState);
                toggleGpioState(mLedGpio03, lightState);
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void initGpio() {
        PeripheralManagerService service = new PeripheralManagerService();
        Log.d(TAG, "Available GPIO: " + service.getGpioList());

        try {
            mLedGpio01 = service.openGpio(GPIO_PIN_RED_LIGHT);
            mLedGpio02 = service.openGpio(GPIO_PIN_GRN_LIGHT);
            mLedGpio03 = service.openGpio(GPIO_PIN_BLU_LIGHT);

            mLedGpio01.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio02.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio03.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    private void bindViewData(final String which, final boolean state) {
        ImageView imageView = null;
        int imgResId = R.drawable.grey_lt_bulb;

        switch (which) {
            case FirebaseConsts.KEY_LIGHT_RED:
                imageView = mRedLightImage;
                imgResId = state ? R.drawable.red_lt_bulb : R.drawable.grey_lt_bulb;
                break;
            case FirebaseConsts.KEY_LIGHT_GREEN:
                imageView = mGreenLightImage;
                imgResId = state ? R.drawable.grn_lt_bulb : R.drawable.grey_lt_bulb;
                break;
            case FirebaseConsts.KEY_LIGHT_BLUE:
                imageView = mBlueLightImage;
                imgResId = state ? R.drawable.blue_lt_bulb : R.drawable.grey_lt_bulb;
                break;
        }

        if (imageView != null) {
            imageView.setImageDrawable(getDrawable(imgResId));
        }
    }

    private void toggleGpioState(final Gpio ledGpio, final boolean onOffState) {
        try {
            ledGpio.setValue(onOffState);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }
}
