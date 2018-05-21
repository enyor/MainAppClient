package com.banticsoftware.mainapp.mainapp

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

data class Trader(
        val id: Int,
        val nombre: String,
        val LatLng: String,
        val especialidad: String,
        val telefono: String,
        val sexo: String,
        val enable: String,
        val tespera: Int

){
    class Deserializer: ResponseDeserializable<Array<Trader>> {
        override fun deserialize(content: String): Array<Trader>? = Gson().fromJson(content, Array<Trader>::class.java)
    }
}
