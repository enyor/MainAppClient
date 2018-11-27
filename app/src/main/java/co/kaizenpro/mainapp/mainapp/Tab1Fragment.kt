package co.kaizenpro.mainapp.mainapp


import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.squareup.picasso.Picasso


import kotlinx.android.synthetic.main.fragment_tab1.*
import kotlinx.android.synthetic.main.fragment_tab1.view.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class Tab1Fragment : Fragment() {
    var texto = ""
    var hora = ""



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_tab1, container, false)


        val time = java.util.Calendar.getInstance().time
        val sdf = SimpleDateFormat("HH:mm:ss")
        val hora = sdf.format(time)


        v.rdbOne.visibility = View.INVISIBLE
        v.rdbTwo.visibility = View.INVISIBLE
        v.rdbThree.visibility = View.INVISIBLE

        val TraderDatos = activity.applicationContext as TraderDatos

        v.direccion.setText("Dirección: "+TraderDatos.getDir())

        Picasso.with(v.context)
                .load(TraderDatos.img)
                .resize(100,100)
                .centerCrop()
                .transform(CircleTransformPicasso())
                //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(v.img)

        v.tnombre.setText(TraderDatos.nombre)
        v.tespecialidad.setText(TraderDatos.especialidad)

        v.btnavegar.isEnabled = false
        v.btnavegar.setOnClickListener{onclicreservar()}

        var URL = "https://mainapp.kaizenpro.co.uk/consulta_horarios.php?id=" + TraderDatos.traderId + "&duration=" + TraderDatos.duracion + "&hora=" + hora + "&limite=" + TraderDatos.limite
        // consulta Ws para ultima posicion del trader
        URL.httpGet().responseString { request, response, result ->
            //do something with response
            result.success {

                val op = JSONArray(it)

                if (op.length() == 0) {
                    //Toast.makeText(activity, "No hay horarios disponibles", Toast.LENGTH_LONG).show()
                   // activity.finish()
                    //Nueva modalidad
                    textView2.setText("En estos momentos, según la distancia y tiempo calculados no se permite hacer una reserva al Trader seleccionado...")
                    textView2.setBackgroundColor(resources.getColor(R.color.rojo))
                    var params = textView2.layoutParams
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    textView2.layoutParams = params
                    }


                if (op.length() >= 1) {
                    v.rdbOne.visibility = View.VISIBLE
                    v.rdbOne.setText(op[0].toString())
                    v.rdbOne.setOnClickListener { onclicrb(op[0].toString(), v) }
                }

                if (op.length() >= 2) {
                    v.rdbTwo.visibility = View.VISIBLE
                    v.rdbTwo.setText(op[1].toString())
                    v.rdbTwo.setOnClickListener { onclicrb(op[1].toString(), v) }
                }

                if (op.length() >= 3) {
                    v.rdbThree.visibility = View.VISIBLE
                    v.rdbThree.setText(op[2].toString())
                    v.rdbThree.setOnClickListener { onclicrb(op[2].toString(), v) }
                }


            }
            result.failure {
                //Toast.makeText(this@Popup, "Error al consultar horarios", Toast.LENGTH_LONG).show()
                 v.tverror.setText("No existen opciones para reservas posibles")
            }

        }
        return v
    }


        private fun onclicrb(boton: String, v: View){

            v.btnavegar.isEnabled = true
            texto = boton
            hora = boton

        }

    private fun onclicreservar(){
        val TraderDatos = activity.applicationContext as TraderDatos
        val sala = TraderDatos.userId+"@"+TraderDatos.traderId


        val url = getRequestUrl(TraderDatos.getFrompos(), TraderDatos.getTopos())


        val intent = Intent(activity, MapsDirActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("fromPosition", TraderDatos.getFrompos())
        intent.putExtra("toPosition", TraderDatos.getTopos())

        //startActivity(intent)
        // setResult(RESULT_OK, intent)
        // finish()
        var database = FirebaseDatabase.getInstance();
        var reserva = "reserv"+"@"+TraderDatos.traderId

        var databaseReference = database.getReference(reserva);//Sala de chat (nombre)


        //Creando la reserva en BD Online
        databaseReference.push().setValue(ReservaEnviar("", TraderDatos.userName, "", "1",sala, hora)) //ServerValue.TIMESTAMP


        var URL ="https://mainapp.kaizenpro.co.uk/crear_reserva.php?trader="+TraderDatos.traderId+"&client="+TraderDatos.userId+"&nombre="+TraderDatos.UserName+"&hora_reserva="+hora
        // consulta Ws para ultima posicion del trader
        URL.httpGet().responseObject(Response.Deserializer()) { request, response, result ->
            val (responses, err) = result

            result.success {

                if (responses!![0].code == 1) {

                } else {

                }


            }


        }

        //Pantalla de Felicitación

        val intents = Intent(activity, Info_reserva::class.java)
        // intent.putExtra("bundle", args)
        //intent.putExtra("duracion", duracion_recorrido.toInt())
        // intent.putExtra(" Id", (n as Trader).id)
        val args = Bundle()
        args.putParcelable("from_position", TraderDatos.getFrompos())
        args.putParcelable("to_position", TraderDatos.getTopos())




        intents.putExtra("nombre", TraderDatos.getNombre())
        intents.putExtra("especialidad",  TraderDatos.getEspecialidad())
        intents.putExtra("hora",  hora)
        intents.putExtra("dir", TraderDatos.getDir())

        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
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











}// Required empty public constructor
