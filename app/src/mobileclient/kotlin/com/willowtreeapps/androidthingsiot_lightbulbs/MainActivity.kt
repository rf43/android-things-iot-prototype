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

    override fun initUiElements() {
        super.initUiElements()

        val containerRedLight = findViewById(R.id.wrapper_red_light)
        containerRedLight.setOnClickListener {
            addSingleEventListener(mRefLightRed)
        }
        val containerGreenLight = findViewById(R.id.wrapper_green_light)
        containerGreenLight.setOnClickListener {
            addSingleEventListener(mRefLightGreen)
        }
        val containerBlueLight = findViewById(R.id.wrapper_blue_light)
        containerBlueLight.setOnClickListener {
            addSingleEventListener(mRefLightBlue)
        }
    }

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
