package com.banticsoftware.mainapp.mainapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.banticsoftware.mainapp.mainapp.R.id.txtMensaje
import com.google.firebase.database.ServerValue
import com.banticsoftware.mainapp.mainapp.R.id.nombre
import kotlinx.android.synthetic.main.activity_chat.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ChildEventListener
import android.support.v7.widget.RecyclerView
import com.banticsoftware.mainapp.mainapp.R.id.fotoPerfil
import com.bumptech.glide.Glide
import com.banticsoftware.mainapp.mainapp.R.id.nombre
import com.google.android.gms.tasks.OnSuccessListener
import android.content.Intent
import com.banticsoftware.mainapp.mainapp.R.id.rvMensajes
import com.banticsoftware.mainapp.mainapp.R.id.rvMensajes
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.database.DatabaseReference
import android.widget.ImageButton
import android.widget.EditText
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView

class Chat : AppCompatActivity() {


    private var adapter: AdapterMensajes? = null
    private val database: FirebaseDatabase? = null
    private val databaseReference: DatabaseReference? = null

    private val PHOTO_SEND = 1
    private val PHOTO_PERFIL = 2
    private val fotoPerfilCadena: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        var database = FirebaseDatabase.getInstance();
        var Uid = intent.getIntExtra("Uid",0)
        var Tid = intent.getIntExtra("Tid",0)
        var sala = Uid.toString()+"@"+Tid.toString()


        var databaseReference = database.getReference(sala);//Sala de chat (nombre)

      adapter = AdapterMensajes(this);

        val l = LinearLayoutManager(this)
        rvMensajes.layoutManager = l
        rvMensajes.adapter = adapter

        btnEnviar.setOnClickListener {
            databaseReference.push().setValue(MensajeEnviar(txtMensaje.getText().toString(), nombre.getText().toString(), "", "1", ServerValue.TIMESTAMP))
            txtMensaje.setText("")
        }

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
