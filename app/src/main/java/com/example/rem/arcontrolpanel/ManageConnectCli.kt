package com.example.rem.arcontrolpanel

import android.bluetooth.BluetoothSocket
import android.util.Log
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by kant on 10.02.2018.
 */
class ManageConnectCliThread(var sock: BluetoothSocket, var data: Int): Thread() {
    var mOutputStream: OutputStream? = null
    override fun run() {
            try {
                mOutputStream = sock.getOutputStream()
            } catch (e: IOException) {
                Log.e("ManageConnectServ:", e.getLocalizedMessage())
            }

        while(true) {
            try {
                mOutputStream!!.write(data)
            } catch (e: IOException) {
            }
        }

    }
}