package com.willowtreeapps.androidthingsiot_lightbulbs

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import com.google.firebase.database.*

abstract class BaseMainActivity : AppCompatActivity() {

    private val TAG = BaseMainActivity::class.java.simpleName

    lateinit var mRedLightImage: ImageView
    lateinit var mGreenLightImage: ImageView
    lateinit var mBlueLightImage: ImageView

    lateinit var mRefBaseState: DatabaseReference
    lateinit var mRefLightRed: DatabaseReference
    lateinit var mRefLightGreen: DatabaseReference
    lateinit var mRefLightBlue: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebaseRefs()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRefBaseState.removeEventListener(baseRefValueListener)
    }

    open fun initUiElements() {
        mRedLightImage = findViewById(R.id.image_view_red_light) as ImageView
        mGreenLightImage = findViewById(R.id.image_view_green_light) as ImageView
        mBlueLightImage = findViewById(R.id.image_view_blue_light) as ImageView
    }

    private fun initFirebaseRefs() {
        val database = FirebaseDatabase.getInstance()

        mRefBaseState = database.reference.child(KEY_LIGHT_STATE)
        mRefBaseState.addValueEventListener(baseRefValueListener)

        mRefLightRed = mRefBaseState.child(KEY_LIGHT_RED).child(KEY_STATE)
        mRefLightGreen = mRefBaseState.child(KEY_LIGHT_GREEN).child(KEY_STATE)
        mRefLightBlue = mRefBaseState.child(KEY_LIGHT_BLUE).child(KEY_STATE)
    }

    private val baseRefValueListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot?) {
            val state = LightBulbState()

            dataSnapshot?.children?.forEach({
                        val dv = it.value as Map<*, *>
                        when(it.key) {
                            KEY_LIGHT_RED ->
                                state.redBulbState = dv[KEY_STATE] != VALUE_OFF

                            KEY_LIGHT_GREEN ->
                                state.greenBulbState = dv[KEY_STATE] != VALUE_OFF

                            KEY_LIGHT_BLUE ->
                                state.blueBulbState = dv[KEY_STATE] != VALUE_OFF
                        }
                    })

            bindViewData(state)
        }

        override fun onCancelled(databaseError: DatabaseError?) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", databaseError?.toException())
        }
    }

    private fun bindViewData(state: LightBulbState) {
        val greyBulb = R.drawable.grey_lt_bulb

        mRedLightImage.setImageDrawable(
                getDrawable(
                        if (state.redBulbState) R.drawable.red_lt_bulb else greyBulb
                )
        )
        mGreenLightImage.setImageDrawable(
                getDrawable(
                        if (state.greenBulbState) R.drawable.grn_lt_bulb else greyBulb
                )
        )
        mBlueLightImage.setImageDrawable(
                getDrawable(
                        if (state.blueBulbState) R.drawable.blue_lt_bulb else greyBulb
                )
        )
    }
}
