package co.kaizenpro.mainapp.mainapp

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Portafolio(
        val id_item: Int,
        val imagen: String,
        val titulo: String,
        val contenido: String
){
    class Deserializer: ResponseDeserializable<Array<Portafolio>> {
        override fun deserialize(content: String): Array<Portafolio>? = Gson().fromJson(content, Array<Portafolio>::class.java)
    }
}