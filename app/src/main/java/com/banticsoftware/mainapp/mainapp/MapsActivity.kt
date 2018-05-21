package com.banticsoftware.mainapp.mainapp

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
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import com.beust.klaxon.*
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
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.progressDialog
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONArray


import java.net.URL
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var mylocation = LatLng(-34.603748,-58.381631)
    var destination = LatLng(-0.0 , -0.0)
    var duracion_recorrido = ""
    var posicionado = ""
    var User_last_reserv = 0

    private lateinit var md : GMapV2Direction

    //Minimo tiempo para updates en Milisegundos
    private val MIN_TIEMPO_ENTRE_UPDATES = (1000 * 60 * 1).toLong() // 1 minuto
    //Minima distancia para updates en metros.
    private val MIN_CAMBIO_DISTANCIA_PARA_UPDATES: Float = 1.5f // 1.5 metros

    val URL = "http://mainapp.kaizenpro.co.uk/consulta_marcadores.php";
    var Traders = ArrayList<Trader>()
    var UserId = 0

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var pd: ProgressDialog

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34


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

        val mLocMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        UserId = intent.getIntExtra("UserId",0)

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

  //      while (mylocation == LatLng(-34.603748,-58.381631) ){
   //         pd.progress = 25
    //    }
      //  pd.progress = 100

      /*  val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        val morph = findViewById<View>(R.id.fabtoolbar) as FABToolbarLayout

        fab.setOnClickListener {
            morph.show()
        }
        morph.setOnClickListener {
            morph.hide()
        }
*/      fab.visibility = View.INVISIBLE
        fab.setOnClickListener {
            val intent = Intent(this@MapsActivity, Chat::class.java)
            intent.putExtra(" Tid", User_last_reserv)
            intent.putExtra(" Uid", UserId)


            startActivity(intent)
        }


        spnivel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filtrar()
            }

        }

        spgenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filtrar()
            }

        }

        spservicio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filtrar()
            }

        }

        sprate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filtrar()
            }

        }




    }



    //NUEVO

    //var progressBar = ProgressBar(this@MapsActivity, null, android.R.attr.progressBarStyleSmall)



    //EP NEW

    override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        Log.i("MapsActivity Ne2", "Lat " + task.result.latitude + " Long " + task.result.longitude)

                        //Toast.makeText(this,"Lat: "+task.result.latitude+" Long: "+task.result.longitude, Toast.LENGTH_SHORT).show()

                        mylocation = LatLng(task.result.latitude,task.result.longitude)

                        pd.progress= 100
                        if (posicionado=="") {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 15F))
                            posicionado = "X"
                        }
                            pd.cancel()
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



//EP NEW FIN





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
marcador.tag = datos

// Set a listener for info window events.
mMap.setOnMarkerClickListener { marker ->
    val position = marker.position
    //Using position get Value from arraylist
    false
}
mMap.setOnMapLongClickListener { marker ->
    val latitude = marker.latitude

    //false

    val intent = Intent(this@MapsActivity, TabLayoutDemoActivity::class.java)

    startActivity(intent)





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
            duracion_recorrido = duration[0]["value"].toString()
            //Toast.makeText(this@MapsActivity, "duración:" +duracion_recorrido  , Toast.LENGTH_LONG).show();

            val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
            // For every element in the JsonArray, decode the polyline string and pass all points to a List
            val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!)  }
            // Add  points to polyline and bounds
            options.add(mylocation)
            LatLongB.include(mylocation)
            for (point in polypts)  {
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



            val args = Bundle()
            args.putParcelable("from_position", mylocation)
            args.putParcelable("to_position", position)
            destination = position



             //   val intent = Intent(this@MapsActivity, TabLayoutDemoActivity::class.java)
            val intent = Intent(this@MapsActivity, Popup::class.java)
                intent.putExtra("bundle", args)
                intent.putExtra("duracion", duracion_recorrido.toInt())
                intent.putExtra(" Id", (n as Trader).id)
                User_last_reserv = (n as Trader).id


                intent.putExtra("nombre", (n as Trader).nombre)
                intent.putExtra("especialidad", (n as Trader).especialidad)
                intent.putExtra("enable", (n as Trader).enable)
                intent.putExtra("limite", (n as Trader).tespera)
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
    pd.progress = 100
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
        val LatLongB = LatLngBounds.Builder()
        fab.visibility =View.VISIBLE
        // Declare polyline object and set up color and width
        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)

        // build URL to call API
        val url = getURL(mylocation, destination)

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


        }
    } else {
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
val params = "$origin&$dest&$sensor"
return "https://maps.googleapis.com/maps/api/directions/json?$params"
}

/**
* Method to decode polyline points
* Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
*/
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

        var URLF ="http://mainapp.kaizenpro.co.uk/consulta_marcadores_filter.php?L="+spnivel.selectedItem.toString()+"&S="+sexo+"&E="+spservicio.selectedItem.toString()+"&R="+sprate.selectedItem.toString()
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















}

