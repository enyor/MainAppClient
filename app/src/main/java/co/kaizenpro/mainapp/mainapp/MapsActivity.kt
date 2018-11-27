package co.kaizenpro.mainapp.mainapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment


import android.location.Criteria
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.util.Log
import android.os.Looper
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ProgressBar
import com.beust.klaxon.*
import com.bumptech.glide.Glide
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.*
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.progressDialog
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

import org.json.JSONArray


import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var mylocation = LatLng(-34.603748,-58.381631)
    var destination = LatLng(-0.0 , -0.0)
    var duracion_recorrido = ""
    var posicionado = ""
    var User_last_reserv = 0

    var UserName_last_reserv = ""
    var salir = true

    companion object {
        var hora_last_reserv = ""
        var reserva_ativa = false
    }








    //Minimo tiempo para updates en Milisegundos
    private val MIN_TIEMPO_ENTRE_UPDATES = (1000 * 60 * 1).toLong() // 1 minuto
    //Minima distancia para updates en metros.
    private val MIN_CAMBIO_DISTANCIA_PARA_UPDATES: Float = 1.5f // 1.5 metros

    val URL = "https://mainapp.kaizenpro.co.uk/consulta_marcadores.php";
    var Traders = ArrayList<co.kaizenpro.mainapp.mainapp.Trader>()
    var UserId = 0
    public var UserName = ""
    var Edad = 0
    var Sexo = ""
    var Direccion = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var pd: ProgressDialog

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private lateinit var fadein: Animation
    private lateinit var fadeout: Animation



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.importantForAutofill =
                    View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS;
        }
        InfoWindow.visibility = View.GONE

        val mLocMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        UserId = intent.getIntExtra("UserId",0)
        UserName = intent.getStringExtra("UserName")
        Sexo = intent.getStringExtra("Sexo")
        Edad = intent.getIntExtra("Edad" , 0 )
        Direccion = intent.getStringExtra("Direccion")

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.C2D_MESSAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.C2D_MESSAGE) != PackageManager.PERMISSION_GRANTED) {
            //Requiere permisos para Android 6.0
            Log.e("MapsActivity", "No se tienen permisos necesarios!, se requieren.")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.C2D_MESSAGE, Manifest.permission.C2D_MESSAGE), 225)
            return
        } else {
            Log.i("MapsActivity", "Permisos necesarios OK!.")
            mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, locListener, Looper.getMainLooper())
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        //val intents = Intent(this@MapsActivity, Popup::class.java)
        //startActivity(intents)

        var progressBar = ProgressBar(this@MapsActivity, null, android.R.attr.progressBarStyleSmall)
        progressBar.visibility = View.VISIBLE

        pd = progressDialog( "Calculando ubicación", "Localizando")
        pd.max = 100
        pd.progress = 25
        pd.show()

       //Agregado para inhabilitarel mensaje
        pd.progress = 100
        pd.cancel()



            fab.visibility = View.INVISIBLE
        fabtoolbar_toolbar.visibility = View.VISIBLE

        fab.setOnClickListener {
            val intents = Intent(this@MapsActivity, Chat::class.java)
            intents.putExtra("Tid", User_last_reserv)
            intents.putExtra("Uid", UserId)
            intents.putExtra("UserName", UserName)
            intents.putExtra("TraderName", UserName_last_reserv)
            intents.putExtra("hora",hora_last_reserv)
            salir = false


            startActivity(intents)
        }
        fabperfil.setOnClickListener {
            val intents = Intent(this@MapsActivity, Popup_datos_aux::class.java)
             intents.putExtra("UserId", UserId)
            intents.putExtra("UserName", UserName)
            intents.putExtra("Edad", Edad)
            intents.putExtra("Sexo", Sexo)
            intents.putExtra("Direccion", Direccion)


            startActivityForResult(intents, 800)

        }


        spnivel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2.toString() != "0"){
                    imgnivel.setColorFilter(R.color.Magent2)
                } else {
                    imgnivel.clearColorFilter()
                }
                filtrar()
            }

        }

        spgenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2.toString() != "0"){
                    imgsexo.setColorFilter(R.color.Magent2)
                } else {
                    imgsexo.clearColorFilter()
                }
                filtrar()
            }

        }

        spservicio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2.toString() != "0"){
                    imgespecialidad.setColorFilter(R.color.Magent2)
                } else {
                    imgespecialidad.clearColorFilter()
                }
                filtrar()
            }

        }

        sprate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2.toString() != "0"){
                    imgrate.setColorFilter(R.color.Magent2)
                } else {
                    imgrate.clearColorFilter()
                }
                filtrar()
            }

        }


                fadein = AnimationUtils.loadAnimation(this,R.anim.fade_in)
                fadeout = AnimationUtils.loadAnimation(this,R.anim.fadeout)

        imgnivel.setOnClickListener {
            spnivel.performClick()
        }
        imgsexo.setOnClickListener {
            spgenero.performClick()
        }
        imgespecialidad.setOnClickListener {
            spservicio.performClick()
        }
        imgrate.setOnClickListener {
            sprate.performClick()
        }







        //Consultar reserva activa
        verificar_reserva_activa(UserId)


    }





    override fun onStart() {
        super.onStart()







        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }


    }

    override fun onRestart() {
        super.onRestart()
        if (salir){
        finish()
        }

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }



    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        Log.i("MapsActivity Ne2", "Lat " + task.result.latitude + " Long " + task.result.longitude)

                        //Toast.makeText(this,"Lat: "+task.result.latitude+" Long: "+task.result.longitude, Toast.LENGTH_SHORT).show()

                        mylocation = LatLng(task.result.latitude,task.result.longitude)

                       // pd.progress= 100
                        if (posicionado=="") {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 15F))
                            posicionado = "X"
                        }
                       // pd.cancel()
                        /* if (posicionado == ""){
                             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 12F))

                             posicionado = "X"
                         }*/



                    } else {
                        Toast.makeText(this,"Error: "+task.exception, Toast.LENGTH_LONG).show()



                    }
                }
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {



            // Request permission
            startLocationPermissionRequest()

        } else {


            startLocationPermissionRequest()
        }
    }

    private fun checkPermissions() =
            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.C2D_MESSAGE)
                == PackageManager.PERMISSION_GRANTED) {

            //mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this, "Debes aceptar para disfrutar de todos los servicios de la aplicación!", Toast.LENGTH_LONG).show();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.C2D_MESSAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                //  mMap.setMyLocationEnabled(true);
            }
        }

        // Enabling MyLocation Layer of Google Map


        mMap.setMyLocationEnabled(true)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setPadding(0,0,0,70)



        mMap.setOnMapClickListener {
            InfoWindow.startAnimation(fadeout)
            InfoWindow.visibility = View.GONE
        }



















        mMap.setInfoWindowAdapter(InfoWindowCustom(this))




        //Obtengo la ultima localizacion conocida



        //EP
        /*
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()


        var location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))

        */
        //EP





        //val criteria = Criteria()

