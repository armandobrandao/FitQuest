package com.example.fitquest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request

class WorkoutAPI {
    companion object {
        fun makeApiRequest(muscle: String?, type: String?, difficulty: String?, duration: String, offset: Int = 0): String = runBlocking(Dispatchers.IO) {
            val apiKey = "Tng/CZGSkgzfSCEV+DbTyw==fsY8JbSZOC11ObD4"
            val apiBaseUrl = "https://api.api-ninjas.com/v1/exercises"
            val apiUrl = buildApiUrl(apiBaseUrl, muscle, type, difficulty, offset)

            val client = OkHttpClient()

            val request = Request.Builder()
                .url(apiUrl)
                .header("X-Api-Key", apiKey)
                .build()

            client.newCall(request).execute().use { response ->
                return@runBlocking if (response.isSuccessful) {
                    response.body?.string() ?: ""
                } else {
                    "Error: ${response.code} ${response.body?.string() ?: ""}"
                }
            }
        }

        private fun buildApiUrl(baseUrl: String, muscle: String?, type: String?, difficulty: String?, offset: Int
        ): String {
            val params = mutableListOf<String>()
            muscle?.let { params.add("muscle=$it") }
            type?.let { params.add("type=$it") }
            difficulty?.let { params.add("difficulty=$it") }
            params.add("offset=$offset")

            val queryString = params.joinToString("&")

            return "$baseUrl?$queryString"
        }
    }
}
