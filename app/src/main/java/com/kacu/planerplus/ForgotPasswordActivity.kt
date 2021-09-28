package com.kacu.planerplus

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private var etEmail: EditText? = null
    private var email: String? = null
    private var firebaseAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        firebaseAuth = FirebaseAuth.getInstance()
        etEmail = findViewById<View>(R.id.et_email_forgotten) as EditText
        email = ""

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

    }
    override fun onResume() {
        super.onResume()
        setStatusBarTransparent(this@ForgotPasswordActivity)
    }

    fun onClick(view: View) {
        if(view.id == R.id.button_send) {
            resetPassword()
        }
    }

    private fun resetPassword()
    {
        email = etEmail!!.text.toString()
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            firebaseAuth!!.sendPasswordResetEmail(email.toString())
            Toast.makeText(this@ForgotPasswordActivity, this@ForgotPasswordActivity.resources.getString(R.string.emailSend) + email, Toast.LENGTH_SHORT).show()
        }
        else
        {
            etEmail!!.error = this@ForgotPasswordActivity.resources.getString(R.string.validEmail)
        }
    }


    private fun setStatusBarTransparent(activity: AppCompatActivity){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            activity.window.statusBarColor = Color.WHITE
        }
        else{
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//
            activity.window.statusBarColor = Color.WHITE
        }
    }



}