package com.example.junliuzhang.myfootprint

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    internal lateinit var locationManager: LocationManager
    internal lateinit var locationListener: LocationListener
    private var mMap: GoogleMap? = null

    fun centerMapOnLocation(location: Location?, title: String) {
        if (location != null) {
            val userLocation = LatLng(location.latitude, location.longitude)
            mMap!!.clear()
            mMap!!.addMarker(MarkerOptions().position(userLocation).title(title))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10f))
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                centerMapOnLocation(lastKnownLocation, "Your Location")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap!!.setOnMapLongClickListener(this)

        val intent = intent
        if (intent.getIntExtra("placeNumber", 0) == 0) {
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    centerMapOnLocation(location, "Your Location")
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

                }

                override fun onProviderEnabled(provider: String) {

                }

                override fun onProviderDisabled(provider: String) {

                }
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                centerMapOnLocation(lastKnownLocation, "Your Location")
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        } else {
            val placeLocation = Location(LocationManager.GPS_PROVIDER)
            placeLocation.latitude = SavedLocationsActivity().locations.get(intent.getIntExtra("placeNumber", 0)).latitude
            placeLocation.longitude = SavedLocationsActivity().locations.get(intent.getIntExtra("placeNumber", 0)).longitude

            centerMapOnLocation(placeLocation, SavedLocationsActivity().places.get(intent.getIntExtra("placeNumber", 0)))
        }

    }

    override fun onMapLongClick(latLng: LatLng) {

        val geocoder = Geocoder(applicationContext, Locale.getDefault())

        var address = ""

        try {

            val listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (listAddresses != null && listAddresses.size > 0) {
                if (listAddresses[0].thoroughfare != null) {
                    if (listAddresses[0].subThoroughfare != null) {
                        address += listAddresses[0].subThoroughfare + " "
                    }

                    address += listAddresses[0].thoroughfare

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (address == "") {
            val sdf = SimpleDateFormat("HH:mm yyyy-MM-dd")
            address += sdf.format(Date())
        }

        mMap!!.addMarker(MarkerOptions().position(latLng).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

        SavedLocationsActivity().places?.add(address)
        SavedLocationsActivity().locations?.add(latLng)

        SavedLocationsActivity().arrayAdapter?.notifyDataSetChanged()

        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show()

    }
}