//EP
/*
criteria.accuracy = Criteria.ACCURACY_FINE
val provider = locationManager.getBestProvider(criteria, false)
locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, locListener, Looper.getMainLooper())








val mLocMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
if ( mLocMgr.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
   // Toast.makeText(this@MapsActivity,"GPS ACTIVO",Toast.LENGTH_LONG).show()


}


if ( mLocMgr.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
   // Toast.makeText(this@MapsActivity,"NETWORK ACTIVA",Toast.LENGTH_LONG).show()
}

//location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))

if (location != null){
    // Toast.makeText(this@MapsActivity, " Actualizando Lat " + location.getLatitude() + " Long " + location.getLongitude(),Toast.LENGTH_LONG).show()

    mylocation = LatLng(location.getLatitude(),location.getLongitude())
}








































if (location == null) {
    val criteria = Criteria()
    criteria.accuracy = Criteria.ACCURACY_FINE
    val provider = locationManager.getBestProvider(criteria, false)
    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, locListener, Looper.getMainLooper())

    location = locationManager.getLastKnownLocation(provider)










}
*/


        //EP



















        // consulta Ws para obtener marcadores
        URL.httpGet().responseObject(Trader.Deserializer()) { request, response, result ->
            val (traders, err) = result















            //Add to ArrayList
            traders?.forEach { trader ->
                Traders.add(trader)
                val lat = trader.LatLng.split(',')
                val otro = LatLng(lat[0].toDouble(), lat[1].toDouble())
                drawMarker(otro, trader.nombre, trader.sexo, trader)
            }

        }




















        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 12F))
        //       mMap.animateCamera(CameraUpdateFactory.zoomIn())














    }


    private fun drawMarker(point: LatLng,title: String, sexo: String, datos: Trader) {
// Creating an instance of MarkerOptions
        val markerOptions = MarkerOptions()

// Setting latitude and longitude for the marker
        markerOptions.position(point)

// Set title
        markerOptions.title(title)





// Icon
//markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        if (sexo=="M") {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_caballero))
        }else{
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_dama))
        }


