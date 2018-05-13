package com.example.rem.arcontrolpanel

import android.bluetooth.BluetoothSocket
import android.util.Log
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.io.InputStream


class ManageConnectServThread(var sock: BluetoothSocket): Thread() {
    lateinit var mInputStream: InputStream

    override fun run(){
        try{
            mInputStream = sock.getInputStream()
        }catch(e: IOException){ Log.e("ManageConnectServ:", e.getLocalizedMessage())
                                }
        var buffer: ByteArray = ByteArray(1024)
        var bytes: Int

        bytes = mInputStream.read(buffer)
        EventBus.getDefault().post(buffer)


    }
}