package com.zentrain.log

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Registration : AppCompatActivity() {
    val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser : FirebaseUser? = firebaseAuth.currentUser

    lateinit var loginEmail: String
    lateinit var loginPassword: String

    lateinit var email : EditText
    lateinit var password : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide(); // hide the title bar
        setContentView(R.layout.activity_registration)

        val tv:TextView = findViewById(R.id.tV)
         email = findViewById(R.id.email)
         password = findViewById(R.id.pswd)
        val confirmpassword:TextView = findViewById(R.id.cpswd)
        val btn:Button= findViewById(R.id.reg)


        btn.setOnClickListener {
            if (password.text.toString() == confirmpassword.text.toString()){
                signinuser()
            }else{
                Toast.makeText(this, "password do not match", Toast.LENGTH_SHORT).show()
            }

        }

        tv.setOnClickListener {
            val intent = Intent(this, login_mail::class.java)
            startActivity(intent)
        }
    }

    private fun signinuser() {
        val email_log = email.text.toString()
        val pwd_log = password.text.toString()

        firebaseAuth.createUserWithEmailAndPassword(email_log , pwd_log)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    startActivity(Intent(this, Aswin::class.java))
                    Toast.makeText(applicationContext,"Logged in successfully", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext,"sign in failed", Toast.LENGTH_LONG).show()
                }

            }
    }
}