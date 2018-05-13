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
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

//var bluetooth = BluetoothAdapter.getDefaultAdapter()
//var uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
val model = Model()

class MainActivity : AppCompatActivity() {

    //global variables
    var currIndex = 0
    var serv = ServerThread()
    lateinit var client: ClientThread



    enum class ControlElements(var id: Int){
        Stop(3),
        Record(1),
        Play(2),
        Backward(12),
        Forwards(13),
        But1(6),
        But2(7),
        But3(10),
        But4(11),
        Left(4),
        Right(5),
        Plus(8),
        Minus(9),
        Triger(14)
    }

    //var idToView = HashMap<Int,Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //listPairingDevices = findViewById(R.id.listView)
        // <?>
        model.mPairedDevices.addAll(model.bluetooth.getBondedDevices().toTypedArray()) // -> BluetoothDevices

        model.idToView = hashMapOf(ControlElements.But1.id   to   (findViewById<Button>(R.id.button8)),
                             ControlElements.But2.id   to   (findViewById<Button>(R.id.button11)),
                             ControlElements.But3.id   to   (findViewById<Button>(R.id.button10)),
                             ControlElements.But4.id   to   (findViewById<Button>(R.id.button12)),
                             ControlElements.Left.id   to   (findViewById<Button>(R.id.button6)),
                             ControlElements.Right.id  to   (findViewById<Button>(R.id.button7)),
                             ControlElements.Record.id to   (findViewById<Button>(R.id.button3)),
                             ControlElements.Play.id   to   (findViewById<Button>(R.id.button)),
                             ControlElements.Backward.id   to   (findViewById<Button>(R.id.button4)),
                             ControlElements.Forwards.id   to   (findViewById<Button>(R.id.button5)),
                             ControlElements.Stop.id   to   (findViewById<Button>(R.id.button2)),
                             ControlElements.Plus.id   to   (findViewById<Button>(R.id.button9)),
                             ControlElements.Minus.id   to   (findViewById<Button>(R.id.button13))
        )
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()

        serv.cancel()
        try{
            client.cancel()
        }catch( e: Exception){}
        EventBus.getDefault().unregister(this)
    }


    //------------------- СОбытия для кнопок------------------------
    fun backwardBut(view: View){
        EventBus.getDefault().post(ControlElements.Backward)
    }
    fun forwardsBut(view: View){
        EventBus.getDefault().post(ControlElements.Backward)
    }
    fun recBut(view: View){
        EventBus.getDefault().post(ControlElements.Record)
    }
    fun playBut(view: View){
        EventBus.getDefault().post(ControlElements.Play)
    }
    fun stopBut(view: View){
        EventBus.getDefault().post( ControlElements.Stop)
    }
    fun leftBut(view: View){
        EventBus.getDefault().post(ControlElements.Left)
    }
    fun rightBut(view: View){
        EventBus.getDefault().post(ControlElements.Right)
    }
    fun oneNumBut(view: View){
        EventBus.getDefault().post(ControlElements.But1)
    }
    fun twoNumBut(view: View){
        EventBus.getDefault().post(ControlElements.But2)
    }
    fun plusBut(view: View){
        EventBus.getDefault().post(ControlElements.Plus)
    }
    fun minusBut(view: View){
        EventBus.getDefault().post(ControlElements.Minus)
    }
    fun threeNumBut(view: View){
        EventBus.getDefault().post(ControlElements.But3)
    }
    fun fourNumBut(view: View){
        EventBus.getDefault().post(ControlElements.But4)
    }
    //-------------------------------------------------------------

    fun goToMenuPairedDiveces(view: View){
        val intent = Intent(this@MainActivity, ChoiceActivity::class.java)
        startActivityForResult(intent, 0)
    }

    override fun onResume() {
        super.onResume()
        //Спрашиваем, врубил ли юзер блютуз
        if(model.bluetooth.isEnabled()){
        }
        else {
            //если нет, предлагаем врубить
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }
        serv.start()
    }

    // ловим и обрабатываем ответку от ChoiceActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                if(data!= null){
                    currIndex = data.getIntExtra("indexdevice", -1)
                    model.currentDevice = model.mPairedDevices[currIndex]
                    client = ClientThread(model.currentDevice!!)
                    client.start()
                    EventBus.getDefault().post(ControlElements.Triger.id) //отправляем специальный байт перед работой, что бы получить начальные состояния всех светодиодов
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

class Model{
    var bluetooth: BluetoothAdapter
        get() {return bluetooth}
        set(blue){}

    var uuid: UUID
        get(){return uuid}
        set(uuid){}

    var currentDevice: BluetoothDevice
        get(){return currentDevice}
        set(curDev){}

    var mPairedDevices = arrayListOf<BluetoothDevice>()

    var idToView = HashMap<Int,Button>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ByteArray) {
        var i = 1
        //смотрим что пришло по BT и устанавливаем значния светодиодов кнопкам
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

}