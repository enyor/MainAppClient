package com.banticsoftware.mainapp.mainapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pb.*
import org.json.JSONArray
import java.text.SimpleDateFormat

/**
 * Created by gedica on 29/03/2018.
 */

class Info_reserva : Activity() {

    var texto = ""
    lateinit var fromPosition: LatLng
    lateinit var toPosition: LatLng
    var nombre = ""
    var especialidad = ""
    var hora = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pb)
        val bundle = intent.getParcelableExtra<Bundle>("bundle")
        fromPosition = bundle.getParcelable<LatLng>("from_position")
        toPosition = bundle.getParcelable<LatLng>("to_position")
        nombre = intent.getStringExtra("nombre")
        especialidad = intent.getStringExtra("especialidad")
        hora = intent.getStringExtra("hora")



       // Redimensiona ventana
       // val dm = DisplayMetrics()
       // windowManager.defaultDisplay.getMetrics(dm)

       // val width = dm.widthPixels
       // val height = dm.heightPixels

        //window.setLayout((width * .8).toInt(), (height * .75).toInt())





        //Nombre.setText(nombre)


        btnavegar.isEnabled = true
        btnavegar.setOnClickListener{onclicreservar()}

        msg.setText("Haz reservado una cita con "+nombre.capitalize()+" por el servicio "+especialidad+" Hora: "+hora)



        //Cargamos los horarios segun el calculo de tiempo
        val time = java.util.Calendar.getInstance().time
        val sdf = SimpleDateFormat("HH:mm:ss")
        val hora = sdf.format(time)







    }



    private fun onclicreservar(){
        val url = getRequestUrl(fromPosition, toPosition)


        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("fromPosition", fromPosition)
        intent.putExtra("toPosition", toPosition)
        //startActivity(intent)
        setResult(RESULT_OK, intent)
        finish()
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

    private fun getRequestUrl2(origin: LatLng, dest: LatLng): String {
        val urls = ("http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + dest.latitude + "," + dest.longitude
                + "&sensor=false&units=metric&mode=driving")
        return urls
    }





}