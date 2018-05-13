package com.example.rem.arcontrolpanel
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.bluetooth.*
import android.util.Log
import java.io.IOException
import java.util.*


class ServerThread : Thread() {
    lateinit var serverSock: BluetoothServerSocket
    var myname: String = "arcontrolpanel"
    var error: Int = 0

    override fun run() {
        lateinit var socket: BluetoothSocket
        var tmp: BluetoothServerSocket? = null
        try {
            tmp = model.bluetooth.listenUsingRfcommWithServiceRecord(myname, model.uuid)
        } catch (e: IOException) {
            Log.e("Server( get socket ): ", e.getLocalizedMessage())
            error = 1
        }
        if(error==0) {
            serverSock = tmp!!


            try {
                socket = serverSock.accept()
                // посылаем соккет в отдельный поток для обработки
                var manageThread = ManageConnectServThread(socket)
                manageThread.start()
            } catch (e: IOException) {
                Log.e("Server( accept ):", e.getLocalizedMessage())
                serverSock.close()
            }

        }
    }

    fun cancel() {
        try {
            serverSock!!.close()
        } catch (e: IOException) { }

    }
}

