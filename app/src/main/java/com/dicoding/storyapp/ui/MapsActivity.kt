package com.dicoding.storyapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.remote.retrofit.ApiClient
import com.dicoding.storyapp.remote.retrofit.ApiService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val apiService: ApiService by lazy {
        ApiClient.createApiClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        getStoriesFromApi()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun getStoriesFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getAllStories()

                withContext(Dispatchers.Main) {
                    if (response.error == false) {
                        val stories = response.listStory
                        if (!stories.isNullOrEmpty()) {
                            val builder = LatLngBounds.Builder()

                            for (storyItem in stories) {
                                storyItem?.let {
                                    val lat = it.lat ?: 0.0
                                    val lon = it.lon ?: 0.0
                                    val title = it.name ?: ""
                                    val position = LatLng(lat, lon)
                                    val description = it.description ?: ""

                                    val markerOptions = MarkerOptions()
                                        .position(position)
                                        .title(title)
                                        .snippet(description)
                                    mMap.addMarker(markerOptions)

                                    builder.include(position)
                                }
                            }

                            val bounds = builder.build()
                            val padding = 100
                            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                            mMap.moveCamera(cameraUpdate)
                        }
                    } else {
                        Toast.makeText(this@MapsActivity, response.message ?: "Failed to fetch stories.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MapsActivity, "Failed to fetch stories. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

