package co.kaizenpro.mainapp.mainapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.kittinunf.fuel.httpGet
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat_resume.*
import kotlinx.android.synthetic.main.activity_chat_resume.view.*

/**
 * A simple [Fragment] subclass.
 */
class Tab3Fragment : Fragment() {

    private var adapter: AdapterMensajes? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater!!.inflate(R.layout.activity_chat_resume, container, false)
        val TraderDatos = activity.applicationContext as TraderDatos
        var database = FirebaseDatabase.getInstance();
        //var URL = "http://mainapp.kaizenpro.co.uk/consulta_review.php?id="+TraderDatos.traderId

        var sala = TraderDatos.userId+"@"+TraderDatos.traderId

        var databaseReference = database.getReference(sala);//Sala de chat (nombre)

        adapter = AdapterMensajes(v.context);

        val l = LinearLayoutManager(v.context)
        v.rvMensajes.layoutManager = l
        v.rvMensajes.adapter = adapter



        adapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                v.rvMensajes.scrollToPosition(adapter!!.getItemCount() - 1)
            }
        })

        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot?, s: String?) {
                val m = dataSnapshot?.getValue<MensajeRecibir>(MensajeRecibir::class.java)
                adapter!!.addMensaje(m)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })





        return v
    }// Required empty public constructor


}