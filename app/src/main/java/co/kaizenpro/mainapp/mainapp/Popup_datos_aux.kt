package co.kaizenpro.mainapp.mainapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pop_datos.*


/**
 * Created by gedica on 29/03/2018.
 */

class Popup_datos_aux : Activity() {

    var texto = ""
    var img = ""
    var img_cover = ""
    var dir = ""
    lateinit var fromPosition: LatLng
    lateinit var toPosition: LatLng
    private var UserId = 0


    val URL = "https://mainapp.kaizenpro.co.uk/actualizar_marcador.php";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_datos)
        UserId = intent.getIntExtra("UserId", 0)
        val nombre = intent.getStringExtra("UserName")
        val vedad = intent.getIntExtra("Edad",0)
        val vsexo = intent.getStringExtra("Sexo")
        val vdireccion = intent.getStringExtra("Direccion")

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * .9).toInt(), (height * .85).toInt())





        btactualizar.setOnClickListener{onclicreservar()}

        Nombre.setText(nombre)
        edad.setText(vedad.toString())
        direccion.setText(vdireccion)
        if (vsexo == "M") {
            M.isChecked = true
            F.isChecked = false
        }
        if (vsexo == "F"){
            M.isChecked = false
            F.isChecked = true

        }

        if (nombre!=""){
            btactualizar.visibility = View.GONE
            bteditar.visibility = View.VISIBLE
            Nombre.isEnabled = false
            edad.isEnabled = false
            M.isEnabled = false
            F.isEnabled = false
            direccion.isEnabled = false

        } else {
            btactualizar.visibility = View.VISIBLE
            bteditar.visibility = View.GONE
            Nombre.isEnabled = true
            edad.isEnabled = true
            M.isEnabled = true
            F.isEnabled = true
            direccion.isEnabled = true
        }

        bteditar.setOnClickListener {
            btactualizar.visibility = View.VISIBLE
            bteditar.visibility = View.GONE
            Nombre.isEnabled = true
            edad.isEnabled = true
            M.isEnabled = true
            F.isEnabled = true
            direccion.isEnabled = true
        }


    }


    private fun onclicreservar(){

        var id = UserId


        var nom = Nombre.text.toString()
        var vedad = edad.text


        var sexo = ""
        if (M.isChecked){
            sexo = "M"
        }else{
            sexo = "F"
        }
        var dir = direccion.text.toString()

        if(nom!="" && vedad.toString()!="0" && sexo!="") {

            val intent = Intent(this, MapsActivity::class.java)

            val valores = listOf("id" to id, "Nombre" to nom, "Edad" to vedad, "sexo" to sexo, "dir" to dir)
            Fuel.get("https://mainapp.kaizenpro.co.uk/actualizar_datos_cliente.php", valores).response { request, response, result ->

                val nedad = vedad.toString().toInt()
                result.success {
                    intent.putExtra("nom",nom)
                    intent.putExtra("edad", nedad)
                    intent.putExtra("sexo", sexo)
                    intent.putExtra("direccion", dir)
                    setResult(RESULT_OK, intent)
                    finish()
                }

                result.failure {
                    Toast.makeText(this, "Error:", Toast.LENGTH_LONG).show();

                    setResult(RESULT_CANCELED, intent)
                    finish()
                }

            }

        } else {
            Toast.makeText(this, "Debe completar los campos obligatorios (Nombre, Edad y sexo)", Toast.LENGTH_LONG).show();
        }





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 400){
            if(resultCode == AppCompatActivity.RESULT_OK) {
                var Fn = data!!.getStringExtra("filename")
                img = Fn
                val imageUri = "https://mainapp.kaizenpro.co.uk/assets/"+Fn
                //   Picasso.with(this@Popup_datos).load(imageUri).resize(100,100).centerInside().into(avatar)

                // } else {
                //   val imageUri = "https://spoanet.com/work/bantic/demo/public/images/avatar.png"
                //  Picasso.with(this@Popup_datos).load(imageUri).resize(100,100).centerInside().into(avatar)


            }
        }
        if(requestCode == 500){
            if(resultCode == AppCompatActivity.RESULT_OK) {
                var Fn = data!!.getStringExtra("filename")
                img_cover = Fn
                val imageUri = "https://mainapp.kaizenpro.co.uk/assets/"+Fn
                //   Picasso.with(this@Popup_datos).load(imageUri).resize(100,100).centerInside().into(avatar)




            }
        }
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
        val urls = ("https://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + dest.latitude + "," + dest.longitude
                + "&sensor=false&units=metric&mode=driving")
        return urls
    }





}