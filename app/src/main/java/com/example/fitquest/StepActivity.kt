package com.example.fitquest

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class StepActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var lastTimestamp: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var threshold = 5f  // Ajuste conforme necessário
    private var dynamicThreshold = 0f
    private var alpha = 0.2f  // Fator de suavização para o filtro de média móvel

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var textViewStepCount: TextView
    private var accelSensor: Sensor? = null

    private val updateInterval = 5000L  // Intervalo de atualização em milissegundos (por exemplo, 5 segundos)
    private var lastUpdateTimestamp = 0L
    private val updatesQueue = mutableListOf<UserProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        // Find the TextView after setContentView
        textViewStepCount = findViewById(R.id.textViewStepCount)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        val user = auth.currentUser
        if (user != null) {
            running = true

            if (accelSensor == null) {
                Toast.makeText(this, "Sensor de aceleração não detectado", Toast.LENGTH_SHORT).show()
            } else {
                sensorManager?.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        } else {
            // Usuário não está logado, desregistre o listener do sensor
            sensorManager?.unregisterListener(this)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val user = auth.currentUser
        if (user != null) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastTimestamp > 500) {
                val deltaX = Math.abs(event?.values?.get(0) ?: (0f - lastX))
                val deltaY = Math.abs(event?.values?.get(1) ?: (0f - lastY))
                val deltaZ = Math.abs(event?.values?.get(2) ?: (0f - lastZ))

                // Calibração Dinâmica
                dynamicThreshold = (deltaX + deltaY + deltaZ) / 3f

                // Filtro de Média Móvel
                dynamicThreshold = alpha * dynamicThreshold + (1 - alpha) * threshold

                if (deltaX > dynamicThreshold || deltaY > dynamicThreshold || deltaZ > dynamicThreshold) {
                    totalSteps++

                    if (updatesQueue.isEmpty()) {
                        // Adicione a primeira atualização à fila local
                        updatesQueue.add(UserProfile(steps = 1))
                    } else {
                        // Incrementa a contagem na última atualização na fila
                        updatesQueue.last().steps++
                    }

                    textViewStepCount.text = totalSteps.toString()
                    lastTimestamp = currentTime

                    // Verifique se é hora de enviar as atualizações para o Firestore
                    if (currentTime - lastUpdateTimestamp > updateInterval) {
                        sendUpdatesToFirestore()
                    }
                }

                lastX = event?.values?.get(0) ?: 0f
                lastY = event?.values?.get(1) ?: 0f
                lastZ = event?.values?.get(2) ?: 0f
            }
        }
    }

    private fun sendUpdatesToFirestore() {
        val user = auth.currentUser
        if (user != null && updatesQueue.isNotEmpty()) {
            firestore.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                        if (userProfile != null) {
                            // Atualiza userProfile com os dados da fila local
                            for (update in updatesQueue) {
                                Log.d("StepActivity", "+++++++++++++++ Steps +++++++++++++++")
                                userProfile.steps += update.steps
                            }

                            // Envie a atualização para o Firestore
                            firestore.collection("users")
                                .document(user.uid)
                                .set(userProfile)

                            // Limpe a fila local
                            updatesQueue.clear()

                            // Atualize o timestamp da última atualização
                            lastUpdateTimestamp = System.currentTimeMillis()
                        }
                    }
                }
        }
    }
}
