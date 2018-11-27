package co.kaizenpro.mainapp.mainapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import co.kaizenpro.mainapp.mainapp.R.id.cover
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso


import kotlinx.android.synthetic.main.activity_tab_layout_demo.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class TabLayoutDemoActivity : AppCompatActivity() {

    var texto = ""
    lateinit var fromPosition: LatLng
    lateinit var toPosition: LatLng
    var nombre = ""
    var especialidad = ""
    var hora = ""
    var UserName = ""
    var sala = ""
    var dir = ""

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
        UserName = intent.getStringExtra("UserName")
        val Uid = intent.getIntExtra("Uid",0)
        val Tid = intent.getIntExtra("Tid",0)
        val imgt = intent.getStringExtra("img")
        val imgtc = intent.getStringExtra("img_cover")
        dir = intent.getStringExtra("dir")
        sala = Uid.toString()+"@"+Tid.toString()



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

        //comentado para dejar ajuste completo
      //  window.setLayout((width * .9).toInt(), (height * .9).toInt())

        val imageUri = "https://mainapp.kaizenpro.co.uk/assets/"+imgt
//        val imageUriC = "https://mainapp.kaizenpro.co.uk/assets/"+imgtc
        val imageUriC = "https://mainapp.kaizenpro.co.uk/assets/Trader_Cover_"+Tid.toString()+".jpeg"

        //val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        //val v = inflater.inflate(R.layout.custom_info_window, null)

        //val avatar = v.findViewById<ImageView>(R.id.img)

        // Comentado segun cambio semana 1 de septiembre
       // Picasso.with(this@TabLayoutDemoActivity).load(imageUriC).into(cover)

      /*  Picasso.with(this@TabLayoutDemoActivity).load(imageUri).into( object: com.squareup.picasso.Target{

            override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                //cover.background = BitmapDrawable(this@TabLayoutDemoActivity.getResources(), bitmap)
                Log.d("TAG", "Image loaded")
            }

            override fun onBitmapFailed(errorDrawable: Drawable) {
                Log.d("TAG", "FAILED")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                Log.d("TAG", "Prepare Load")
            }
        })
        */



      /*  Picasso.with(this@TabLayoutDemoActivity)
                .load(imageUri)
                .resize(100,100)
                .centerCrop()
                .transform(CircleTransformPicasso())
                //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(img)
*/

      /*  Glide.with(this@TabLayoutDemoActivity)
                .load(Uri.parse(imageUri)) // add your image url
                .override(100,100)
                .centerCrop()
                .transform(CircleTransform(this@TabLayoutDemoActivity)) // applying the image transformer
                .into(img)*/

        //Picasso.with(this@Popup).load(imageUri).transform(CircleTransform()).into(avatar)

      //  tnombre2.setText(nombre)
        //tespecialidad2.setText(especialidad)

//        btnavegar.isEnabled = false
//        btnavegar.setOnClickListener{onclicreservar()}


        //Cargamos los horarios segun el calculo de tiempo
        val time = java.util.Calendar.getInstance().time
        val sdf = SimpleDateFormat("HH:mm:ss")
        val hora = sdf.format(time)




        // calculo de horarios y radibutton



        rdbOne.visibility = View.INVISIBLE
        rdbTwo.visibility = View.INVISIBLE
        rdbThree.visibility = View.INVISIBLE

        val TraderDatos = applicationContext as TraderDatos

        TraderDatos.setImg(imageUri)


        btnavegar.isEnabled = false
        btnavegar.setOnClickListener{onclicreservar()}

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
                        textView2.visibility = View.VISIBLE
                }


                if (op.length() >= 1) {
                    rdbOne.visibility = View.VISIBLE
                    rdbOne.setText(op[0].toString())
                    rdbOne.setOnClickListener { onclicrb(op[0].toString()) }
                }

                if (op.length() >= 2) {
                    rdbTwo.visibility = View.VISIBLE
                    rdbTwo.setText(op[1].toString())
                    rdbTwo.setOnClickListener { onclicrb(op[1].toString()) }
                }

                if (op.length() >= 3) {
                    rdbThree.visibility = View.VISIBLE
                    rdbThree.setText(op[2].toString())
                    rdbThree.setOnClickListener { onclicrb(op[2].toString()) }
                }


            }
            result.failure {
                //Toast.makeText(this@Popup, "Error al consultar horarios", Toast.LENGTH_LONG).show()
                tverror.setText("No existen opciones para reservas posibles")
            }

        }
        return

    }

    private fun configureTabLayout() {

        //tab_layout.addTab(tab_layout.newTab().setText("Contacto"))
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_portafolio))
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_check_grey))
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_mensajeria_grey))
     //   tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_servicios_grey))

        val adapter = TabPagerAdapter(supportFragmentManager,
                tab_layout.tabCount)
        pager.adapter = adapter

        pager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        tab_layout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
               // tab.setIcon(R.drawable.ic_check)
                when (tab.position){
                    0 -> {tab.setIcon(R.drawable.ic_portafolio)}
                    1 -> {tab.setIcon(R.drawable.ic_check)}
                    2 -> {tab.setIcon(R.drawable.ic_mensajeria)}
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                when (tab.position){
                    0 -> {tab.setIcon(R.drawable.ic_portafolio_grey)}
                    1 -> {tab.setIcon(R.drawable.ic_check_grey)}
                    2 -> {tab.setIcon(R.drawable.ic_mensajeria_grey)}
                }

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

        if (UserName != "") {

            val TraderDatos = applicationContext as TraderDatos
            val sala = TraderDatos.userId + "@" + TraderDatos.traderId

            val url = getRequestUrl(fromPosition, toPosition)


            val intent = Intent(this, MapsDirActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("fromPosition", TraderDatos.getFrompos())
            intent.putExtra("toPosition", TraderDatos.getTopos())


            var database = FirebaseDatabase.getInstance();
            var reserva = "reserv" + "@" + TraderDatos.traderId

            var databaseReference = database.getReference(reserva);//Sala de chat (nombre)

            val time = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val fecha = sdf.format(time)
            val fh = fecha + " " + hora
            //Creando la reserva en BD Online
            databaseReference.push().setValue(ReservaEnviar("", TraderDatos.userName, "", "1", sala, fh)) //ServerValue.TIMESTAMP


            var URL = "https://mainapp.kaizenpro.co.uk/crear_reserva.php?trader=" + TraderDatos.traderId + "&client=" + TraderDatos.userId + "&nombre=" + TraderDatos.UserName + "&hora_reserva=" + hora
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

            val intents = Intent(this, Info_reserva::class.java)


            val args = Bundle()
            args.putParcelable("from_position", TraderDatos.getFrompos())
            args.putParcelable("to_position", TraderDatos.getTopos())




            intents.putExtra("nombre", TraderDatos.getNombre())
            intents.putExtra("especialidad", TraderDatos.getEspecialidad())
            intents.putExtra("hora", hora)
            intents.putExtra("dir", TraderDatos.getDir())

            setResult(Activity.RESULT_OK, intent)
            finish()
            intents.putExtra("bundle", args)
            startActivity(intents)

        } else {

            Toast.makeText(this@TabLayoutDemoActivity, "Para hacer reservas debe completar sus datos de perfil...", Toast.LENGTH_LONG).show()

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

    override fun onRestart() {
        super.onRestart()
        finish()

    }


}