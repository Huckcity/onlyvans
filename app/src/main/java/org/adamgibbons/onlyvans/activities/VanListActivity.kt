package org.adamgibbons.onlyvans.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.adamgibbons.onlyvans.MainApp
import org.adamgibbons.onlyvans.R
import org.adamgibbons.onlyvans.adapters.VanAdapter
import org.adamgibbons.onlyvans.adapters.VanListener
import org.adamgibbons.onlyvans.databinding.ActivityVanListBinding
import org.adamgibbons.onlyvans.helpers.SwipeToDeleteCallback
import org.adamgibbons.onlyvans.models.VanModel

class VanListActivity : AppCompatActivity(), VanListener {

    private lateinit var app: MainApp
    private lateinit var binding: ActivityVanListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var vanList: List<VanModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        app = application as MainApp

        binding = ActivityVanListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        vanList = app.vans.findAll()
        loadVans()

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.recyclerView.adapter as VanAdapter
                val van = vanList[viewHolder.adapterPosition]
                adapter.removeAt(viewHolder.adapterPosition)
                app.vans.delete(van)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        registerRefreshCallback()
    }

    override fun onVanClick(van: VanModel) {
        val launcherIntent = Intent(this, VanActivity::class.java)
        launcherIntent.putExtra("van_edit", van)
        refreshIntentLauncher.launch(launcherIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_van_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, VanActivity::class.java)
                refreshIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadVans() }
    }

    private fun loadVans() {
        showVans()
    }

    private fun showVans() {
        binding.recyclerView.adapter = VanAdapter(app.vans.findAll() as ArrayList<VanModel>, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

}