package org.adamgibbons.onlyvans

import android.app.Application
import org.adamgibbons.onlyvans.models.VanMemStore

class MainApp : Application() {

    val vans = VanMemStore()

}