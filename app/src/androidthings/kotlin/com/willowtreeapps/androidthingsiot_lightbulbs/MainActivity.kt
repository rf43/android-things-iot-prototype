package com.willowtreeapps.androidthingsiot_lightbulbs

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.IOException

class MainActivity : BaseMainActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var mLedGpio01: Gpio
    private lateinit var mLedGpio02: Gpio
    private lateinit var mLedGpio03: Gpio

    private val GPIO_PIN_RED_LIGHT = "BCM23"
    private val GPIO_PIN_GRN_LIGHT = "BCM24"
    private val GPIO_PIN_BLU_LIGHT = "BCM25"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_android_things_main)
        initActivity()
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

    override fun initEventListeners() {
        mRefLightRed.addValueEventListener(mRedLightEventListener)
        mRefLightGreen.addValueEventListener(mGreenLightEventListener)
        mRefLightBlue.addValueEventListener(mBlueLightEventListener)
    }

    override fun removeEventListeners() {
        mRefLightRed.removeEventListener(mRedLightEventListener)
        mRefLightGreen.removeEventListener(mBlueLightEventListener)
        mRefLightBlue.removeEventListener(mGreenLightEventListener)
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

    private fun toggleGpioState(ledGpio: Gpio, onOffState: Boolean) {
        try {
            ledGpio.value = onOffState
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }

    // Event Listeners

    private val mRedLightEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            mLightBulbState.redBulbState = TextUtils.equals(VALUE_ON, dataSnapshot.value as CharSequence?)
            bindViewData(mLightBulbState)
            toggleGpioState(mLedGpio01, mLightBulbState.redBulbState)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", databaseError.toException())
        }
    }

    private val mGreenLightEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            mLightBulbState.greenBulbState = TextUtils.equals(VALUE_ON, dataSnapshot.value as CharSequence?)
            bindViewData(mLightBulbState)
            toggleGpioState(mLedGpio02, mLightBulbState.greenBulbState)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", databaseError.toException())
        }
    }

    private val mBlueLightEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            mLightBulbState.blueBulbState = TextUtils.equals(VALUE_ON, dataSnapshot.value as CharSequence?)
            bindViewData(mLightBulbState)
            toggleGpioState(mLedGpio03, mLightBulbState.blueBulbState)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", databaseError.toException())
        }
    }
}
