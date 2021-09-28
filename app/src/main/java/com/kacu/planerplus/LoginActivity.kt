package com.kacu.planerplus

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.widget.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*





private var user: FirebaseUser? = null

class LoginActivity : AppCompatActivity() {


    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var userName: String? = null
    private var password: String? = null
    private var fail = false
    private var firebaseAuth: FirebaseAuth? = null

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private val TAG = "GoogleActivity"
    private var firebaseDatabase: FirebaseDatabase? = null
    private var id: String? = null
    private var userNameCustomUser: String? = null
    private var firebaseEmail: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()



        if (firebaseAuth!!.currentUser != null) {
            LogIn()
        }

        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        userName = ""
        password = ""


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }


    override fun onResume() {
        super.onResume()
        setStatusBarTransparent(this@LoginActivity)
    }


    private fun setStatusBarTransparent(activity: AppCompatActivity) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            activity.window.statusBarColor = Color.WHITE
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//
            activity.window.statusBarColor = Color.WHITE
        }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.button_signup -> {
                startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            }
            R.id.button_forgot_password -> {
                startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            }
            R.id.button_signin_custom_user -> {
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityIfNeeded(signInIntent, RC_SIGN_IN)
            }
            R.id.button_signin_provider -> {
                trySign()
            }

        }
    }

    private fun trySign() {
        userName = etEmail?.text.toString()
        password = etPassword?.text.toString()

        if (userName!!.isEmpty()) {
            fail = true
            etEmail!!.error = this@LoginActivity.resources.getString(R.string.loginEmpty)
        }
        if (password!!.isEmpty()) {
            fail = true
            etPassword!!.error = this@LoginActivity.resources.getString(R.string.passwordEmpty)
        }
        if (!fail) {
            singIn(userName!!, password!!)
        }
        fail = false
    }

    private fun singIn(userName: String, password: String) {
        firebaseAuth!!.signInWithEmailAndPassword(userName, password).addOnCompleteListener { task: Task<AuthResult?> ->
            if (task.isSuccessful) {

                if (firebaseAuth!!.currentUser!!.isEmailVerified) {
                    LogIn()
                    Toast.makeText(
                        this@LoginActivity,
                        this@LoginActivity.resources.getString(R.string.loginSucces),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    user = firebaseAuth!!.currentUser
                    firebaseAuth!!.signOut()
                    AlertEmail()
                }
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    this@LoginActivity.resources.getString(R.string.loginFail),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    fun LogIn() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()

    }


    private fun AlertEmail() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(this@LoginActivity.resources.getString(R.string.verification))
        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.alert_statue_registration, null)

        var textView: TextView? = mDialogView.findViewById<View>(R.id.textView) as TextView
        textView!!.text = this@LoginActivity.resources.getString(R.string.verificationText)



        alert.setPositiveButton(
            this@LoginActivity.resources.getString(R.string.next)
        ) { dialog: DialogInterface?, which: Int ->

        }



        alert.setNeutralButton(
            this@LoginActivity.resources.getString(R.string.sendAgain)
        ) { dialog: DialogInterface?, which: Int ->
            var fail = false
            try {
                user!!.sendEmailVerification()
            } catch (e: Exception) {
                fail = true
            } finally {
                if (!fail) {
                    Toast.makeText(
                        this@LoginActivity,
                        this@LoginActivity.resources.getString(R.string.sent),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        this@LoginActivity.resources.getString(R.string.fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }

        alert.setIcon(R.drawable.ic_launcher_foreground)
        alert.setView(mDialogView)
        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button2_bg)
        dialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(
                    TAG,
                    "firebaseAuthWithGoogle:" + account.id
                )

                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this@LoginActivity, "error 403", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    Log.d(
                        TAG,
                        "signInWithCredential:success"
                    )
                    getId()
                    ReadOrInitFromFirebase()


                } else {
                    Toast.makeText(this@LoginActivity, this@LoginActivity.resources.getString(R.string.errorInLogginGoogle), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getId() {


        firebaseEmail = Objects.requireNonNull(firebaseAuth!!.currentUser)!!.email
        val idNumber: String = FirebaseAuth.getInstance().currentUser!!.uid
        id = idNumber
    }



    private fun ReadOrInitFromFirebase() {
        var initSuccces = true

        try {
            firebaseDatabase = FirebaseDatabase.getInstance()

            firebaseDatabase!!.reference.child("Users").child(id!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.childrenCount.toInt() != 0) {

                        } else {
                            val userType: String? = "Custom user"
                            userNameCustomUser = firebaseAuth!!.currentUser!!.displayName

                            val person = Person(id, userNameCustomUser!!.lowercase(), firebaseEmail, userType, "Empty number")
                            firebaseDatabase!!.reference.child("Users").child(id!!).setValue(person)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        } catch (e: Exception) {
            initSuccces = false
        } finally {
            if (initSuccces) {

                LogIn()
                Toast.makeText(
                    this@LoginActivity,
                    this@LoginActivity.resources.getString(R.string.loginSucces),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                firebaseAuth!!.signOut()
                Toast.makeText(
                    this@LoginActivity,
                    this@LoginActivity.resources.getString(R.string.firebaseInitFail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}


