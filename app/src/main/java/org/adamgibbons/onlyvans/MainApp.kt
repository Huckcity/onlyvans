package org.adamgibbons.onlyvans

import android.app.Application
import org.adamgibbons.onlyvans.helpers.AuthHelpers
import org.adamgibbons.onlyvans.models.UserJSONStore
import org.adamgibbons.onlyvans.models.UserStore
import org.adamgibbons.onlyvans.models.VanJSONStore
import org.adamgibbons.onlyvans.models.VanStore

class MainApp : Application() {

    lateinit var vans : VanStore
    lateinit var users : UserStore
    lateinit var prefs : AuthHelpers

    override fun onCreate() {
        super.onCreate()
        vans = VanJSONStore(applicationContext)
        users = UserJSONStore(applicationContext)
        prefs = AuthHelpers(applicationContext)
    }

}