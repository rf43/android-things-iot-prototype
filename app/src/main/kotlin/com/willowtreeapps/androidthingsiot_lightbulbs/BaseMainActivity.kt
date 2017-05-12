package com.willowtreeapps.androidthingsiot_lightbulbs

import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

abstract class BaseMainActivity : AppCompatActivity() {

    lateinit var mRedLightImage: ImageView
    lateinit var mGreenLightImage: ImageView
    lateinit var mBlueLightImage: ImageView

    lateinit var mRefLightRed: DatabaseReference
    lateinit var mRefLightGreen: DatabaseReference
    lateinit var mRefLightBlue: DatabaseReference

    protected val mLightBulbState: LightBulbState = LightBulbState()

    override fun onDestroy() {
        super.onDestroy()
        removeEventListeners()
    }

    open fun initUiElements() {
        mRedLightImage = findViewById(R.id.image_view_red_light) as ImageView
        mGreenLightImage = findViewById(R.id.image_view_green_light) as ImageView
        mBlueLightImage = findViewById(R.id.image_view_blue_light) as ImageView
    }

    fun initActivity() {
        initFirebaseRefs()
        initUiElements()
    }

    fun bindViewData(state: LightBulbState) {
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

    private fun initFirebaseRefs() {
        val database = FirebaseDatabase.getInstance()
        val baseRef = database.reference.child(KEY_LIGHT_STATE)

        mRefLightRed = baseRef.child(KEY_LIGHT_RED).child(KEY_STATE)
        mRefLightGreen = baseRef.child(KEY_LIGHT_GREEN).child(KEY_STATE)
        mRefLightBlue = baseRef.child(KEY_LIGHT_BLUE).child(KEY_STATE)

        initEventListeners()
    }

    abstract fun initEventListeners()
    abstract fun removeEventListeners()
}
