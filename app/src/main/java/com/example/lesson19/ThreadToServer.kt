package com.example.lesson19

import android.os.AsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request

class ThreadToServer(
    private val okHttpClient: OkHttpClient,
    private val request: Request,
    val addToTextView:(String) -> Unit
) : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String? {
        return try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                null
            } else response.body?.string()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result!=null){
            addToTextView(result)
        }
    }
}