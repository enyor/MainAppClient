package com.banticsoftware.mainapp.mainapp


import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.util.*

data class Horario(
        val date: Date

){
    class Deserializer: ResponseDeserializable<Array<Horario>> {
        override fun deserialize(content: String): Array<Horario>? = Gson().fromJson(content, Array<Horario>::class.java)
    }
}