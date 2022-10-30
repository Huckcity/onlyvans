package org.adamgibbons.onlyvans.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VanModel(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var image64: String = "",
    var color: String = "",
    var year: Int = 2022,
    var engine: Double = 2.0,
    var location: Location = Location()
) : Parcelable

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable