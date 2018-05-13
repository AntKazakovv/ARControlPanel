package com.example.rem.arcontrolpanel

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView


class ChoiceActivity : AppCompatActivity() {


    lateinit var listPairingDevices: ListView
    var deviceArray: ArrayAdapter<String>? = null
    //var currElemArray: BluetoothDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paire_device)
        listPairingDevices = findViewById(R.id.listView)
    }

    override fun onResume() {
        super.onResume()
        //получаем список сопряженных устройств
        model.mPairedDevices.addAll(model.bluetooth.getBondedDevices().toTypedArray()) // -> BluetoothDevice
        deviceArray = ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1)

        var intent2 = Intent()
        // заполняем адаптер информацией об найденых устройствах
        for( i in model.mPairedDevices!!){
            deviceArray!!.add("${i.getName()} | ${i.getAddress()}")
        }
        listPairingDevices.setAdapter(deviceArray)

        //--------------------------------------------
        listPairingDevices.setOnItemClickListener{parent, v , position, id ->
            //currElemArray = mPairedDevices!![position]
            intent2.putExtra("indexdevice", position)
            setResult(Activity.RESULT_OK, intent2)
            finish()
        }
    }



}