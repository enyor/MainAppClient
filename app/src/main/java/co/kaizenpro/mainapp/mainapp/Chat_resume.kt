package co.kaizenpro.mainapp.mainapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_resume.*

class Chat_resume : AppCompatActivity() {


    private var adapter: AdapterMensajes? = null
    private val database: FirebaseDatabase? = null
    private val databaseReference: DatabaseReference? = null

    private val PHOTO_SEND = 1
    private val PHOTO_PERFIL = 2
    private val fotoPerfilCadena: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_resume)
        var database = FirebaseDatabase.getInstance();
        val Uid = intent.getIntExtra("Uid",0)
        val Tid = intent.getIntExtra("Tid",0)
        val UserName = intent.getStringExtra("UserName")
        val TraderName = intent.getStringExtra("TraderName")
        var sala = Uid.toString()+"@"+Tid.toString()




        var databaseReference = database.getReference(sala);//Sala de chat (nombre)

      adapter = AdapterMensajes(this);

        val l = LinearLayoutManager(this)
        rvMensajes.layoutManager = l
        rvMensajes.adapter = adapter



        adapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                setScrollbar()
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


    }

    private fun setScrollbar() {
        rvMensajes.scrollToPosition(adapter!!.getItemCount() - 1)
    }





}