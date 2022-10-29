package org.adamgibbons.onlyvans.models

import android.content.Context
import android.net.Uri
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.adamgibbons.onlyvans.helpers.exists
import org.adamgibbons.onlyvans.helpers.read
import org.adamgibbons.onlyvans.helpers.write
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class UserJSONStore(private val context: Context) : UserStore {

    private val JSON_FILE = "users.json"
    private val gsonBuilder: Gson? = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Uri::class.java, UriParser())
        .create()

    private val listType: Type = object : TypeToken<ArrayList<UserModel>>() {}.type
    private var users = mutableListOf<UserModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findById(id: String): UserModel {
        return users.find { u -> u.id == id }!!
    }

    override fun findAll(): List<UserModel> {
        logAll()
        return users
    }

    override fun create(user: UserModel) {
        user.id = UUID.randomUUID().toString()
        user.password = BCrypt.withDefaults().hashToString(12, user.password.toCharArray())
        users.add(user)
        serialize()
    }

    override fun update(user: UserModel) {
        val existingUser: UserModel? = users.find { u -> u.id == user.id }
        if (existingUser != null) {
            existingUser.username = user.username
            existingUser.password = user.password
        }
        serialize()
    }

    override fun delete(user: UserModel) {
        users.remove(user)
        serialize()
    }

    override fun findByUsername(username: String): UserModel? {
        return users.find { u -> u.username == username }
    }

    override fun verifyPassword(user: UserModel, password: String): Boolean {
        val res = BCrypt.verifyer().verify(password.toCharArray(), user.password.toCharArray())
        return res.verified
    }

    private fun serialize() {
        val jsonString = gsonBuilder?.toJson(users, listType)
        if (jsonString != null) {
            write(context, JSON_FILE, jsonString)
        }
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        if (gsonBuilder != null) {
            users = gsonBuilder.fromJson(jsonString, listType)
        }
    }

    private fun logAll() {
        users.forEach { println(it) }
    }
}