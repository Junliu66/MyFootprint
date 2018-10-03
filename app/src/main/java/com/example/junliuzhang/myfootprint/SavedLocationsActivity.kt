package com.example.junliuzhang.myfootprint

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

import com.google.android.gms.maps.model.LatLng

import java.util.ArrayList

class SavedLocationsActivity : AppCompatActivity() {

    var places = ArrayList<String>()
    var locations = ArrayList<LatLng>()
    lateinit var arrayAdapter: ArrayAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_locations)

        val listView = findViewById<ListView>(R.id.listView)

        places.add("Add a new places...")
        locations.add(LatLng(0.0, 0.0))

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, places)

        listView.adapter = arrayAdapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("placeNumber", position)

            startActivity(intent)
        }

    }
}
