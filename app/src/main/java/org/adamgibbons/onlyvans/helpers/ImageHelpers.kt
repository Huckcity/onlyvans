package org.adamgibbons.onlyvans.helpers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.result.ActivityResultLauncher
import org.adamgibbons.onlyvans.R
import java.io.ByteArrayOutputStream

fun showImagePicker(intentLauncher : ActivityResultLauncher<Intent>) {

    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        type = "image/*"
        addCategory(Intent.CATEGORY_OPENABLE)
    }
    chooseFile = Intent.createChooser(chooseFile, R.string.select_van_image.toString())
    intentLauncher.launch(chooseFile)
}

fun encodeImage(bm: Bitmap): String? {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream)
    val b: ByteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun decodeImage(imageString: String): Bitmap {
    val decodedByte = Base64.decode(imageString, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return bitmap
}