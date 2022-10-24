package org.adamgibbons.onlyvans.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.adamgibbons.onlyvans.MainApp
import org.adamgibbons.onlyvans.R
import org.adamgibbons.onlyvans.databinding.ActivityVanBinding
import org.adamgibbons.onlyvans.helpers.decodeImage
import org.adamgibbons.onlyvans.helpers.encodeImage
import org.adamgibbons.onlyvans.helpers.showImagePicker
import org.adamgibbons.onlyvans.models.VanModel
import java.io.InputStream


class VanActivity : AppCompatActivity() {

    private var van = VanModel()
    private lateinit var binding: ActivityVanBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var app: MainApp
    private var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        app = application as MainApp

        if (intent.hasExtra("van_edit")) {
            edit = true
            van = intent.extras?.getParcelable("van_edit")!!
            binding.vanTitle.setText(van.title)
            binding.vanDescription.setText(van.description)
            binding.btnAdd.setText(R.string.update_van)
            binding.chooseImage.setText(R.string.change_image)
            binding.vanImage.setImageBitmap(decodeImage(van.image64))
        }

        registerImagePickerCallback()
    }

    fun addVan(view: View) {
        van.title = binding.vanTitle.text.toString()
        van.description = binding.vanDescription.text.toString()
        if (van.title.isEmpty()) {
            Snackbar.make(view, "You must enter a title", Snackbar.LENGTH_LONG).show()
        } else {
            if (edit) {
                app.vans.update(van.copy())
            } else {
                app.vans.create(van.copy())
            }
            setResult(RESULT_OK)
            finish()
        }
    }

    fun addVanImage(view: View) {
        showImagePicker(imageIntentLauncher)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_van_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            val imageStream: InputStream? =
                                contentResolver.openInputStream(result.data!!.data!!)
                            val selectedImage = BitmapFactory.decodeStream(imageStream)
                            val encodedImage: String? = encodeImage(selectedImage)

                            binding.vanImage.setImageBitmap(selectedImage)
                            binding.chooseImage.setText(R.string.change_image)
                            println(encodedImage)
                            if (encodedImage != null) {
                                van.image64 = encodedImage
                            }
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}