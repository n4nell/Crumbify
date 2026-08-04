package com.example.crumbify

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

class RequestHandler {

    fun sendPostRequest(requestURL: String, postDataParams: HashMap<String, String>): String {
        val sb = StringBuilder()
        try {
            val url = URL(requestURL)
            val conn = url.openConnection() as HttpURLConnection

            conn.readTimeout = 15000
            conn.connectTimeout = 15000
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true

            val os: OutputStream = conn.outputStream
            BufferedWriter(OutputStreamWriter(os, "UTF-8")).use { writer ->
                writer.write(getPostDataString(postDataParams))
                writer.flush()
            }
            os.close()

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
                    var response: String?
                    while (br.readLine().also { response = it } != null) {
                        sb.append(response)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    fun sendGetRequest(requestURL: String): String {
        val sb = StringBuilder()
        try {
            val url = URL(requestURL)
            val conn = url.openConnection() as HttpURLConnection
            BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    sb.append(line).append("\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    fun sendGetRequestParam(requestURL: String, id: String): String {
        return sendGetRequest("$requestURL$id")
    }

    private fun getPostDataString(params: HashMap<String, String>): String {
        val result = StringBuilder()
        var first = true
        for ((key, value) in params) {
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }
        return result.toString()
    }
}