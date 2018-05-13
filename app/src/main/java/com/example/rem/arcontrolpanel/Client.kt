package com.example.rem.arcontrolpanel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

//var socket: BluetoothSocket? = null


class ClientThread(var dev: BluetoothDevice): Thread(){
    var socket: BluetoothSocket? = null
    //var blueDev: BluetoothDevice? = null
    var data1: Int? = null

    override fun run(){
        //реcгитрируем приемник
        EventBus.getDefault().register(this)


        var tmp: BluetoothSocket? = null
        try {
            tmp = dev.createRfcommSocketToServiceRecord(model.uuid)
        } catch (e: IOException) {
            Log.e("Client(get socket):", e.getLocalizedMessage())
        }
        socket = tmp

        try{
            socket!!.connect()
        }
        catch(e: IOException){ Log.e("Client( connect ): ", e.getLocalizedMessage())
            try{
                socket!!.close()
            }
            catch(e: IOException){ Log.e("Client( close socket ):", e.getLocalizedMessage()) }
        }




    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEvent(event: MainActivity.ControlElements) {
        data1 = event.id
        if(data1 != null){
            // киаем сокет в  поток для передачи данных
            var manageThread = ManageConnectCliThread(socket!!, data1!!)
            manageThread.start()
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


