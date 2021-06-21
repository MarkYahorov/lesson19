package com.example.lesson19

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private lateinit var thisParam: EditText
    private lateinit var searchBtn: Button
    private lateinit var serverResultText: TextView
    private lateinit var progress: ProgressDialog
    private lateinit var serverThread: ThreadToServer

    private val URL_SERVER = "https://pub.zame-dev.org/senla-training-addition/lesson-19.php?param="
    private val PARAM_OF_URL = "param"
    private val SERVER_TEXT_RESULT = "serverTextResult"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAll()
    }

    private fun initAll() {
        thisParam = findViewById(R.id.param_for_url)
        searchBtn = findViewById(R.id.send_request_btn)
        serverResultText = findViewById(R.id.request_text)
    }

    override fun onStart() {
        super.onStart()
        searchBtnListener()
    }

    private fun searchBtnListener() {
        searchBtn.setOnClickListener {
            createProgressDialog()
            createRequest(thisParam.text.toString())
        }
    }

    private fun createRequest(string: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(URL_SERVER+string)
            .build()

        goToServerFromThread(client, request)
    }

    private fun goToServerFromThread(client: OkHttpClient, request: Request) {
        serverThread = ThreadToServer(client, request) {
            Thread.sleep(5000)
            Handler(Looper.getMainLooper()).post { serverResultText.text = it
            progress.dismiss()}
        }
        serverThread.executeOnExecutor(Executors.newFixedThreadPool(1))
    }

    private fun createProgressDialog() {
        progress = ProgressDialog(this@MainActivity)
        progress.show()
        progress.setContentView(R.layout.progress)
        progress.window?.setBackgroundDrawableResource(R.color.transparent)
        progress.setCanceledOnTouchOutside(false)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PARAM_OF_URL, thisParam.text.toString())
        outState.putString(SERVER_TEXT_RESULT, serverResultText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        thisParam.setText(savedInstanceState.getString(PARAM_OF_URL))
        serverResultText.text  = savedInstanceState.getString(SERVER_TEXT_RESULT)
    }

    override fun onStop() {
        super.onStop()
        searchBtn.setOnClickListener(null)
        serverThread.cancel(true)
    }
}