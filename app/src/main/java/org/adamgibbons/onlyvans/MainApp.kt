package org.adamgibbons.onlyvans

import android.app.Application
import org.adamgibbons.onlyvans.models.VanJSONStore
import org.adamgibbons.onlyvans.models.VanMemStore
import org.adamgibbons.onlyvans.models.VanStore

class MainApp : Application() {

    lateinit var vans : VanStore

    override fun onCreate() {
        super.onCreate()
        vans = VanJSONStore(applicationContext)
    }

}