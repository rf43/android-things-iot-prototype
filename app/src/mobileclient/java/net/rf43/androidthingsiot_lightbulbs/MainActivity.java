package net.rf43.androidthingsiot_lightbulbs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ImageView mRedLightImage;
    private ImageView mGreenLightImage;
    private ImageView mBlueLightImage;

    private boolean mLightState01;
    private boolean mLightState02;
    private boolean mLightState03;

    private DatabaseReference mRefLightRed;
    private DatabaseReference mRefLightGreen;
    private DatabaseReference mRefLightBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);

        initFirebaseRefs();
        initEventListeners();
        initUiElements();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRefLightRed.removeEventListener(mRedLightEventListener);
        mRefLightGreen.removeEventListener(mBlueLightEventListener);
        mRefLightBlue.removeEventListener(mGreenLightEventListener);
    }

    private void initFirebaseRefs() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference baseRef = database.getReference().child(FirebaseConsts.KEY_LIGHT_STATE);

        mRefLightRed = baseRef.child(FirebaseConsts.KEY_LIGHT_RED).child(FirebaseConsts.KEY_STATE);
        mRefLightGreen = baseRef.child(FirebaseConsts.KEY_LIGHT_GREEN).child(FirebaseConsts.KEY_STATE);
        mRefLightBlue = baseRef.child(FirebaseConsts.KEY_LIGHT_BLUE).child(FirebaseConsts.KEY_STATE);
    }

    private void initUiElements() {
        final View containerRedLight = findViewById(R.id.wrapper_red_light);
        final View containerGreenLight = findViewById(R.id.wrapper_green_light);
        final View containerBlueLight = findViewById(R.id.wrapper_blue_light);

        mRedLightImage = (ImageView) findViewById(R.id.image_view_red_light);
        mGreenLightImage = (ImageView) findViewById(R.id.image_view_green_light);
        mBlueLightImage = (ImageView) findViewById(R.id.image_view_blue_light);

        containerRedLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                toggleLightState(mRefLightRed, mLightState01);
            }
        });
        containerGreenLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                toggleLightState(mRefLightGreen, mLightState02);
            }
        });
        containerBlueLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                toggleLightState(mRefLightBlue, mLightState03);
            }
        });
    }

    private void initEventListeners() {
        mRefLightRed.addValueEventListener(mRedLightEventListener);
        mRefLightGreen.addValueEventListener(mGreenLightEventListener);
        mRefLightBlue.addValueEventListener(mBlueLightEventListener);
    }

    private void toggleLightState(final DatabaseReference databaseReference, final boolean state) {
        databaseReference.setValue(state ? FirebaseConsts.VALUE_OFF : FirebaseConsts.VALUE_ON);
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

    // Event Listeners

    private ValueEventListener mRedLightEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String lightState = dataSnapshot.getValue(String.class);
            mLightState01 = TextUtils.equals(FirebaseConsts.VALUE_ON, lightState);
            bindViewData(FirebaseConsts.KEY_LIGHT_RED, mLightState01);
        }

        @Override
        public void onCancelled(final DatabaseError databaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", databaseError.toException());
        }
    };

    private ValueEventListener mGreenLightEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String lightState = dataSnapshot.getValue(String.class);
            mLightState02 = TextUtils.equals(FirebaseConsts.VALUE_ON, lightState);
            bindViewData(FirebaseConsts.KEY_LIGHT_GREEN, mLightState02);
        }

        @Override
        public void onCancelled(final DatabaseError databaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", databaseError.toException());
        }
    };

    private ValueEventListener mBlueLightEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String lightState = dataSnapshot.getValue(String.class);
            mLightState03 = TextUtils.equals(FirebaseConsts.VALUE_ON, lightState);
            bindViewData(FirebaseConsts.KEY_LIGHT_BLUE, mLightState03);
        }

        @Override
        public void onCancelled(final DatabaseError databaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", databaseError.toException());
        }
    };
}
