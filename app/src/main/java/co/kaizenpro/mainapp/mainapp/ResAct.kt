package co.kaizenpro.mainapp.mainapp

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class ResAct(
        val fecha: String,
        val hora_reserva: String,
        val trader_id: Int


){
    class Deserializer: ResponseDeserializable<Array<ResAct>> {
        override fun deserialize(content: String): Array<ResAct>? = Gson().fromJson(content, Array<ResAct>::class.java)
    }
}