package co.kaizenpro.mainapp.mainapp

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Review(
        val id_review: Int,
        val de: String,
        val rating: Float,
        val comentario: String
){
    class Deserializer: ResponseDeserializable<Array<Review>> {
        override fun deserialize(content: String): Array<Review>? = Gson().fromJson(content, Array<Review>::class.java)
    }
}