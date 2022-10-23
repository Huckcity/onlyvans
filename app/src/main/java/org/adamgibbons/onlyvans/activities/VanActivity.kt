package org.adamgibbons.onlyvans.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import org.adamgibbons.onlyvans.MainApp
import org.adamgibbons.onlyvans.R
import org.adamgibbons.onlyvans.databinding.ActivityVanBinding
import org.adamgibbons.onlyvans.models.VanMemStore
import org.adamgibbons.onlyvans.models.VanModel

class VanActivity : AppCompatActivity() {

    private var van = VanModel()
    private val vans = VanMemStore()
    private lateinit var binding: ActivityVanBinding
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        app = application as MainApp

    }

    fun addVan(view: View) {
        van.title = binding.vanTitle.text.toString()
        van.description = binding.vanDescription.text.toString()
        if (van.title.isEmpty()) {
            Snackbar.make(view, "You must enter a title", Snackbar.LENGTH_LONG).show()
        } else {
            app.vans.create(van.copy())
            setResult(RESULT_OK)
            finish()
        }
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
}