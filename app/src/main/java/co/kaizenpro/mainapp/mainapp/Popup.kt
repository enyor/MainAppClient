package co.kaizenpro.mainapp.mainapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pop.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import org.json.JSONArray
import java.text.SimpleDateFormat




class Popup : Activity() {

    var texto = ""
    lateinit var fromPosition: LatLng
    lateinit var toPosition: LatLng
     var nombre = ""
     var especialidad = ""
     var hora = ""
    var UserId = 0
    var UserName = ""
    var sala = ""
    var dir = ""
    private val database: FirebaseDatabase? = null
    private val databaseReference: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop2)
        val bundle = intent.getParcelableExtra<Bundle>("bundle")
        fromPosition = bundle.getParcelable<LatLng>("from_position")
        toPosition = bundle.getParcelable<LatLng>("to_position")
         nombre = intent.getStringExtra("nombre")
         especialidad = intent.getStringExtra("especialidad")
        val enable = intent.getStringExtra("enable")
        UserId = intent.getIntExtra("Id",0)
        val duracion = intent.getIntExtra("duracion",0)
        val limite = intent.getIntExtra("limite",60)
        UserName = intent.getStringExtra("UserName")
        val Uid = intent.getIntExtra("Uid",0)
        val Tid = intent.getIntExtra("Tid",0)
        val img = intent.getStringExtra("img")
        dir = intent.getStringExtra("dir")
        sala = Uid.toString()+"@"+Tid.toString()


        rdbOne.visibility= View.INVISIBLE
        rdbTwo.visibility= View.INVISIBLE
        rdbThree.visibility = View.INVISIBLE

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * .8).toInt(), (height * .75).toInt())

        val imageUri = "https://mainapp.kaizenpro.co.uk/assets/"+img

        Glide.with(this@Popup)
                .load(Uri.parse(imageUri)) // add your image url
                .override(80,80)
                .centerCrop()
                .transform(CircleTransform(this@Popup)) // applying the image transformer
                .into(avatar)


        //Picasso.with(this@Popup).load(imageUri).resize(100,100).centerInside().into(avatar)
        //Picasso.with(this@Popup).load(imageUri).transform(CircleTransform()).into(avatar)

        Nombre.setText(nombre)
        Especialidad.setText(especialidad)

        btnavegar.isEnabled = false
        btnavegar.setOnClickListener{onclicreservar()}


        //Cargamos los horarios segun el calculo de tiempo
        val time = java.util.Calendar.getInstance().time
        val sdf = SimpleDateFormat("HH:mm:ss")
        val hora = sdf.format(time)


        var URL ="https://mainapp.kaizenpro.co.uk/consulta_horarios.php?id="+UserId+"&duration="+duracion+"&hora="+hora+"&limite="+limite
        // consulta Ws para ultima posicion del trader
        URL.httpGet().responseString { request, response, result ->
            //do something with response
            result.success {

                val op = JSONArray(it)

                if(op.length()==0){
                    Toast.makeText(this@Popup,"No hay horarios disponibles",Toast.LENGTH_LONG).show()
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
        var database = FirebaseDatabase.getInstance();
        var reserva = "reserv"+"@"+UserId.toString()

        var databaseReference = database.getReference(reserva);//Sala de chat (nombre)

        //Creando la reserva en BD Online
        databaseReference.push().setValue(ReservaEnviar("", UserName, "", "1",sala, hora))


        //Pantalla de Felicitación

        val intents = Intent(this@Popup, Info_reserva::class.java)
       // intent.putExtra("bundle", args)
        //intent.putExtra("duracion", duracion_recorrido.toInt())
       // intent.putExtra(" Id", (n as Trader).id)
        val args = Bundle()
        args.putParcelable("from_position", fromPosition)
        args.putParcelable("to_position", toPosition)




        intents.putExtra("nombre", nombre)
        intents.putExtra("especialidad",  especialidad)
        intents.putExtra("hora",  hora)
        intents.putExtra("dir", dir)
       // intent.putExtra("enable", (n as Trader).enable)
       // intent.putExtra("limite", (n as Trader).tespera)
        setResult(RESULT_OK, intent)
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

    private fun getRequestUrl2(origin: LatLng, dest: LatLng): String {
        val urls = ("https://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + dest.latitude + "," + dest.longitude
                + "&sensor=false&units=metric&mode=driving")
        return urls
    }





}