package com.example.junliuzhang.myfootprint

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        if (mAuth.currentUser != null) {
            logIn()
        }
    }

    fun goClicked(view: View) {
        mAuth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString()).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                logIn()

            } else {
                mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString()).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(task.result.user.uid).child("email").setValue(emailEditText?.text.toString())
                        //FirebaseDatabase.getInstance().getReference().child("users").setValue("Hello World!")
                        //FirebaseDatabase.getInstance().getReference().child("users").child
                        logIn()
                    } else {
                        Toast.makeText(this, "Login Failed. Try Again!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // ...
        }

    }

    fun logIn() {
        val intent = Intent(this, UserFeedActivity::class.java)
        startActivity(intent)
    }
}