// Adding marker on the Google Map
        val marcador = mMap.addMarker(markerOptions)
        marcador.hideInfoWindow()
        marcador.tag = datos


// Set a listener for info window events.
    /*    mMap.setOnMarkerClickListener { marker ->
            val position = marker.position
            //Using position get Value from arraylist
            false
        }
        mMap.setOnMapLongClickListener { marker ->
            val latitude = marker.latitude

            //false

            //val intent = Intent(this@MapsActivity, TabLayoutDemoActivity::class.java)

            //startActivity(intent)





        }*/

        mMap.setOnMarkerClickListener { marker ->
            val position = marker.position
            val n = marker.tag
            //Using position get Value from arraylist
            NomT.setText(marker.title.toString())
            espT.setText((n as Trader).especialidad)
            ratT.rating = (n as Trader).rate.toFloat()
            val  imageUri = "https://mainapp.kaizenpro.co.uk/assets/"+(n as Trader).imagen


/*                Picasso.with(this@MapsActivity)
                        .load(imageUri)
                        //.placeholder(R.drawable.ic_launcher_background)
                        .resize(90, 90)
                        .centerCrop()
                        .transform(CircleTransformPicasso())
                        //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(imgAvatar)
*/
            Glide.with(this@MapsActivity)
                    .load(Uri.parse(imageUri)) // add your image url
                    .override(90,90)
                    .centerCrop()
                    .transform(CircleTransform(this@MapsActivity)) // applying the image transformer
                    .into(imgAvatar)

            btT.setOnClickListener {

                //INICIO RUTA
                val LatLongB = LatLngBounds.Builder()

                // Declare polyline object and set up color and width
                val options = PolylineOptions()
                options.color(Color.RED)
                options.width(5f)

                // build URL to call API
                val url = getURL(mylocation, position)
                if (mylocation != LatLng(-34.603748,-58.381631)) {

                    doAsync {
                        // Connect to URL, download content and convert into string asynchronously
                        val result = URL(url).readText()
                        uiThread {
                            // When API call is done, create parser and convert into JsonObjec
                            val parser: Parser = Parser()
                            val stringBuilder: StringBuilder = StringBuilder(result)
                            val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                            // get to the correct element in JsonObject
                            val routes = json.array<JsonObject>("routes")
                            val duration = routes!!["legs"]["duration"] as JsonArray<JsonObject>

                            if (duration.size != 0) {

                                // try {
                                duracion_recorrido = duration[0]["value"].toString()
                                //Toast.makeText(this@MapsActivity, "duración:" +duracion_recorrido  , Toast.LENGTH_LONG).show();
                                // }catch (e: IndexOutOfBoundsException){
                                //     null
                                // }

                                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                                // For every element in the JsonArray, decode the polyline string and pass all points to a List
                                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                                // Add  points to polyline and bounds
                                options.add(mylocation)
                                LatLongB.include(mylocation)
                                for (point in polypts) {
                                    options.add(point)
                                    LatLongB.include(point)
                                }
                                options.add(position)
                                LatLongB.include(position)
                                // build bounds
                                val bounds = LatLongB.build()
                                // add polyline to the map
                                mMap!!.addPolyline(options)
                                // show map with route centered
                                mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))


                            }
                            val args = Bundle()
                            args.putParcelable("from_position", mylocation)
                            args.putParcelable("to_position", position)
                            destination = position



                            // val intent = Intent(this@MapsActivity, TabLayoutDemoActivity::class.java)
                            val intent = Intent(this@MapsActivity, TabLayoutDemoActivity::class.java)
                            //val intent = Intent(this@MapsActivity, Popup::class.java)

                            try {
                                duracion_recorrido.toInt()
                            } catch (e: NumberFormatException ){
                                duracion_recorrido = "9999"
                            }

                            intent.putExtra("bundle", args)
                            intent.putExtra("duracion", duracion_recorrido.toInt())
                            intent.putExtra("Id", (n as Trader).id)
                            User_last_reserv = (n as Trader).id


                            intent.putExtra("nombre", (n as Trader).nombre)
                            UserName_last_reserv = (n as Trader).nombre
                            intent.putExtra("especialidad", (n as Trader).especialidad)
                            intent.putExtra("enable", (n as Trader).enable)
                            intent.putExtra("limite", (n as Trader).tespera)
                            intent.putExtra("rate", (n as Trader).rate)
                            intent.putExtra("UserName", UserName)
                            intent.putExtra("Tid", User_last_reserv)
                            intent.putExtra("Uid", UserId)
                            if ((n as Trader).imagen==""){
                                intent.putExtra("img","avatar.png")
                            }else{
                                intent.putExtra("img",(n as Trader).imagen)
                            }
                            if ((n as Trader).imagen_cover==""){
                                intent.putExtra("img_cover","avatar.png")
                            }else{
                                intent.putExtra("img_cover",(n as Trader).imagen_cover)
                            }

                            if ((n as Trader).direccion==""){
                                intent.putExtra("dir","NA")
                            }else{
                                intent.putExtra("dir",(n as Trader).direccion)
                            }
                            // Calling Application class (see application tag in AndroidManifest.xml)
                            val TraderDatos = applicationContext as TraderDatos
                            //Llenando Datos
                            TraderDatos.frompos = mylocation
                            TraderDatos.topos   = destination
                            TraderDatos.TraderId = User_last_reserv.toString()
                            TraderDatos.duracion = duracion_recorrido.toInt()
                            TraderDatos.limite = (n as Trader).tespera.toString()
                            TraderDatos.dir = (n as Trader).direccion
                            TraderDatos.UserName = UserName
                            TraderDatos.UserId = UserId.toString()
                            TraderDatos.nombre = (n as Trader).nombre
                            TraderDatos.especialidad = (n as Trader).especialidad
                            TraderDatos.dir = (n as Trader).direccion
                            TraderDatos.imgcover = (n as Trader).imagen_cover
                            TraderDatos.rating = (n as Trader).rate
                            // TraderDatos.edad = (n as Trader).edad

                            //startActivity(intent)
                            //
                            startActivityForResult(intent, 100)




                        }



                    }

                    //FIN RUTA

                }else{
                    Toast.makeText(this@MapsActivity,"Por favor espere unos instantes para obtener su ubicación",Toast.LENGTH_LONG).show()
                }


            }
            InfoWindow.setOnClickListener {

                //INICIO RUTA
                val LatLongB = LatLngBounds.Builder()

                // Declare polyline object and set up color and width
                val options = PolylineOptions()
                options.color(Color.RED)
                options.width(5f)

                // build URL to call API
                val url = getURL(mylocation, position)
                if (mylocation != LatLng(-34.603748,-58.381631)) {

                    doAsync {
                        // Connect to URL, download content and convert into string asynchronously
                        val result = URL(url).readText()
                        uiThread {
                            // When API call is done, create parser and convert into JsonObjec
                            val parser: Parser = Parser()
                            val stringBuilder: StringBuilder = StringBuilder(result)
                            val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                            // get to the correct element in JsonObject
                            val routes = json.array<JsonObject>("routes")
                            val duration = routes!!["legs"]["duration"] as JsonArray<JsonObject>

                            if (duration.size != 0) {

                                // try {
                                duracion_recorrido = duration[0]["value"].toString()
                                //Toast.makeText(this@MapsActivity, "duración:" +duracion_recorrido  , Toast.LENGTH_LONG).show();
                                // }catch (e: IndexOutOfBoundsException){
                                //     null
                                // }

                                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                                // For every element in the JsonArray, decode the polyline string and pass all points to a List
                                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                                // Add  points to polyline and bounds
                                options.add(mylocation)
                                LatLongB.include(mylocation)
                                for (point in polypts) {
                                    options.add(point)
                                    LatLongB.include(point)
                                }
                                options.add(position)
                                LatLongB.include(position)
                                // build bounds
                                val bounds = LatLongB.build()
                                // add polyline to the map
                                mMap!!.addPolyline(options)
                                // show map with route centered
                                mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))


                            }
                            val args = Bundle()
                            args.putParcelable("from_position", mylocation)
                            args.putParcelable("to_position", position)
                            destination = position



                            // val intent = Intent(this@MapsActivity, TabLayoutDemoActivity::class.java)
                            val intent = Intent(this@MapsActivity, TabLayoutDemoActivity::class.java)
                            //val intent = Intent(this@MapsActivity, Popup::class.java)

                            try {
                                duracion_recorrido.toInt()
                            } catch (e: NumberFormatException ){
                                duracion_recorrido = "9999"
                            }

                            intent.putExtra("bundle", args)
                            intent.putExtra("duracion", duracion_recorrido.toInt())
                            intent.putExtra("Id", (n as Trader).id)
                            User_last_reserv = (n as Trader).id


                            intent.putExtra("nombre", (n as Trader).nombre)
                            UserName_last_reserv = (n as Trader).nombre
                            intent.putExtra("especialidad", (n as Trader).especialidad)
                            intent.putExtra("enable", (n as Trader).enable)
                            intent.putExtra("limite", (n as Trader).tespera)
                            intent.putExtra("rate", (n as Trader).rate)
                            intent.putExtra("UserName", UserName)
                            intent.putExtra("Tid", User_last_reserv)
                            intent.putExtra("Uid", UserId)
                            if ((n as Trader).imagen==""){
                                intent.putExtra("img","avatar.png")
                            }else{
                                intent.putExtra("img",(n as Trader).imagen)
                            }
                            if ((n as Trader).imagen_cover==""){
                                intent.putExtra("img_cover","avatar.png")
                            }else{
                                intent.putExtra("img_cover",(n as Trader).imagen_cover)
                            }

                            if ((n as Trader).direccion==""){
                                intent.putExtra("dir","NA")
                            }else{
                                intent.putExtra("dir",(n as Trader).direccion)
                            }
                            // Calling Application class (see application tag in AndroidManifest.xml)
                            val TraderDatos = applicationContext as TraderDatos
                            //Llenando Datos
                            TraderDatos.frompos = mylocation
                            TraderDatos.topos   = destination
                            TraderDatos.TraderId = User_last_reserv.toString()
                            TraderDatos.duracion = duracion_recorrido.toInt()
                            TraderDatos.limite = (n as Trader).tespera.toString()
                            TraderDatos.dir = (n as Trader).direccion
                            TraderDatos.UserName = UserName
                            TraderDatos.UserId = UserId.toString()
                            TraderDatos.nombre = (n as Trader).nombre
                            TraderDatos.especialidad = (n as Trader).especialidad
                            TraderDatos.dir = (n as Trader).direccion
                            TraderDatos.imgcover = (n as Trader).imagen_cover
                            TraderDatos.rating = (n as Trader).rate
                           // TraderDatos.edad = (n as Trader).edad

                            //startActivity(intent)
                            //
                            startActivityForResult(intent, 100)




                        }



                    }

                    //FIN RUTA

                }else{
                    Toast.makeText(this@MapsActivity,"Por favor espere unos instantes para obtener su ubicación",Toast.LENGTH_LONG).show()
                }


            }

            InfoWindow.visibility = View.VISIBLE
            InfoWindow.startAnimation(fadein)

            false
        }



        mMap.setOnInfoWindowClickListener {  marker ->

            val position = marker.position
            val n = marker.tag


            //INICIO RUTA
            val LatLongB = LatLngBounds.Builder()

            // Declare polyline object and set up color and width
            val options = PolylineOptions()
            options.color(Color.RED)
            options.width(5f)

            // build URL to call API
            val url = getURL(mylocation, position)
            if (mylocation != LatLng(-34.603748,-58.381631)) {

                doAsync {
                    // Connect to URL, download content and convert into string asynchronously
                    val result = URL(url).readText()
                    uiThread {
                        // When API call is done, create parser and convert into JsonObjec
                        val parser: Parser = Parser()
                        val stringBuilder: StringBuilder = StringBuilder(result)
                        val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                        // get to the correct element in JsonObject
                        val routes = json.array<JsonObject>("routes")
                        val duration = routes!!["legs"]["duration"] as JsonArray<JsonObject>

                        if (duration.size != 0) {

                            // try {
                            duracion_recorrido = duration[0]["value"].toString()
                            //Toast.makeText(this@MapsActivity, "duración:" +duracion_recorrido  , Toast.LENGTH_LONG).show();
                            // }catch (e: IndexOutOfBoundsException){
                            //     null
                            // }

                            val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                            // For every element in the JsonArray, decode the polyline string and pass all points to a List
                            val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                            // Add  points to polyline and bounds
                            options.add(mylocation)
                            LatLongB.include(mylocation)
                            for (point in polypts) {
                                options.add(point)
                                LatLongB.include(point)
                            }
                            options.add(position)
                            LatLongB.include(position)
                            // build bounds
                            val bounds = LatLongB.build()
                            // add polyline to the map
                            mMap!!.addPolyline(options)
                            // show map with route centered
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))


                        }
                        val args = Bundle()
                        args.putParcelable("from_position", mylocation)
                        args.putParcelable("to_position", position)
                        destination = position



                        // val intent = Intent(this@MapsActivity, TabLayoutDemoActivity::class.java)
                        val intent = Intent(this@MapsActivity, TabLayoutDemoActivity::class.java)
                        //val intent = Intent(this@MapsActivity, Popup::class.java)

                        try {
                            duracion_recorrido.toInt()
                        } catch (e: NumberFormatException ){
                            duracion_recorrido = "9999"
                        }

                        intent.putExtra("bundle", args)
                        intent.putExtra("duracion", duracion_recorrido.toInt())
                        intent.putExtra("Id", (n as Trader).id)
                        User_last_reserv = (n as Trader).id


                        intent.putExtra("nombre", (n as Trader).nombre)
                        UserName_last_reserv = (n as Trader).nombre
                        intent.putExtra("especialidad", (n as Trader).especialidad)
                        intent.putExtra("enable", (n as Trader).enable)
                        intent.putExtra("limite", (n as Trader).tespera)
                        intent.putExtra("rate", (n as Trader).rate)
                        intent.putExtra("UserName", UserName)
                        intent.putExtra("Tid", User_last_reserv)
                        intent.putExtra("Uid", UserId)
                        if ((n as Trader).imagen==""){
                            intent.putExtra("img","avatar.png")
                        }else{
                            intent.putExtra("img",(n as Trader).imagen)
                        }
                        if ((n as Trader).imagen_cover==""){
                            intent.putExtra("img_cover","avatar.png")
                        }else{
                            intent.putExtra("img_cover",(n as Trader).imagen_cover)
                        }

                        if ((n as Trader).direccion==""){
                            intent.putExtra("dir","NA")
                        }else{
                            intent.putExtra("dir",(n as Trader).direccion)
                        }
                        // Calling Application class (see application tag in AndroidManifest.xml)
                        val TraderDatos = applicationContext as TraderDatos
                        //Llenando Datos
                        TraderDatos.frompos = mylocation
                        TraderDatos.topos   = destination
                        TraderDatos.TraderId = User_last_reserv.toString()
                        TraderDatos.duracion = duracion_recorrido.toInt()
                        TraderDatos.limite = (n as Trader).tespera.toString()
                        TraderDatos.dir = (n as Trader).direccion
                        TraderDatos.UserName = UserName
                        TraderDatos.UserId = UserId.toString()
                        TraderDatos.nombre = (n as Trader).nombre
                        TraderDatos.especialidad = (n as Trader).especialidad
                        TraderDatos.dir = (n as Trader).direccion

                                //startActivity(intent)
                        //
                        startActivityForResult(intent, 100)




                    }



                }

                //FIN RUTA

            }else{
                Toast.makeText(this@MapsActivity,"Por favor espere unos instantes para obtener su ubicación",Toast.LENGTH_LONG).show()
            }


        }


    }

    var locListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.i("MapsActivity", "Lat " + location.getLatitude() + " Long " + location.getLongitude())


            mylocation = LatLng(location.getLatitude(),location.getLongitude())
           // pd.progress = 100
            if (posicionado=="") {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 15F))
                posicionado = "X"
            }
           // pd.cancel()
            //  Toast.makeText(this@MapsActivity,"Loc act: "+mylocation.toString(),Toast.LENGTH_SHORT).show()
            /* if (posicionado == "" && location != null){
                 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 12F))

                 posicionado = "X"
             }*/

        }

        override fun onProviderDisabled(provider: String) {
            Log.i("MapsActivity", "onProviderDisabled()")
        }

        override fun onProviderEnabled(provider: String) {
            Log.i("MapsActivity", "onProviderEnabled()")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.i("MapsActivity", "onStatusChanged()")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100) {
            if (resultCode == RESULT_OK) {
                salir = false
                //hora_last_reserv = data!!.getStringExtra("hora")
                val LatLongB = LatLngBounds.Builder()
                fab.visibility =View.VISIBLE
                // Declare polyline object and set up color and width
                val options = PolylineOptions()
                options.color(Color.RED)
                options.width(5f)

                // build URL to call API
                val url = getURL(mylocation, destination)
                InfoWindow.startAnimation(fadeout)
                InfoWindow.visibility = View.GONE
                doAsync {
                    // Connect to URL, download content and convert into string asynchronously
                    val result = URL(url).readText()
                    uiThread {
                        // When API call is done, create parser and convert into JsonObjec
                        val parser: Parser = Parser()
                        val stringBuilder: StringBuilder = StringBuilder(result)
                        val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                        // get to the correct element in JsonObject
                        val routes = json.array<JsonObject>("routes")
                        val duration = routes!!["legs"]["duration"] as JsonArray<JsonObject>
                        duracion_recorrido = duration[0]["text"].toString()
                        //Toast.makeText(this@MapsActivity, "duración:" + duracion_recorrido, Toast.LENGTH_LONG).show();

                        val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                        // For every element in the JsonArray, decode the polyline string and pass all points to a List
                        val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                        // Add  points to polyline and bounds
                        options.add(mylocation)
                        LatLongB.include(mylocation)
                        for (point in polypts) {
                            options.add(point)
                            LatLongB.include(point)
                        }
                        options.add(destination)
                        LatLongB.include(destination)
                        // build bounds
                        val bounds = LatLongB.build()
                        // add polyline to the map
                        mMap!!.addPolyline(options)
                        // show map with route centered
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                    }

                    fabtoolbar_toolbar.animate()
                            .translationY(fabtoolbar_toolbar.getHeight().toFloat())
                            .alpha(1.0f)
                            .setListener(null)
                            .setDuration(2000)

                }


            } else {
                Toast.makeText(this@MapsActivity,"Accion cancelada",Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        }
        if(requestCode == 800) {
            if (resultCode == RESULT_OK){
                UserName = data!!.getStringExtra("nom")
                Edad = data!!.getIntExtra("edad",0)
                Sexo = data!!.getStringExtra("sexo")
                Direccion = data!!.getStringExtra("direccion")

                salir = false
                Toast.makeText(this@MapsActivity,"Datos Actualizados",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this@MapsActivity,"Accion cancelada",Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        }
    }


    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val apikey = "key=AIzaSyDrgvfnlT0T43H6HQkk1YEXaHlta9fJAbM"
        /*Deprecated
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
        */
        val params = "$origin&$dest&$sensor&$apikey"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"

    }


    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }

    private fun filtrar(){

        var sexo = spgenero.selectedItem.toString().substring(0,1)
        if (sexo == "T"){
            sexo = "Todos"
        }

        var URLF ="https://mainapp.kaizenpro.co.uk/consulta_marcadores_filter.php?L="+spnivel.selectedItem.toString()+"&S="+sexo+"&E="+spservicio.selectedItem.toString()+"&R="+sprate.selectedItem.toString()
        URLF.httpGet().responseObject(Trader.Deserializer()) { request, response, result ->
            val (traders, err) = result

            mMap.clear()
            //Add to ArrayList
            traders?.forEach { trader ->
                Traders.add(trader)
                val lat = trader.LatLng.split(',')
                val otro = LatLng(lat[0].toDouble(), lat[1].toDouble())
                drawMarker(otro, trader.nombre, trader.sexo, trader)
            }

        }



    }

    private fun verificar_reserva_activa( id: Int){

        val time = java.util.Calendar.getInstance().time
        val sdh = SimpleDateFormat("HH:mm:ss")
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val hora = sdh.format(time)
        val fecha = sdf.format(time)

        val URLRA = "https://mainapp.kaizenpro.co.uk/consulta_reserva_activa.php?id="+id+"&fecha="+fecha+"&hora="+hora;
        var ReservaA = ArrayList<co.kaizenpro.mainapp.mainapp.ResAct>()

        URLRA.httpGet().responseObject(ResAct.Deserializer()) { request, response, result ->
            val (resacts, err) = result

            resacts?.forEach { resact ->
                ReservaA.add(resact)

            }

            if(ReservaA.isNotEmpty()){
                User_last_reserv = ReservaA[0].trader_id
                hora_last_reserv = ReservaA[0].hora_reserva
                fab.visibility =View.VISIBLE
            }

        }


    }















}

