package com.willowtreeapps.androidthingsiot_lightbulbs

import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class MainActivity : BaseMainActivity() {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_main)
        initUiElements()
    }

    /**
     * Initialize the click listener callbacks
     */
    override fun initUiElements() {
        super.initUiElements()

        findViewById(R.id.wrapper_red_light).setOnClickListener {
            addSingleEventListener(mRefLightRed)
        }
        findViewById(R.id.wrapper_green_light).setOnClickListener {
            addSingleEventListener(mRefLightGreen)
        }
        findViewById(R.id.wrapper_blue_light).setOnClickListener {
            addSingleEventListener(mRefLightBlue)
        }
    }

    /**
     * Convenience method that we can pass a database reference into and bind a
     * single, one-time, ValueEventListener to. When this is called, it will
     * automatically set the value in the database to the appropriate value.
     */
    private fun addSingleEventListener(databaseReference: DatabaseReference) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                databaseReference.setValue(
                        if (dataSnapshot?.value != VALUE_OFF) VALUE_OFF else VALUE_ON
                )
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError?.toException())
            }
        })
    }
}
