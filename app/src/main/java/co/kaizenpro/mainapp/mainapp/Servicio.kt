package co.kaizenpro.mainapp.mainapp

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Servicio(
        val id_servicio: Int,
        val titulo: String,
        val descripcion: String,
        val precio: String
){
    class Deserializer: ResponseDeserializable<Array<Servicio>> {
        override fun deserialize(content: String): Array<Servicio>? = Gson().fromJson(content, Array<Servicio>::class.java)
    }
}