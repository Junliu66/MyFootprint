package com.example.junliuzhang.myfootprint

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

class GeoLocationActivity:AppCompatActivity() {

    var locationManager:LocationManager? = null
    var locationListner:LocationListener? = null

    override fun onCreate(savedInstanceState:Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo_location)

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListner = object: LocationListener {

            override fun onLocationChanged(location:Location) {
                updateLocationInfo(location)
            }
            override fun onStatusChanged(provider:String, status:Int, extras:Bundle) {
            }
            override fun onProviderEnabled(provider:String) {
            }
            override fun onProviderDisabled(provider:String) {
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        else
        {
            this.locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.1f, this.locationListner)
            val lastKnownLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null)
            {
                updateLocationInfo(lastKnownLocation)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode:Int, @NonNull permissions:Array<String>, @NonNull grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            startListening()
        }
    }
    fun startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED)
        {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.1f, locationListner)
        }
    }
    fun updateLocationInfo(location:Location) {
        var latTextView : TextView? = findViewById(R.id.latTextView)
        val lonTextView : TextView? = findViewById(R.id.lonTextView)
        val accTextView : TextView? = findViewById(R.id.accTextView)
        val altTextView : TextView? = findViewById(R.id.altTextView)
        val addTextView : TextView? = findViewById(R.id.addTextView)

        latTextView?.setText("Latitude: " + java.lang.Double.toString(location.getLatitude()))
        lonTextView?.setText("Longitude: " + java.lang.Double.toString(location.getLongitude()))
        accTextView?.setText("Accuracy: " + java.lang.Double.toString(location.getAccuracy().toDouble()))
        altTextView?.setText("Altitude: " + java.lang.Double.toString(location.getAltitude()))

        var address = "Could not find address :("
        val geocoder = Geocoder(this, Locale.getDefault())
        try
        {
            val listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1)
            if (listAddresses != null && listAddresses.size > 0)
            {
                address = "Address:\n"
                if (listAddresses.get(0).thoroughfare != null)
                {
                    address += listAddresses.get(0).thoroughfare + "\n"
                }
                if (listAddresses.get(0).getLocality() != null)
                {
                    address += listAddresses.get(0).getLocality() + " "
                }
                if (listAddresses.get(0).getPostalCode() != null)
                {
                    address += listAddresses.get(0).getPostalCode() + " "
                }
                if (listAddresses.get(0).getAdminArea() != null)
                {
                    address += listAddresses.get(0).getAdminArea()
                }
            }
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
        addTextView?.setText(address)
    }
}

