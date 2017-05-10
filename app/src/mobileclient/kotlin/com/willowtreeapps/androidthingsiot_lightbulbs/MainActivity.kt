package com.willowtreeapps.androidthingsiot_lightbulbs

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class MainActivity : BaseMainActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private var mLightState01: Boolean = false
    private var mLightState02: Boolean = false
    private var mLightState03: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_main)
        initActivity()
    }

    override fun initUiElements() {
        super.initUiElements()

        val containerRedLight = findViewById(R.id.wrapper_red_light)
        containerRedLight.setOnClickListener {
            mRefLightRed.let { it1 -> toggleLightState(it1, mLightState01) }
        }
        val containerGreenLight = findViewById(R.id.wrapper_green_light)
        containerGreenLight.setOnClickListener {
            mRefLightGreen.let { it1 -> toggleLightState(it1, mLightState02) }
        }
        val containerBlueLight = findViewById(R.id.wrapper_blue_light)
        containerBlueLight.setOnClickListener {
            mRefLightBlue.let { it1 -> toggleLightState(it1, mLightState03) }
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

    private fun toggleLightState(databaseReference: DatabaseReference, state: Boolean) {
        databaseReference.setValue(if (state) VALUE_OFF else VALUE_ON)
    }

    // Event Listeners

    private val mRedLightEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            mLightState01 = TextUtils.equals(VALUE_ON, dataSnapshot.value as CharSequence?)
            bindViewData(KEY_LIGHT_RED, mLightState01)
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
            mLightState02 = TextUtils.equals(VALUE_ON, dataSnapshot.value as CharSequence?)
            bindViewData(KEY_LIGHT_GREEN, mLightState02)
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
            mLightState03 = TextUtils.equals(VALUE_ON, dataSnapshot.value as CharSequence?)
            bindViewData(KEY_LIGHT_BLUE, mLightState03)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", databaseError.toException())
        }
    }
}
