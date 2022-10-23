package org.adamgibbons.onlyvans.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VanModel (
    var id: Long = 0,
    var title: String = "",
    var description: String = ""
        ) : Parcelable