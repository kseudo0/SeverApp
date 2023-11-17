package com.example.severapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

class MainActivity : AppCompatActivity() {
    lateinit var socket: Socket;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serverSocket = ServerSocket(2345)
        startServer(serverSocket)
        val btnClick = findViewById<Button>(R.id.btnSend)

        btnClick.setOnClickListener {
            val message: String = "Hello From Server \n"
            try {
                GlobalScope.launch {
                    if (socket != null) {
                        sendMessage(socket, message)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startServer(serverSocket: ServerSocket) {
        var onlyOne: Boolean = true;
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                println("Server is running")
                socket = serverSocket.accept()
                println("Connected with clients")
                if (onlyOne) {
                    handleClient(socket)
                    onlyOne = false
                }
            }
        }
    }

    private suspend fun handleClient(serverSocket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val inputReader = BufferedReader(InputStreamReader(serverSocket.getInputStream()))
                val outputStream: OutputStream = serverSocket.getOutputStream()
                val receivedData = inputReader.readLine()
                serverSocket.close()
                val tvMessage = findViewById<TextView>(R.id.tvMessage)

                runOnUiThread {
                    val receivedText = "Received message: $receivedData"
                    tvMessage.text = receivedText

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private suspend fun sendMessage(serverSocket: Socket, message: String) {
        try {
            val outputStream: OutputStream = serverSocket.getOutputStream()
            outputStream.write(message.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}