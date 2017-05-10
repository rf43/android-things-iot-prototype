package net.rf43.androidthingsiot_lightbulbs

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var mRedLightImage: ImageView
    private lateinit var mGreenLightImage: ImageView
    private lateinit var mBlueLightImage: ImageView

    private lateinit var mRefLightRed: DatabaseReference
    private lateinit var mRefLightGreen: DatabaseReference
    private lateinit var mRefLightBlue: DatabaseReference

    private var mLightState01: Boolean = false
    private var mLightState02: Boolean = false
    private var mLightState03: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_main)

        initFirebaseRefs()
        initUiElements()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRefLightRed.removeEventListener(mRedLightEventListener)
        mRefLightGreen.removeEventListener(mBlueLightEventListener)
        mRefLightBlue.removeEventListener(mGreenLightEventListener)
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

    private fun initEventListeners() {
        mRefLightRed.addValueEventListener(mRedLightEventListener)
        mRefLightGreen.addValueEventListener(mGreenLightEventListener)
        mRefLightBlue.addValueEventListener(mBlueLightEventListener)
    }

    private fun toggleLightState(databaseReference: DatabaseReference, state: Boolean) {
        databaseReference.setValue(if (state) VALUE_OFF else VALUE_ON)
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

    // Event Listeners

    private val mRedLightEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            mLightState01 = TextUtils.equals(VALUE_ON, dataSnapshot.getValue(String::class.java))
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
            mLightState02 = TextUtils.equals(VALUE_ON, dataSnapshot.getValue(String::class.java))
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
            mLightState03 = TextUtils.equals(VALUE_ON, dataSnapshot.getValue(String::class.java))
            bindViewData(KEY_LIGHT_BLUE, mLightState03)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", databaseError.toException())
        }
    }
}
