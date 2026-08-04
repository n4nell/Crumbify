package com.example.crumbify.helper

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

class MultipartRequest {
    fun upload(url: String, params: Map<String, String>, file: File): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        // Kirim Parameter Teks
        for ((key, value) in params) {
            requestBodyBuilder.addFormDataPart(key, value)
        }

        // Kirim Gambar dengan Type yang Benar
        val fileBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        requestBodyBuilder.addFormDataPart("image", file.name, fileBody)

        val request = Request.Builder()
            .url(url)
            .post(requestBodyBuilder.build())
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                // Ini akan membantu kita melihat apa isi error 500-nya
                Log.e("SERVER_RAW_ERROR", body)
                return "{\"status\":false, \"message\":\"Server Error: ${response.code}\"}"
            }
            return body
        }
    }
}