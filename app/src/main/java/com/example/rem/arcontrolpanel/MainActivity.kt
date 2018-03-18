package com.example.rem.arcontrolpanel

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

var bluetooth = BluetoothAdapter.getDefaultAdapter()
var uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

class MainActivity : AppCompatActivity() {

    //global variables
    var mPairedDevices : Array<BluetoothDevice>? = null
    var listPairingDevices: ListView? = null
    var currIndex = 0
    var currentDevice: BluetoothDevice? = null
    var serv: ServerThread? = null
    var client: ClientThread? = null
    var statusButtons: ArrayList<Int>? = null

    var idToView = HashMap<Int,Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listPairingDevices = findViewById(R.id.listView)
        mPairedDevices = bluetooth.getBondedDevices().toTypedArray() // -> BluetoothDevice
        var i = 0
        while(i < 13){
            statusButtons!!.add(0)
        }
        idToView = hashMapOf(6 to (findViewById<Button>(R.id.button8)),
                             7 to (findViewById<Button>(R.id.button11)),
                             10 to (findViewById<Button>(R.id.button10)),
                             11 to (findViewById<Button>(R.id.button12)),
                             4 to (findViewById<Button>(R.id.button6)),
                             5 to (findViewById<Button>(R.id.button7)),
                             1 to (findViewById<Button>(R.id.button3)),
                             2 to (findViewById<Button>(R.id.button)))

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(serv != null) {
            serv!!.cancel()
            client!!.cancel()
        }
        EventBus.getDefault().unregister(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ByteArray) {
        var i = 1
        while(i<event.size) {
            if(event[i].toInt() == 1)
                idToView[event[i-1].toInt()]!!.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN))
            if(event[i].toInt() == 2)
                idToView[event[i-1].toInt()]!!.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW))
            if(event[i].toInt() == 3)
                idToView[event[i-1].toInt()]!!.setBackgroundTintList(ColorStateList.valueOf(Color.RED))
            i+=1
        }
    }

    //------------------- СОбытия для кнопок------------------------
    fun recBut(view: View){
        EventBus.getDefault().post(1)
    }

    fun playBut(view: View){
        EventBus.getDefault().post(2)
    }

    fun stopBut(view: View){
        EventBus.getDefault().post(3)
    }

    fun leftBut(view: View){
        EventBus.getDefault().post(4)
    }

    fun rightBut(view: View){
        EventBus.getDefault().post(5)
    }

    fun oneNumBut(view: View){
        EventBus.getDefault().post(6)
    }

    fun twoNumBut(view: View){
        EventBus.getDefault().post(7)
    }

    fun plusBut(view: View){
        EventBus.getDefault().post(8)
    }

    fun minusBut(view: View){
        EventBus.getDefault().post(9)
    }

    fun threeNumBut(view: View){
        EventBus.getDefault().post(10)
    }

    fun fourNumBut(view: View){
        EventBus.getDefault().post(11)
    }
    //-------------------------------------------------------------

    fun goToMenuPairedDiveces(view: View){
        val intent = Intent(this@MainActivity, ChoiceActivity::class.java)
        startActivityForResult(intent, 0)
        EventBus.getDefault().post(12) //отправляем специальный байт перед работой, что бы получить начальные состояния всех светодиодов
    }

    override fun onResume() {
        super.onResume()
        //Спрашиваем, врубил ли юзер блютуз
        if(bluetooth.isEnabled()){
        }
        else {
            //если нет, предлагаем врубить
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }

        serv = ServerThread()
        serv!!.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                if(data!= null){
                    currIndex = data.getIntExtra("indexdevice", -1)
                    currentDevice = mPairedDevices!![currIndex]
                    client = ClientThread(currentDevice!!)
                    client!!.start()
                }
                else{
                    println("ERROR: data is null -> (onActivityResult)")
                }
            }
            else{
                println("RETURN_CANCELED -> (onActivityResult)")
            }
        }
    }


}
