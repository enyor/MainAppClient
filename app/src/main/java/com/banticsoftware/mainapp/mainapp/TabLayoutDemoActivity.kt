package com.banticsoftware.mainapp.mainapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pop.*

import kotlinx.android.synthetic.main.activity_tab_layout_demo.*
import org.json.JSONArray
import java.text.SimpleDateFormat

class TabLayoutDemoActivity : AppCompatActivity() {

    var texto = ""
    lateinit var fromPosition: LatLng
    lateinit var toPosition: LatLng
    var nombre = ""
    var especialidad = ""
    var hora = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_layout_demo)


        val bundle = intent.getParcelableExtra<Bundle>("bundle")
        fromPosition = bundle.getParcelable<LatLng>("from_position")
        toPosition = bundle.getParcelable<LatLng>("to_position")
        nombre = intent.getStringExtra("nombre")
        especialidad = intent.getStringExtra("especialidad")
        val enable = intent.getStringExtra("enable")
        val UserId = intent.getIntExtra(" Id",0)
        val duracion = intent.getIntExtra("duracion",0)
        val limite = intent.getIntExtra("limite",60)



      // setSupportActionBar(toolbar)

     //   val bar = actionBar
     //   bar.setDisplayShowHomeEnabled(false)
     //   bar.setDisplayShowTitleEnabled(false)


        //

   /*     fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        } */
        configureTabLayout()


        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * .8).toInt(), (height * .75).toInt())

        val imageUri = "https://spoanet.com/work/bantic/demo/public/images/avatar.png"


        Picasso.with(this@TabLayoutDemoActivity).load(imageUri).resize(100,100).centerInside().into(avatar)
        //Picasso.with(this@Popup).load(imageUri).transform(CircleTransform()).into(avatar)

        Nombre.setText(nombre)
        Especialidad.setText(especialidad)

        btnavegar.isEnabled = false
        btnavegar.setOnClickListener{onclicreservar()}


        //Cargamos los horarios segun el calculo de tiempo
        val time = java.util.Calendar.getInstance().time
        val sdf = SimpleDateFormat("HH:mm:ss")
        val hora = sdf.format(time)


        var URL ="http://mainapp.kaizenpro.co.uk/consulta_horarios.php?id="+UserId+"&duration="+duracion+"&hora="+hora+"&limite="+limite
        // consulta Ws para ultima posicion del trader
        URL.httpGet().responseString { request, response, result ->
            //do something with response
            result.success {

                val op = JSONArray(it)

                if(op.length()==0){
                    Toast.makeText(this@TabLayoutDemoActivity,"No hay horarios disponibles", Toast.LENGTH_LONG).show()
                    finish()
                }


                if (op.length()>=1){
                    rdbOne.visibility= View.VISIBLE
                    rdbOne.setText(op[0].toString())
                    rdbOne.setOnClickListener{onclicrb(op[0].toString())}
                }

                if (op.length()>=2){
                    rdbTwo.visibility= View.VISIBLE
                    rdbTwo.setText(op[1].toString())
                    rdbTwo.setOnClickListener{onclicrb(op[1].toString())}
                }

                if (op.length()>=3){
                    rdbThree.visibility= View.VISIBLE
                    rdbThree.setText(op[2].toString())
                    rdbThree.setOnClickListener{onclicrb(op[2].toString())}
                }


            }
            result.failure {
                //Toast.makeText(this@Popup, "Error al consultar horarios", Toast.LENGTH_LONG).show()
                tverror.setText("No existen opciones para reservas posibles")
            }

        }







    }

    private fun configureTabLayout() {

        tab_layout.addTab(tab_layout.newTab().setText("Tab 1 Item"))
        tab_layout.addTab(tab_layout.newTab().setText("Tab 2 Item"))
        tab_layout.addTab(tab_layout.newTab().setText("Tab 3 Item"))
        tab_layout.addTab(tab_layout.newTab().setText("Tab 4 Item"))

        val adapter = TabPagerAdapter(supportFragmentManager,
                tab_layout.tabCount)
        pager.adapter = adapter

        pager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        tab_layout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })



    }

    private fun onclicrb(boton: String){

        btnavegar.isEnabled = true
        texto = boton
        hora = boton

    }

    private fun onclicreservar(){
        val url = getRequestUrl(fromPosition, toPosition)


        val intent = Intent(this, MapsDirActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("fromPosition", fromPosition)
        intent.putExtra("toPosition", toPosition)
        //startActivity(intent)
        // setResult(RESULT_OK, intent)
        // finish()


        //Pantalla de Felicitación

        val intents = Intent(this@TabLayoutDemoActivity, Info_reserva::class.java)
        // intent.putExtra("bundle", args)
        //intent.putExtra("duracion", duracion_recorrido.toInt())
        // intent.putExtra(" Id", (n as Trader).id)
        val args = Bundle()
        args.putParcelable("from_position", fromPosition)
        args.putParcelable("to_position", toPosition)




        intents.putExtra("nombre", nombre)
        intents.putExtra("especialidad",  especialidad)
        intents.putExtra("hora",  hora)
        // intent.putExtra("enable", (n as Trader).enable)
        // intent.putExtra("limite", (n as Trader).tespera)
        setResult(Activity.RESULT_OK, intent)
        finish()
        intents.putExtra("bundle", args)
        startActivity(intents)
        //
        // startActivityForResult(intents, 100)



    }

    private fun getRequestUrl(origin: LatLng, dest: LatLng): String {
        //Value of origin
        val str_org = "origin=" + origin.latitude + "," + origin.longitude
        //Value of destination
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        //Set value enable the sensor
        val sensor = "sensor=false"
        //Mode for find direction
        val mode = "mode=driving"
        //Build the full param
        val param = "$str_org&$str_dest&$sensor&$mode"
        //Output format
        val output = "json"
        //Create url to request
        return "https://maps.googleapis.com/maps/api/directions/$output?$param"
    }


}