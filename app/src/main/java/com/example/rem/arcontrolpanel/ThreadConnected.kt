package com.example.rem.arcontrolpanel

import android.bluetooth.BluetoothSocket
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.InputStream
import java.io.OutputStream

class ThreadConnected : Thread {
    var connectedInpStream: InputStream? = null
    var connectedOutStream: OutputStream? = null

    constructor(bsocket: BluetoothSocket){

        var inp: InputStream? = null
        var out: OutputStream? = null

        try {
            inp = bsocket.getInputStream();
            out = bsocket.getOutputStream();
        }catch (e: Exception){
            e.printStackTrace()
        }

        connectedInpStream = inp
        connectedOutStream = out
    }

    override fun run(){
        while(true){
            val buffer = ByteArray(4) // [ff, ss]  ff, ss -- 0 <= num <= 99
            // --- код чтения и обработки входящих данных ---
        }
    }

    public fun write(data: Int){
        try{
            connectedOutStream!!.write(data)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}