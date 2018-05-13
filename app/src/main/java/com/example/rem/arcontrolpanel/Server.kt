package com.example.rem.arcontrolpanel
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.bluetooth.*
import android.util.Log
import java.io.IOException
import java.util.*


class ServerThread : Thread() {
    var serverSock: BluetoothServerSocket? = null
    var myname: String = "arcontrolpanel"


    override fun run() {
        var socket: BluetoothSocket
        var tmp: BluetoothServerSocket? = null
        try {
            tmp = model.bluetooth.listenUsingRfcommWithServiceRecord(myname, model.uuid)
        } catch (e: IOException) {
            Log.e("Server( get socket ): ", e.getLocalizedMessage())
        }
        serverSock = tmp

        while (true) {
            try {
                socket = serverSock!!.accept()
            } catch (e: IOException) {
                Log.e("Server( accept ):", e.getLocalizedMessage())
                serverSock!!.close()
                break
            }

            if (socket != null) {
                // посылаем соккет в отдельный поток для обработки
                var manageThread = ManageConnectServThread(socket)
                manageThread.start()
            }
        }
    }

    fun cancel() {
        try {
            serverSock!!.close()
        } catch (e: IOException) { }

    }
}

