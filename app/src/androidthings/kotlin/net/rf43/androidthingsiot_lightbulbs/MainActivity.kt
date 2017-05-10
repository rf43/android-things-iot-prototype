package net.rf43.androidthingsiot_lightbulbs

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import com.google.firebase.database.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var mRedLightImage: ImageView
    private lateinit var mGreenLightImage: ImageView
    private lateinit var mBlueLightImage: ImageView

    private lateinit var mRefLightRed: DatabaseReference
    private lateinit var mRefLightGreen: DatabaseReference
    private lateinit var mRefLightBlue: DatabaseReference

    private lateinit var mLedGpio01: Gpio
    private lateinit var mLedGpio02: Gpio
    private lateinit var mLedGpio03: Gpio

    private val GPIO_PIN_RED_LIGHT = "BCM23"
    private val GPIO_PIN_GRN_LIGHT = "BCM24"
    private val GPIO_PIN_BLU_LIGHT = "BCM25"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_android_things_main)

        initUiElements()
        initFirebaseRefs()
        initGpio()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Close the Gpio pin.
        Log.i(TAG, "Closing LED GPIO pin")
        try {
            mLedGpio01.close()
            mLedGpio02.close()
            mLedGpio03.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }

    private fun initFirebaseRefs() {
        val database = FirebaseDatabase.getInstance()
        val baseRef = database.reference.child(KEY_LIGHT_STATE)

        mRefLightRed = baseRef.child(KEY_LIGHT_RED).child(KEY_STATE)
        mRefLightGreen = baseRef.child(KEY_LIGHT_GREEN).child(KEY_STATE)
        mRefLightBlue = baseRef.child(KEY_LIGHT_BLUE).child(KEY_STATE)

        initEventListeners()
    }

    private fun initUiElements() {
        mRedLightImage = findViewById(R.id.image_view_red_light) as ImageView
        mGreenLightImage = findViewById(R.id.image_view_green_light) as ImageView
        mBlueLightImage = findViewById(R.id.image_view_blue_light) as ImageView
    }

    private fun initEventListeners() {
        mRefLightRed.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val lightState = TextUtils.equals(VALUE_ON, dataSnapshot.getValue(String::class.java))
                bindViewData(KEY_LIGHT_RED, lightState)
                toggleGpioState(mLedGpio01, lightState)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })

        mRefLightGreen.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val lightState = TextUtils.equals(VALUE_ON, dataSnapshot.getValue(String::class.java))
                bindViewData(KEY_LIGHT_GREEN, lightState)
                toggleGpioState(mLedGpio02, lightState)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })

        mRefLightBlue.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val lightState = TextUtils.equals(VALUE_ON, dataSnapshot.getValue(String::class.java))
                bindViewData(KEY_LIGHT_BLUE, lightState)
                toggleGpioState(mLedGpio03, lightState)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })
    }

    private fun initGpio() {
        val service = PeripheralManagerService()
        Log.d(TAG, "Available GPIO: " + service.gpioList)

        try {
            mLedGpio01 = service.openGpio(GPIO_PIN_RED_LIGHT)
            mLedGpio02 = service.openGpio(GPIO_PIN_GRN_LIGHT)
            mLedGpio03 = service.openGpio(GPIO_PIN_BLU_LIGHT)

            mLedGpio01.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            mLedGpio02.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            mLedGpio03.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }

    private fun bindViewData(which: String, state: Boolean) {
        var imgResId = R.drawable.grey_lt_bulb

        when (which) {
            KEY_LIGHT_RED -> {
                if (state) imgResId = R.drawable.red_lt_bulb
                mRedLightImage.setImageDrawable(getDrawable(imgResId))
            }
            KEY_LIGHT_GREEN -> {
                if (state) imgResId = R.drawable.grn_lt_bulb
                mGreenLightImage.setImageDrawable(getDrawable(imgResId))
            }
            KEY_LIGHT_BLUE -> {
                if (state) imgResId = R.drawable.blue_lt_bulb
                mBlueLightImage.setImageDrawable(getDrawable(imgResId))
            }
        }
    }

    private fun toggleGpioState(ledGpio: Gpio, onOffState: Boolean) {
        try {
            ledGpio.value = onOffState
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }
}
