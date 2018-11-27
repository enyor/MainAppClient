package co.kaizenpro.mainapp.mainapp

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.httpGet
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_tab4.*
import kotlinx.android.synthetic.main.fragment_tab4.view.*


/**
 * A simple [Fragment] subclass.
 */
class Tab4Fragment : Fragment() {

     //var recyclerPersonajes: RecyclerView? = null
    var listaPersonaje: ArrayList<ItemPortafolio>? = null
    var collapse = true



    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater!!.inflate(R.layout.fragment_tab4, container, false)
        val Trader = activity.applicationContext as TraderDatos
        var URL = "https://mainapp.kaizenpro.co.uk/consulta_portafolio.php?id="+Trader.traderId

        val constraintSet1 = ConstraintSet()
        constraintSet1.clone(v.ConstraintLayout)
        val constraintSet2 = ConstraintSet()
        constraintSet2.clone(v.context, R.layout.fragment_tab4_alt)
        var changed = false


        v.nomP.setText(Trader.nombre)
        v.nomD.setText(Trader.nombre)
        v.espD.setText(Trader.especialidad)
        v.espD.setText(Trader.especialidad)
        v.invitaD.setText(Trader.nombre+" espera tu reserva")

        var i = 0

        var itemsData = arrayListOf<ItemPortafolio>()
        URL.httpGet().responseObject(Portafolio.Deserializer()) { request, response, result ->
            val (portafolios, err) = result


            //Add to ArrayList
            portafolios?.forEach { portafolio ->
                itemsData.add(ItemPortafolio(portafolio.id_item, portafolio.titulo, portafolio.contenido, portafolio.imagen))
            }
            if(itemsData.isNotEmpty()) {

                v.textimg.setText(itemsData[i].nombre)
                v.textoampD.setText(itemsData[i].info)
               /* Picasso.with(v.context)
                        .load("https://mainapp.kaizenpro.co.uk/assets/" + itemsData[i].imagenId)
                        .resize(100, 100)
                        .into(v.imageView)*/
                  Glide.with(v.context)
                .load("https://mainapp.kaizenpro.co.uk/assets/" + itemsData[i].imagenId) // add your image url
                //.override(100,100)
                          .dontTransform()
                          //.centerCrop()
                //.transform(CircleTransform(this@TabLayoutDemoActivity)) // applying the image transformer
                .into(v.imageView)
            }
        }

        v.imageView.setOnClickListener {

            if(itemsData.isNotEmpty()) {
                i = i + 1
                if (itemsData.size == i) {
                    i = 0
                }
                v.textimg.setText(itemsData[i].nombre)
                v.textoampD.setText(itemsData[i].info)

               /* Picasso.with(v.context)
                        .load("https://mainapp.kaizenpro.co.uk/assets/" + itemsData[i].imagenId)
                        .resize(100, 100)
                        .into(v.imageView)
                        */
                Glide.with(v.context)
                        .load("https://mainapp.kaizenpro.co.uk/assets/" + itemsData[i].imagenId) // add your image url
                        //.override(100,100)
                        .dontTransform()
                        //.centerCrop()
                        //.transform(CircleTransform(this@TabLayoutDemoActivity)) // applying the image transformer
                        .into(v.imageView)

            }
        }

     /*   var recyclerPersonajes = v.findViewById<RecyclerView>(R.id.recyclerId)
        var llm = LinearLayoutManager(activity)

        recyclerPersonajes.layoutManager = llm
        //recyclerPersonajes.setLayoutManager(LinearLayoutManager(context))
        var itemsData = arrayListOf<ItemPortafolio>()
        //  llenarLista()
        URL.httpGet().responseObject(Portafolio.Deserializer()) { request, response, result ->
            val (portafolios, err) = result


            //Add to ArrayList
            portafolios?.forEach { portafolio ->
                itemsData.add(ItemPortafolio(portafolio.id_item,portafolio.titulo,portafolio.contenido,portafolio.imagen))
            }

            val adapter = PortafolioAdapter(itemsData)


            recyclerPersonajes.isNestedScrollingEnabled = false
            //recyclerPersonajes.setHasFixedSize(true)


            recyclerPersonajes.adapter = adapter
        }

*/
        v.floatingActionButton.setOnClickListener {

            TransitionManager.beginDelayedTransition(v.ConstraintLayout)
            val constraint = if (changed) constraintSet1 else constraintSet2
            constraint.applyTo(v.ConstraintLayout)
            changed = !changed

            if (changed) {
                v.floatingActionButton.setImageResource(R.drawable.ic_arrow_down)
            } else {
                v.floatingActionButton.setImageResource(R.drawable.ic_info_i_blue)
            }

        }


        return v
    }



}// Required empty public constructor