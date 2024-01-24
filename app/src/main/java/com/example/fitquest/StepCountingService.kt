package com.example.fitquest

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StepCountingService : Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelSensor: Sensor? = null
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var threshold = 5f
    private var dynamicThreshold = 0f
    private var alpha = 0.2f

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
        Toast.makeText(this, "Serviço encerrado", Toast.LENGTH_SHORT).show()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val user = auth.currentUser
        if (user != null) {
            val deltaX = Math.abs(event?.values?.get(0) ?: (0f - lastX))
            val deltaY = Math.abs(event?.values?.get(1) ?: (0f - lastY))
            val deltaZ = Math.abs(event?.values?.get(2) ?: (0f - lastZ))

            dynamicThreshold = (deltaX + deltaY + deltaZ) / 3f
            dynamicThreshold = alpha * dynamicThreshold + (1 - alpha) * threshold

            if (deltaX > dynamicThreshold || deltaY > dynamicThreshold || deltaZ > dynamicThreshold) {
                // Incrementa a contagem de passos no Firestore
                incrementStepCountInFirestore()
            }

            lastX = event?.values?.get(0) ?: 0f
            lastY = event?.values?.get(1) ?: 0f
            lastZ = event?.values?.get(2) ?: 0f
        }
    }

    private fun incrementStepCountInFirestore() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                        if (userProfile != null) {
                            Log.d("StepCountingService", "+++++++++++++++ Steps +++++++++++++++")
                            userProfile.steps++
                            firestore.collection("users")
                                .document(user.uid)
                                .set(userProfile)
                        }
                    }
                }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
