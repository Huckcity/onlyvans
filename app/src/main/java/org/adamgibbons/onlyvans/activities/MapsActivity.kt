package org.adamgibbons.onlyvans.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.adamgibbons.onlyvans.MainApp
import org.adamgibbons.onlyvans.R
import org.adamgibbons.onlyvans.databinding.ActivityMapsBinding
import org.adamgibbons.onlyvans.models.Location

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locations: ArrayList<Location>
    private var location = Location()
    private lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MainApp

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locations = arrayListOf()
        if (intent.hasExtra("location")) {
            locations.add(intent.extras?.getParcelable("location")!!)
        } else {
            app.vans.findAll().onEach {
                locations.add(it.location)
            }
            locations.add(Location(53.505696, -7.829102, 7f))
        }

        location = locations[0]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        locations.forEach { location ->
            val loc = LatLng(location.lat, location.lng)
            val options = MarkerOptions()
                .title("Van")
                .snippet("GPS : $loc")
                .draggable(true)
                .position(loc)
            map.addMarker(options)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, location.zoom))
            map.setOnMarkerDragListener(this)
        }

    }

    override fun onMarkerDrag(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
    }

    override fun onMarkerDragEnd(marker: Marker) {
    }

    override fun onMarkerDragStart(marker: Marker) {
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("location", location)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        super.onBackPressed()
    }

}