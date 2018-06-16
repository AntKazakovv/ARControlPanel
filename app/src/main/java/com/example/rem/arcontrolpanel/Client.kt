package com.example.rem.arcontrolpanel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.io.OutputStream
import android.widget.Toast
//import android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread



//var socket: BluetoothSocket? = null


class ClientThread : Thread{
    var socket: BluetoothSocket? = null
    //var blueDev: BluetoothDevice? = null
    var data1: Int? = null
    var connectOK = false
    var arrayThreads = mutableListOf<ThreadConnected>()

    constructor(dev: BluetoothDevice){
        //реcгитрируем приемник
        EventBus.getDefault().register(this)
        try{
            socket = dev.createRfcommSocketToServiceRecord(model.uuid)
        }
        catch (e: Exception){Log.e("Error open socket: ", e.getLocalizedMessage())}
    }

    override fun run() {

        try {
            socket!!.connect()
            connectOK = true
        } catch (e: IOException) {

            Log.e("Client( connect ): ", e.getLocalizedMessage())
            try {
                socket!!.close()
            } catch (e: IOException) {
                Log.e("Client( close socket ):", e.getLocalizedMessage())
                e.printStackTrace()
            }
        }

        if(connectOK){
            var thr = ThreadConnected(socket!!)
            thr.start()
            arrayThreads.add(thr)
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public fun onEvent(event: MainActivity.ControlElements) {
        val data1 = event.id
        for(thr in arrayThreads){
            thr.write(data1)
        }
    }

    fun cancel() {
        try {
            socket!!.close()
            //отвязываем приемник
            EventBus.getDefault().unregister(this)
        } catch (e: IOException) { }
    }
}


