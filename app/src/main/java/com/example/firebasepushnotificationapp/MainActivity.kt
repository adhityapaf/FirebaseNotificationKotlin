package com.example.firebasepushnotificationapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebasepushnotificationapp.databinding.ActivityMainBinding
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var activityMainBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        FirebaseService.sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete) {
                FirebaseService.token = it.result
                activityMainBinding.edtToken.setText(it.result)
            }

        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        with(activityMainBinding) {
            sendButton.setOnClickListener {
                val title = edtTitle.text.toString()
                val message = edtMessage.text.toString()
                val receipientToken = edtToken.text.toString()
                if (title.isNotEmpty() && message.isNotEmpty()) {
                    PushNotification(
                        NotificationData(title, message),
                        receipientToken
                    ).also {
                        sendNotification(it)
                    }
                }
            }
        }

    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString(), )
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString() )
        }
    }
}