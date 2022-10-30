package org.adamgibbons.onlyvans.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var id: String = "",
    var username: String = "",
    var password: String = ""
) : Parcelable