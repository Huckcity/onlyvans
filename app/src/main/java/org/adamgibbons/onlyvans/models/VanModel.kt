package org.adamgibbons.onlyvans.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VanModel (
    var id: String = "",
    var title: String = "",
    var description: String = ""
        ) : Parcelable