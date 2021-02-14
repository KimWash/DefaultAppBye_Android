package com.kimwash.defaultappbye

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket


internal class ServerThread(val context: Context, val handler: Handler) : Thread() {
    var serverSocket: ServerSocket = ServerSocket()
    private val TAG = "ServerThread"
    var isLoop = true
    public fun setIsLoop(isLoop: Boolean) {
        this.isLoop = isLoop
    }

    fun closeConnect(){
        if (serverSocket != null) {
            try {
                serverSocket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun run() {
        Log.d(TAG, "running")

        try {
            serverSocket.bind(InetSocketAddress(9000));
            val socket: Socket = serverSocket.accept()
            val message = Message.obtain()
            val bundle = Bundle()
            bundle.putBoolean("connected", true)
            bundle.putString("ipAddress", socket.remoteSocketAddress.toString())
            message.data = bundle
            handler.sendMessage(message)
            val `is`: InputStream = socket.getInputStream()
            val os: OutputStream = socket.getOutputStream()

            var byteArr = ByteArray(1024)
            var msg: String? = null
            while (isLoop) {
                val readByteCount: Int = `is`.read(byteArr)

                if (readByteCount == -1) throw IOException()

                msg = String(byteArr!!, 0, readByteCount, Charsets.UTF_8)
                println("Data Received OK!")
                println("Message : $msg")


                if (msg == "request List") {
                    val pm = context.packageManager
                    val pkgs = pm.getInstalledPackages(0)

                    var myList = JSONArray()


                    for (info in pkgs) {
                        val packageName = info.packageName
                        val appName =
                            info.applicationInfo.loadLabel(context.packageManager).toString()
                        val jsonObject = JSONObject()

                        jsonObject.put("packageName", packageName)
                        jsonObject.put("appName", appName)
                        myList.put(jsonObject)
                    }
                    msg = myList.toString()
                }

                byteArr = msg.toByteArray(charset("UTF-8"))
                os.write(byteArr)
                os.flush()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            Log.d(TAG, "destory")

            if (serverSocket != null) {
                try {
                    val message = Message.obtain()
                    val bundle = Bundle()
                    bundle.putBoolean("connected", false)
                    message.data = bundle
                    handler.sendMessage(message)

                    serverSocket.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}