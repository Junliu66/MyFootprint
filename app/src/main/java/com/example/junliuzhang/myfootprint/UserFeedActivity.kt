package com.example.junliuzhang.myfootprint

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class UserFeedActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var userListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var keys: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_feed)

        userListView = findViewById(R.id.userListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        userListView?.adapter = adapter

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val email = p0?.child("email")?.value as String
                emails.add(email)
                keys.add(p0.key.toString())
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildRemoved(p0: DataSnapshot) {}

        })

       userListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val item = userListView?.getItemAtPosition(position).toString()
            val key = FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position)).key

            val intent = Intent(this, ShowFeedsActivity::class.java)
            intent.putExtra("itemName", key.toString())
            startActivity(intent)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.feeds, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.createFeed) {
            val intent = Intent(this, CreateFeedActivity::class.java)
            startActivity(intent)
        } else if (item?.itemId == R.id.yourLocation) {
            val intent = Intent(this, GeoLocationActivity::class.java)
            startActivity(intent)
        } else if (item?.itemId == R.id.favoritePlaces){
            val intent = Intent(this, SavedLocationsActivity::class.java)
            startActivity(intent)
        } else if (item?.itemId == R.id.logout) {
            mAuth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mAuth.signOut()
    }
}
