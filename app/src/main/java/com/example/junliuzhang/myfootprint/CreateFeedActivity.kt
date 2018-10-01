package com.example.junliuzhang.myfootprint

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.provider.MediaStore
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.support.annotation.NonNull
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*


class CreateFeedActivity : AppCompatActivity() {

    var createFeedImageView: ImageView? = null
    var messageEditText: EditText? = null
    var imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_feed)

        createFeedImageView = findViewById(R.id.createFeedImageView)
        messageEditText = findViewById(R.id.messageEditText)
    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun chooseImageClicked(view: View) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1);
        } else {
            getPhoto();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                createFeedImageView?.setImageBitmap(bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    fun nextClicked(view: View) {
        createFeedImageView?.setDrawingCacheEnabled(true)
        createFeedImageView?.buildDrawingCache()
        val bitmap = createFeedImageView?.getDrawingCache()
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        //val currentUser = FirebaseAuth.getInstance().currentUser
        //val uploadTask = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser!!.uid).child("image").setValue(createSnapImageView)
        //FirebaseDatabase.getInstance().getReference().child("users").child(currentUser!!.uid).child("image").child("imageName").setValue(imageName)


        val imageRef = FirebaseStorage.getInstance().getReference().child("images").child(imageName)
        val uploadTask = imageRef.putBytes(data)


        imageRef.downloadUrl.addOnSuccessListener(object: OnSuccessListener<Uri> {
            override fun onSuccess(url: Uri) {
                //Toast.makeText(applicationContext, "hahaha", Toast.LENGTH_SHORT).show()
                val imageUrl = url.toString()
                //Toast.makeText(applicationContext, imageUrl, Toast.LENGTH_SHORT).show()
                val currentUser = FirebaseAuth.getInstance().currentUser
                FirebaseDatabase.getInstance().reference.child("users").child(currentUser!!.uid).child("images")
                        .child(UUID.randomUUID().toString()).setValue(url.toString())

                val intent = Intent(applicationContext, UserFeedActivity::class.java)
                startActivity(intent)
            }

        }).addOnFailureListener({
            Toast.makeText(this, "UploadFailed", Toast.LENGTH_SHORT).show()
        })

        /**
        uploadTask.addOnFailureListener ({
        Toast.makeText(this, "UploadFailed", Toast.LENGTH_SHORT).show()
        }).addOnSuccessListener ({ taskSnapshot ->

        val currentUser = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUser!!.uid).child("image")
        .child(UUID.randomUUID().toString()).setValue(taskSnapshot.storage.downloadUrl.toString())
        //Log.i("URL", taskSnapshot.toString())

        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
        })**/
    }




}
