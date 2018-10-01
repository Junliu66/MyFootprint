package com.example.junliuzhang.myfootprint

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.util.ArrayList

class ShowFeedsActivity : AppCompatActivity() {
    //recyclerview object
    private var recyclerView: RecyclerView? = null

    //adapter object
    private var adapter: RecyclerView.Adapter<*>? = null

    //database reference
    private var mDatabase: DatabaseReference? = null

    //progress dialog
    private var progressDialog: ProgressDialog? = null

    //list to hold all the uploaded images
    private var uploads: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_feeds)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)


        uploads = ArrayList<String>()

        val user = intent.getStringExtra("itemName")
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user).child("images")

        //adding an event listener to fetch values
        mDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (postSnapshot in snapshot.children) {
                    val upload = postSnapshot.value.toString()
                    Toast.makeText(applicationContext, upload, Toast.LENGTH_LONG).show()
                    uploads!!.add(upload)
                }
                //creating adapter
                adapter = MyAdapter(applicationContext, uploads!!)

                //adding adapter to recyclerview
                recyclerView!!.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                progressDialog!!.dismiss()
            }
        })

    }
}
