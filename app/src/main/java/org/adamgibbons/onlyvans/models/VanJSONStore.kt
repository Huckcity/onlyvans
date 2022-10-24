package org.adamgibbons.onlyvans.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.adamgibbons.onlyvans.helpers.exists
import org.adamgibbons.onlyvans.helpers.read
import org.adamgibbons.onlyvans.helpers.write
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

const val JSON_FILE = "vans.json"
val gsonBuilder: Gson? = GsonBuilder()
    .setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()

val listType: Type = object : TypeToken<ArrayList<VanModel>>() {}.type

class VanJSONStore(private val context: Context) : VanStore {

    private var vans = mutableListOf<VanModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findById(id: String): VanModel {
        return vans.find { v -> v.id == id }!!
    }

    override fun findAll(): List<VanModel> {
        logAll()
        return vans
    }

    override fun create(van: VanModel) {
        van.id = UUID.randomUUID().toString()
        vans.add(van)
        serialize()
    }

    override fun update(van: VanModel) {
        val existingVan: VanModel? = vans.find { v -> v.id == van.id }
        if (existingVan != null) {
            existingVan.title = van.title
            existingVan.description = van.description
            existingVan.image64 = van.image64
            existingVan.color = van.color
            existingVan.engine = van.engine
            existingVan.year = van.year
        }
        serialize()
    }

    override fun delete(van: VanModel) {
        vans.remove(van)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder?.toJson(vans, listType)
        if (jsonString != null) {
            write(context, JSON_FILE, jsonString)
        }
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        if (gsonBuilder != null) {
            vans = gsonBuilder.fromJson(jsonString, listType)
        }
    }

    private fun logAll() {
        vans.forEach { println(it) }
    }

}

class UriParser : JsonDeserializer<Uri>, JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}