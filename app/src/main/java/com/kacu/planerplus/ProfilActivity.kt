package com.kacu.planerplus

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*

import com.google.android.material.snackbar.Snackbar
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView

import android.widget.LinearLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.telephony.TelephonyManager

import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ProfilActivity : AppCompatActivity() {

    private var id: String? = null
    private var userName: String? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null

    private var tvName: TextView? = null
    private var tvType: TextView? = null
    private var userType: String? = null
    private var user: FirebaseUser? = null
    private var email: String? = null
    private var viewOnClick: View? = null

    private var cardPassword: CardView? = null
    private var cardPhone: CardView? = null
    private var cardAdress: CardView? = null
    private var cardClients: CardView? = null
    private var cardName: CardView? = null
    private var cardProviders: CardView? = null
    private var linearLayoutProvider: LinearLayout? = null
    private var linearLayoutUser: LinearLayout? = null
    private var allCardsView: LinearLayout? = null
    private var imageLayout: RelativeLayout? = null
    private var progressBar: ProgressBar? = null
    var storageReference: StorageReference? = null

    private var tableEmail= arrayOfNulls<String>(0)
    private var tablePass= arrayOfNulls<String>(0)
    private var tableImageId = arrayOfNulls<String>(0)
    private var tableTypeInt = IntArray(0)
    private var tablePositionInt= IntArray(0)
    private var profileImageView: CircleImageView? = null
    private val RC_SIGN_IN = 9001
    private var mGoogleSignInClient: GoogleSignInClient? = null


    private var progressBarDialog: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)





        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        profileImageView = findViewById<View>(R.id.circleImageView) as CircleImageView

        val profileRef =
            storageReference!!.child("users/" + firebaseAuth!!.currentUser!!.uid + "/profile.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(profileImageView)
        }
        tvName = findViewById<View>(R.id.tvName) as TextView
        tvType = findViewById<View>(R.id.tvType) as TextView
        cardPassword = findViewById<View>(R.id.cardViewChangePassword) as CardView
        cardPhone = findViewById<View>(R.id.cardViewChangePhone) as CardView
        cardAdress = findViewById<View>(R.id.cardViewChangeAddress) as CardView
        cardClients = findViewById<View>(R.id.CardViewClients) as CardView
        cardName = findViewById<View>(R.id.cardViewChangeName) as CardView
        cardProviders = findViewById<View>(R.id.cardViewProviderList) as CardView

        linearLayoutProvider = findViewById<View>(R.id.hideWhenNormalUser1) as LinearLayout
        linearLayoutUser = findViewById<View>(R.id.onlyNormalUser) as LinearLayout
        allCardsView = findViewById<View>(R.id.allCardView) as LinearLayout
        imageLayout = findViewById<View>(R.id.signup_image_layout) as RelativeLayout
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        progressBar!!.max = 1000





        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        if (firebaseAuth!!.currentUser != null) {
            getId()
            loadInfo()
            user = firebaseAuth!!.currentUser

        } else {
            LogOut()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.cardViewChangePassword -> {
                viewOnClick = view
                AlertChangePassword("", "", "", false)
            }
            R.id.cardViewChangeName -> {
                viewOnClick = view
                alertChangeName()
            }
            R.id.cardViewChangePhone -> {
                viewOnClick = view
                alertChangePhone()
            }
            R.id.cardViewChangeAddress -> {
                viewOnClick = view
                AlertChangeAddress("", "", "", "", "")
            }
            R.id.CardViewClients -> {
                val intent = Intent(this, MyUsersListActivity::class.java)
                startActivity(intent)
            }
            R.id.cardViewProviderList -> {
                val intent = Intent(this, MyServiceProvidersListActivity::class.java)
                startActivity(intent)
            }
            R.id.cardViewChangePicture -> {
                val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(openGalleryIntent)
            }
            R.id.saveAcc -> {
                viewOnClick = view
                if(userType == "Service provider")
                {
                    changeAccNotSaved()
                }
                else
                {
                    val snackbar_fail = Snackbar
                        .make(
                            viewOnClick!!,
                            this@ProfilActivity.resources.getString(R.string.onlyProviderFunction) + "\n"+
                                    this@ProfilActivity.resources.getString(R.string.googleUsersHaveInstaSavedAcc),
                            Snackbar.LENGTH_LONG
                        )
                    snackbar_fail.show()
                }


            }
            R.id.swapAcc -> {
                loadUsersList()
            }
        }



    }

    private fun alertChangeName() {
        val alert = androidx.appcompat.app.AlertDialog.Builder(this)
        val edittext = EditText(this)
        edittext.hint = this@ProfilActivity.resources.getString(R.string.newNameHint)
        edittext.setTextColor(Color.BLACK)
        alert.setMessage(this@ProfilActivity.resources.getString(R.string.newNameText))
        alert.setTitle(this@ProfilActivity.resources.getString(R.string.newNameTitle))
        alert.setView(edittext)
        alert.setPositiveButton(
            this@ProfilActivity.resources.getString(R.string.next)
        ) { dialog: DialogInterface?, whichButton: Int ->

            var fail = false
            val newName: String? = edittext.text.toString().lowercase()

            if (newName!!.isEmpty()) {
                fail = true
            }
            if (!fail) {

                if (userType == "Service provider") {

                    try {
                        firebaseDatabase!!.reference.child("Providers").child(id!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.childrenCount.toInt() != 0) {
                                        val newPerson = dataSnapshot.getValue(Person::class.java)
                                        val companyAdress: String? = newPerson!!.companyAdress
                                        val companyPhoneNumber: String? = newPerson.companyPhone
                                        val companyCattegry = newPerson.companyCattegory

                                        val person = Person(
                                            id,
                                            newName,
                                            email,
                                            userType,
                                            companyAdress,
                                            companyPhoneNumber,
                                            companyCattegry
                                        )
                                        firebaseDatabase!!.reference.child("Providers")
                                            .child(id.toString()).setValue(person)
                                        tvType!!.text =
                                            this@ProfilActivity.resources.getString(R.string.profileType) + userType
                                        tvName!!.text = newName
                                        val snackbar_fail = Snackbar
                                            .make(
                                                viewOnClick!!,
                                                this@ProfilActivity.resources.getString(R.string.changeSuccesfull),
                                                Snackbar.LENGTH_LONG
                                            )
                                        snackbar_fail.show()
                                    } else {
                                        Toast.makeText(
                                            this@ProfilActivity,
                                            this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ProfilActivity,
                            this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                } else if (userType == "Custom user") {
                    try {
                        firebaseDatabase!!.reference.child("Users").child(id!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                @SuppressLint("SetTextI18n")
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.childrenCount.toInt() != 0) {
                                        val userPerson: Person? = dataSnapshot.getValue(Person::class.java)
                                        val phoneNumber: String = userPerson!!.companyPhone
                                        val person = Person(id, newName, email, userType, phoneNumber)
                                        firebaseDatabase!!.reference.child("Users")
                                            .child(id.toString()).setValue(person)
                                        tvType!!.text =
                                            this@ProfilActivity.resources.getString(R.string.profileType) + userType
                                        tvName!!.text = newName
                                        val snackbar_fail = Snackbar
                                            .make(
                                                viewOnClick!!,
                                                this@ProfilActivity.resources.getString(R.string.changeSuccesfull),
                                                Snackbar.LENGTH_LONG
                                            )
                                        snackbar_fail.show()
                                    } else {
                                        Toast.makeText(
                                            this@ProfilActivity,
                                            this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ProfilActivity,
                            this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

            } else {
                Toast.makeText(
                    this@ProfilActivity,
                    this@ProfilActivity.resources.getString(R.string.emptyName),
                    Toast.LENGTH_SHORT
                ).show()
                alertChangeName()
            }
        }
        alert.setNegativeButton(
            this@ProfilActivity.resources.getString(R.string.cancel)
        ) { dialogInterface: DialogInterface?, i: Int ->
            Toast.makeText(
                this@ProfilActivity,
                this@ProfilActivity.resources.getString(R.string.canceled),
                Toast.LENGTH_SHORT
            ).show()
        }
        val dialog = alert.create()
        dialog.setIcon(R.drawable.ic_launcher_foreground)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)
        dialog.show()
    }


    private fun AlertChangePassword(
        oldPass: String,
        newPass: String,
        newPassConfirm: String,
        f: Boolean) {

        val alert = AlertDialog.Builder(this)
        alert.setTitle(this@ProfilActivity.resources.getString(R.string.changePassword))

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_change_password, null)
        val etOldPassword: EditText = mDialogView.findViewById<View>(R.id.etOldPassword) as EditText
        val etNewPassword: EditText = mDialogView.findViewById<View>(R.id.etNewPassword) as EditText
        val etConfirmNewPassword: EditText =
            mDialogView.findViewById<View>(R.id.etConfirmNewPassword) as EditText



        alert.setView(etNewPassword)
        alert.setView(etOldPassword)
        alert.setView(etConfirmNewPassword)

        etOldPassword.setText(oldPass)
        etNewPassword.setText(newPass)
        etConfirmNewPassword.setText(newPassConfirm)

        var fail = f
        var oldPassword: String = etOldPassword.text.toString()
        var newPassword: String = etNewPassword.text.toString()
        var newPasswordConfirm: String = etConfirmNewPassword.text.toString()



        if (oldPassword.isEmpty()) {
            etOldPassword.error = this@ProfilActivity.resources.getString(R.string.passwordEmpty)
        }
        if (newPassword.length < 8) {
            etNewPassword.error = this@ProfilActivity.resources.getString(R.string.noPassword)
        }

        if (newPasswordConfirm.length < 8) {
            etConfirmNewPassword.error =
                this@ProfilActivity.resources.getString(R.string.noPassword)
        } else if (newPassword != newPasswordConfirm) {
            etNewPassword.error = this@ProfilActivity.resources.getString(R.string.noSamePassword)
            etConfirmNewPassword.error =
                this@ProfilActivity.resources.getString(R.string.noSamePassword)
        }


        alert.setPositiveButton(
            this@ProfilActivity.resources.getString(R.string.next)
        ) { dialog: DialogInterface?, which: Int ->

            oldPassword = etOldPassword.text.toString()
            newPassword = etNewPassword.text.toString()
            newPasswordConfirm = etConfirmNewPassword.text.toString()

            if (oldPassword.isEmpty()) {
                fail = true
            }
            if (newPassword.isEmpty() || newPasswordConfirm.isEmpty()) {
                fail = true

            } else if (newPassword != newPasswordConfirm) {
                fail = true
            }

            if (!fail) {
                user = firebaseAuth!!.currentUser
                val credential = EmailAuthProvider.getCredential(email!!, oldPassword)
                user!!.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user!!.updatePassword(newPassword).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val snackbar_fail = Snackbar
                                    .make(
                                        viewOnClick!!,
                                        this@ProfilActivity.resources.getString(R.string.changeSuccesfull),
                                        Snackbar.LENGTH_LONG
                                    )
                                snackbar_fail.show()
                            } else {
                                val snackbar_su = Snackbar
                                    .make(
                                        viewOnClick!!,
                                        this@ProfilActivity.resources.getString(R.string.fail),
                                        Snackbar.LENGTH_LONG
                                    )
                                snackbar_su.show()
                            }
                        }

                    } else {
                        AlertChangePassword(oldPassword, newPassword, newPasswordConfirm, false)
                        Toast.makeText(
                            this@ProfilActivity,
                            this@ProfilActivity.resources.getString(R.string.oldPassBad),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    this@ProfilActivity,
                    this@ProfilActivity.resources.getString(R.string.fail),
                    Toast.LENGTH_SHORT
                ).show()
                AlertChangePassword(oldPassword, newPassword, newPasswordConfirm, false)
            }

        }

        alert.setNegativeButton(
            this@ProfilActivity.resources.getString(R.string.cancel)
        ) { _: DialogInterface?, i: Int ->
            Toast.makeText(
                this@ProfilActivity,
                this@ProfilActivity.resources.getString(R.string.canceled),
                Toast.LENGTH_SHORT
            ).show()
        }

        alert.setIcon(R.drawable.ic_launcher_foreground)
        alert.setView(mDialogView)
        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)
        dialog.show()
    }

    private fun LogOut() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
    }

    private fun getId() {
        email = Objects.requireNonNull(firebaseAuth!!.currentUser)!!.email
        val idNumber: String = FirebaseAuth.getInstance().currentUser!!.uid
        id = idNumber
    }


    private fun loadInfo() {
        try {
            firebaseDatabase!!.reference.child("Providers").child(id!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.childrenCount.toInt() != 0) {
                            val newPerson = dataSnapshot.getValue(
                                Person::class.java
                            )
                            userType = newPerson!!.userType
                            val userNameProvider = newPerson.name
                            if (userType == "Service provider") {
                                tvType!!.text =
                                    this@ProfilActivity.resources.getString(R.string.profileType) + userType
                                tvName!!.text = userNameProvider
                                loadUi()

                            }

                        } else {
                            firebaseDatabase!!.reference.child("Users").child(id!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.childrenCount.toInt() != 0) {
                                            val newPerson = snapshot.getValue(
                                                Person::class.java
                                            )
                                            userType = newPerson!!.userType
                                            val currentPhone = newPerson!!.companyPhone
                                            val userNameClinet = newPerson.name
                                            if (userType == "Custom user") {
                                                tvType!!.text =
                                                    this@ProfilActivity.resources.getString(R.string.profileType) + userType
                                                tvName!!.text = userNameClinet
                                                loadUi()
                                                if (currentPhone=="Empty number")
                                                {
                                                    alertChangePhone()
                                                }
                                            }
                                            else {
                                                Toast.makeText(this@ProfilActivity, this@ProfilActivity.resources.getString(R.string.firebaseInitFail), Toast.LENGTH_LONG).show()

                                                loadUi()
                                            }
                                        } else {
                                        Toast.makeText(this@ProfilActivity, this@ProfilActivity.resources.getString(R.string.firebaseInitFail), Toast.LENGTH_LONG).show()

                                            loadUi()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        } catch (e: Exception) {
            Toast.makeText(
                this@ProfilActivity,
                this.resources.getString(R.string.firebaseInitFail),
                Toast.LENGTH_LONG
            ).show()
            tvType!!.text = this@ProfilActivity.resources.getString(R.string.failLoad)
            tvName!!.text = this@ProfilActivity.resources.getString(R.string.failLoad)
        }
    }

    override fun onResume() {
        super.onResume()
        loadUi()
        val random = Random()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun loadUi() {
        var fail = false
        if (userType == "Custom user") {
            cardPassword!!.visibility = View.GONE
            linearLayoutProvider!!.visibility = View.GONE
            linearLayoutUser!!.visibility = View.VISIBLE
            allCardsView!!.visibility = View.VISIBLE
        } else if (userType == "Service provider") {
            if (linearLayoutProvider!!.visibility == View.GONE) {
                cardPassword!!.visibility = View.VISIBLE
                linearLayoutProvider!!.visibility = View.VISIBLE
                linearLayoutUser!!.visibility = View.GONE
            }
            allCardsView!!.visibility = View.VISIBLE
        } else {
            fail = true
        }

        if (fail) {

            progressBar!!.visibility = View.VISIBLE
            allCardsView!!.visibility = View.GONE
            imageLayout!!.visibility = View.GONE



            ObjectAnimator.ofInt(progressBar, "progress", 1000).setDuration(2000).start()

        } else {
            if (allCardsView!!.visibility == View.GONE || imageLayout!!.visibility == View.GONE || progressBar!!.visibility == View.VISIBLE) {
                allCardsView!!.visibility = View.VISIBLE
                imageLayout!!.visibility = View.VISIBLE
                progressBar!!.visibility = View.GONE
            }
        }
    }


    private fun alertChangePhone() {
        val alert = androidx.appcompat.app.AlertDialog.Builder(this)
        val edittext = EditText(this)
        edittext.hint = this@ProfilActivity.resources.getString(R.string.newPhoneHint)
        edittext.setTextColor(Color.BLACK)
        edittext.inputType = 3
        alert.setMessage(this@ProfilActivity.resources.getString(R.string.newPhoneText))
        alert.setTitle(this@ProfilActivity.resources.getString(R.string.newPhoneTitle))
        alert.setView(edittext)
        alert.setPositiveButton(
            this@ProfilActivity.resources.getString(R.string.next)
        ) { dialog: DialogInterface?, whichButton: Int ->

            var fail = false
            val newPhone: String? = edittext.text.toString()

            if (newPhone!!.length < 9) {
                fail = true
            }
            if (!fail) {

                if (userType == "Service provider") {

                    try {
                        firebaseDatabase!!.reference.child("Providers").child(id!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.childrenCount.toInt() != 0) {
                                        val newPerson = dataSnapshot.getValue(Person::class.java)
                                        val companyAdress: String? = newPerson!!.companyAdress
                                        val comapnyName: String? = newPerson.name
                                        val companyCattegry: String? = newPerson.companyCattegory

                                        val person = Person(
                                            id,
                                            comapnyName,
                                            email,
                                            userType,
                                            companyAdress,
                                            newPhone,
                                            companyCattegry
                                        )
                                        firebaseDatabase!!.reference.child("Providers")
                                            .child(id.toString()).setValue(person)
                                        val snackbar_fail = Snackbar
                                            .make(
                                                viewOnClick!!,
                                                this@ProfilActivity.resources.getString(R.string.changeSuccesfull),
                                                Snackbar.LENGTH_LONG
                                            )
                                        snackbar_fail.show()
                                    } else {
                                        Toast.makeText(
                                            this@ProfilActivity,
                                            this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ProfilActivity,
                            this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                }else if (userType == "Custom user")
                {
                    try {
                        firebaseDatabase!!.reference.child("Users").child(id!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.childrenCount.toInt() != 0) {
                                        val newPerson = dataSnapshot.getValue(Person::class.java)
                                        val comapnyName: String? = newPerson!!.name

                                        val person = Person(
                                            id, comapnyName, email, userType, newPhone)
                                        firebaseDatabase!!.reference.child("Users")
                                            .child(id.toString()).setValue(person)
                                        val snackbar_fail = Snackbar
                                            .make(
                                                viewOnClick!!,
                                                this@ProfilActivity.resources.getString(R.string.changeSuccesfull),
                                                Snackbar.LENGTH_LONG
                                            )
                                        snackbar_fail.show()
                                    } else {
                                        Toast.makeText(
                                            this@ProfilActivity,
                                            this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ProfilActivity,
                            this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                else {
                    Toast.makeText(
                        this@ProfilActivity,
                        this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            } else {
                Toast.makeText(
                    this@ProfilActivity,
                    this@ProfilActivity.resources.getString(R.string.noPhoneNumber),
                    Toast.LENGTH_SHORT
                ).show()
                alertChangeName()
            }
        }
        alert.setNegativeButton(
            this@ProfilActivity.resources.getString(R.string.cancel)
        ) { dialogInterface: DialogInterface?, i: Int ->
            Toast.makeText(
                this@ProfilActivity,
                this@ProfilActivity.resources.getString(R.string.canceled),
                Toast.LENGTH_SHORT
            ).show()
        }
        val dialog = alert.create()
        dialog.setIcon(R.drawable.ic_launcher_foreground)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)
        dialog.show()
    }


    private fun AlertChangeAddress(
        postalCodeInit: String,
        cityInit: String,
        streetInit: String,
        buildInit: String,
        doorInit: String
    ) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(this@ProfilActivity.resources.getString(R.string.newAdress))
        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.alert_registration_provider_adress, null)
        val etPostalCode: EditText = mDialogView.findViewById(R.id.etPostalCode)
        val etCity: EditText = mDialogView.findViewById(R.id.etCity)
        val etStreet: EditText = mDialogView.findViewById(R.id.etStreet)
        val etBuildNumber: EditText = mDialogView.findViewById(R.id.etBuildNumber)
        val etDoorNumber: EditText = mDialogView.findViewById(R.id.etDoorNumber)

        val et1: TextView = mDialogView.findViewById(R.id.et1)
        val et2: TextView = mDialogView.findViewById(R.id.et2)
        val et3: TextView = mDialogView.findViewById(R.id.et3)
        val et4: TextView = mDialogView.findViewById(R.id.et4)
        val et5: TextView = mDialogView.findViewById(R.id.et5)

        et1.setTextColor(Color.BLACK)
        et2.setTextColor(Color.BLACK)
        et3.setTextColor(Color.BLACK)
        et4.setTextColor(Color.BLACK)
        et5.setTextColor(Color.BLACK)

        etPostalCode.setTextColor(Color.BLACK)
        etCity.setTextColor(Color.BLACK)
        etStreet.setTextColor(Color.BLACK)
        etBuildNumber.setTextColor(Color.BLACK)
        etDoorNumber.setTextColor(Color.BLACK)


        alert.setView(etPostalCode)
        alert.setView(etCity)
        alert.setView(etStreet)
        alert.setView(etBuildNumber)
        alert.setView(etDoorNumber)

        var newAdress: String

        var postalCode: String = postalCodeInit
        var city: String = cityInit
        var street: String = streetInit
        var buildNumber: String = buildInit
        var doorNumber: String = doorInit

        etPostalCode.setText(postalCode)
        etCity.setText(city)
        etStreet.setText(street)
        etBuildNumber.setText(buildNumber)
        etDoorNumber.setText(doorNumber)

        alert.setPositiveButton(
            this@ProfilActivity.resources.getString(R.string.next)
        ) { dialog: DialogInterface?, which: Int ->

            postalCode = etPostalCode.text.toString()
            city = etCity.text.toString()
            street = etStreet.text.toString()
            buildNumber = etBuildNumber.text.toString()
            doorNumber = etDoorNumber.text.toString()

            if (postalCode.isEmpty() || city.isEmpty() || street.isEmpty() || buildNumber.isEmpty()) {
                Toast.makeText(
                    this@ProfilActivity,
                    this@ProfilActivity.resources.getString(R.string.notFullAdress),
                    Toast.LENGTH_SHORT
                ).show()
                AlertChangeAddress(postalCode, city, street, buildNumber, doorNumber)
            } else {
                val streetString = this@ProfilActivity.resources.getString(R.string.street)
                if (doorNumber.isEmpty()) {
                    newAdress = "$postalCode  -  $city,   \n $streetString $street $buildNumber"
                } else {
                    newAdress = "$postalCode  -  $city,   \n $streetString $street $buildNumber/$doorNumber"
                }

                if (userType == "Service provider") {

                    try {
                        firebaseDatabase!!.reference.child("Providers").child(id!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.childrenCount.toInt() != 0) {
                                        val newPerson = dataSnapshot.getValue(Person::class.java)
                                        val companyPhone: String? = newPerson!!.companyPhone
                                        val comapnyName: String? = newPerson.name
                                        val companyCattegry = newPerson.companyCattegory
                                        val person = Person(
                                            id,
                                            comapnyName,
                                            email,
                                            userType,
                                            newAdress,
                                            companyPhone,
                                            companyCattegry
                                        )
                                        firebaseDatabase!!.reference.child("Providers")
                                            .child(id.toString()).setValue(person)
                                        val snackbar_fail = Snackbar
                                            .make(
                                                viewOnClick!!,
                                                this@ProfilActivity.resources.getString(R.string.changeSuccesfull),
                                                Snackbar.LENGTH_LONG
                                            )
                                        snackbar_fail.show()
                                    } else {
                                        Toast.makeText(
                                            this@ProfilActivity,
                                            this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ProfilActivity,
                            this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                } else {
                    Toast.makeText(
                        this@ProfilActivity,
                        this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }


        }
        alert.setNegativeButton(
            this@ProfilActivity.resources.getString(R.string.cancel)
        ) { dialog: DialogInterface?, which: Int ->
            Toast.makeText(
                this@ProfilActivity,
                this@ProfilActivity.resources.getString(R.string.canceled),
                Toast.LENGTH_SHORT
            ).show()
        }
        alert.setIcon(R.drawable.ic_launcher_foreground)
        alert.setView(mDialogView)
        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)
        dialog.show()
    }





    private fun changeAccNotSaved() {
        email

        var wasSaved = false

        val questionsDatabaseHelper: SQLiteOpenHelper = QuestionsDatabaseHelper(this)
        val db = questionsDatabaseHelper.writableDatabase

        val c = db.query("USERS", null, null, null, null, null, null)

        var checkEmail: String

        c.moveToFirst()
        if (c.moveToFirst()) {

            checkEmail = c.getString(c.getColumnIndex("EMAIL"))
            if (checkEmail == email) {
                wasSaved = true
            }


            while (c.moveToNext()) {
                checkEmail = c.getString(c.getColumnIndex("EMAIL"))
                if (checkEmail == email) {
                    wasSaved = true
                }
            }
        }

        c.close()
        db.close()

        if (wasSaved) {
            Toast.makeText(
                this@ProfilActivity,
                this@ProfilActivity.resources.getString(R.string.accWasSaved), Toast.LENGTH_SHORT
            ).show()
        } else {
            if (userType == "Service provider") {
                alertgetPass("")

            } else {
                Toast.makeText(
                    this@ProfilActivity,
                    this@ProfilActivity.resources.getString(R.string.firebaseInitFail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun saveAccount(typeThis: Int, emailThis: String, passThis: String, uID: String) {
        var failSave = false
        try {
            val questionsDatabaseHelper: SQLiteOpenHelper =
                QuestionsDatabaseHelper(this@ProfilActivity)
            val db = questionsDatabaseHelper.writableDatabase

            val userValues = ContentValues()
            userValues.put("TYPE", typeThis)
            userValues.put("EMAIL", emailThis)
            userValues.put("PASSWORD", passThis)
            userValues.put("UID", uID)

            db.insert("USERS", null, userValues)
            db.close()
        } catch (e: Exception) {
            failSave = true
            Toast.makeText(
                this@ProfilActivity,
                this@ProfilActivity.resources.getString(R.string.SQLiteExpection),
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            if (!failSave) {
                Toast.makeText(
                    this@ProfilActivity,
                    this@ProfilActivity.resources.getString(R.string.savingComplete),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun alertgetPass(thisPass: String) {
        val alert = androidx.appcompat.app.AlertDialog.Builder(this)
        val edittext = EditText(this)
        edittext.hint = this@ProfilActivity.resources.getString(R.string.hint_password)
        edittext.setTextColor(Color.BLACK)
        edittext.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD


        alert.setMessage(this@ProfilActivity.resources.getString(R.string.confirmPassword))
        alert.setTitle(this@ProfilActivity.resources.getString(R.string.savingAccount))
        alert.setView(edittext)
        var passConfirm: String = thisPass
        edittext.setText(passConfirm)

        alert.setPositiveButton(this@ProfilActivity.resources.getString(R.string.next)) { dialog: DialogInterface?, whichButton: Int ->
            passConfirm = edittext.text.toString()
            if(passConfirm.isEmpty())
            {
                passConfirm =" "
            }
            user = firebaseAuth!!.currentUser
            val credential = EmailAuthProvider.getCredential(email!!, passConfirm)
            user!!.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
               saveAccount(2, email!!, passConfirm, user!!.uid)
                }
                else {
                 alertgetPass(passConfirm)
                    Toast.makeText(
                        this@ProfilActivity,
                        this@ProfilActivity.resources.getString(R.string.passwordIncorrect),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            }
            alert.setNegativeButton(
                this@ProfilActivity.resources.getString(R.string.cancel)
            ) { dialogInterface: DialogInterface?, i: Int ->
                Toast.makeText(
                    this@ProfilActivity,
                    this@ProfilActivity.resources.getString(R.string.canceled),
                    Toast.LENGTH_SHORT
                ).show()
            }
            val dialog = alert.create()
            dialog.setIcon(R.drawable.ic_launcher_foreground)
            dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)
            dialog.show()
        }


    @SuppressLint("UseCompatLoadingForDrawables")
    private  fun showListAccounts(
        ThisEmailTable: Array<String?>, ThisPassTable: Array<String?>,
        ThisIdTypeTable: IntArray, ThisImagesTable: Array<String?>, ThisTablePositionInt: IntArray) {

        val alert = AlertDialog.Builder(this)
        alert.setTitle(this@ProfilActivity.resources.getString(R.string.changeAccount))

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_accounts_list, null)
        val accountsListRecyclerView: RecyclerView? =mDialogView.findViewById(R.id.Accounts_recycler_view)


        val button = mDialogView.findViewById<Button>(R.id.button_signin_custom_user)
        button.setOnClickListener {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityIfNeeded(signInIntent, RC_SIGN_IN)
        }

        val adapter = CardCaptionedAccountsAdapter(tableImageId, tableEmail)
        accountsListRecyclerView!!.adapter = adapter
        val layoutManager = LinearLayoutManager(this@ProfilActivity)
        accountsListRecyclerView.layoutManager = layoutManager



        alert.setNegativeButton(
            this@ProfilActivity.resources.getString(R.string.cancel)
        ) { _: DialogInterface?, i: Int ->

        }

        alert.setIcon(R.drawable.ic_launcher_foreground)
        alert.setView(mDialogView)
        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)



        adapter.setListener { position ->
            alertManagmentAccounts(position, ThisEmailTable, ThisPassTable,  ThisIdTypeTable, ThisImagesTable, ThisTablePositionInt )
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun loadUsersList() {
        var loadingUsersFail = false
        try {
            val questionsDatabaseHelper: SQLiteOpenHelper = QuestionsDatabaseHelper(this)
            val db = questionsDatabaseHelper.writableDatabase

            val c = db.query("USERS", null, null, null, null, null, null)
            val count = DatabaseUtils.queryNumEntries(db, "USERS")
            val countInt = count.toInt()
            tableTypeInt = IntArray(countInt)
            tablePositionInt = IntArray(countInt)
            tableImageId = arrayOfNulls<String>(countInt)
            tableEmail = arrayOfNulls<String>(countInt)
            tablePass = arrayOfNulls<String>(countInt)


            c.moveToFirst()
            if (c.moveToFirst()) {
                var cursorPosition: Int = 0

                var idToTable: Int = c.getInt(c.getColumnIndex("_id"))
                var emailString: String = c.getString(c.getColumnIndex("EMAIL"))
                var passString: String = c.getString(c.getColumnIndex("PASSWORD"))
                var typeUserInt: Int = c.getInt(c.getColumnIndex("TYPE"))
                var imageIdInt: String = c.getString(c.getColumnIndex("UID"))


                tablePass[cursorPosition] = passString
                tableEmail[cursorPosition] = emailString
                tableTypeInt[cursorPosition] = typeUserInt
                tableImageId[cursorPosition] = imageIdInt
                tablePositionInt[cursorPosition] = idToTable




                while (c.moveToNext()) {
                    cursorPosition++

                    idToTable =c.getInt(c.getColumnIndex("_id"))
                    emailString = c.getString(c.getColumnIndex("EMAIL"))
                    passString = c.getString(c.getColumnIndex("PASSWORD"))
                    typeUserInt = c.getInt(c.getColumnIndex("TYPE"))
                       imageIdInt= c.getString(c.getColumnIndex("UID"))

                    tablePass[cursorPosition] = passString
                    tableEmail[cursorPosition] = emailString
                    tableTypeInt[cursorPosition] = typeUserInt
                    tableImageId[cursorPosition] = imageIdInt
                    tablePositionInt[cursorPosition] = idToTable

                }
            } else {
                loadingUsersFail = true
                Toast.makeText(
                    this@ProfilActivity,
                    this@ProfilActivity.resources.getString(R.string.noUsersToLoad),
                    Toast.LENGTH_SHORT
                ).show()
            }

            c.close()
            db.close()
        } catch (e: Exception) {
            loadingUsersFail = true
            Toast.makeText(
                this@ProfilActivity,
                this@ProfilActivity.resources.getString(R.string.SQLiteExpection),
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            if (!loadingUsersFail) {
                showListAccounts(tableEmail, tablePass, tableTypeInt, tableImageId, tablePositionInt)
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
                    val intent = Intent(this@ProfilActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    finish()
                    startActivity(intent)
                    Toast.makeText(this@ProfilActivity, this@ProfilActivity.resources.getString(R.string.relogginSuccess), Toast.LENGTH_SHORT).show()


                } else {

                    Toast.makeText(this@ProfilActivity, this@ProfilActivity.resources.getString(R.string.errorInLogginGoogle), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun singIn(userName: String, password: String) {
        firebaseAuth!!.signInWithEmailAndPassword(userName, password).addOnCompleteListener(
            OnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {

                        val intent = Intent(this@ProfilActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        finish()
                        startActivity(intent)
                        Toast.makeText(this@ProfilActivity, this@ProfilActivity.resources.getString(R.string.relogginSuccess), Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(
                        this@ProfilActivity,
                        this@ProfilActivity.resources.getString(R.string.loginFail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }




    private fun alertManagmentAccounts(ThisPosition:Int, ThisEmailTable: Array<String?>, ThisPassTable: Array<String?>,
                                       ThisIdTypeTable: IntArray, ThisImagesTable: Array<String?>, ThisPositionTableInt: IntArray) {
        val alert = androidx.appcompat.app.AlertDialog.Builder(this)

        val emailsAccountString: String = ThisEmailTable[ThisPosition].toString()


        alert.setMessage(this@ProfilActivity.resources.getString(R.string.decide)+"\n\n"+ this@ProfilActivity.resources.getString(R.string.account) +" "+emailsAccountString)
        alert.setTitle(this@ProfilActivity.resources.getString(R.string.accountMamagment))
        alert.setPositiveButton(
            this@ProfilActivity.resources.getString(R.string.logIn)
        ) { dialog: DialogInterface?, whichButton: Int ->
            val typeAccountInt: Int = ThisIdTypeTable[ThisPosition]
            if (emailsAccountString!=email)
            {
                if (typeAccountInt==2)
                {
                    var failSignOut = false
                    try {
                        firebaseAuth!!.signOut()
                    }
                    catch (e: Exception)
                    {
                        failSignOut=true
                    }
                    finally {
                        if (!failSignOut)
                        {
                            val passAcAlredy: String = ThisPassTable[ThisPosition].toString()
                            singIn(emailsAccountString, passAcAlredy)
                        }
                        else
                        {
                            Toast.makeText(
                                this@ProfilActivity,
                                this@ProfilActivity.resources.getString(R.string.signOutFail),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                else
                {
                    Toast.makeText(
                        this@ProfilActivity,
                        this@ProfilActivity.resources.getString(R.string.failLoad),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else
            {
                Toast.makeText(
                    this@ProfilActivity,
                    this@ProfilActivity.resources.getString(R.string.loggedAlreadyThisAccount),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        alert.setNeutralButton(
            this@ProfilActivity.resources.getString(R.string.delete)
        ) { dialog: DialogInterface?, whichButton: Int ->
            var failDelete = false
            try {


                val questionsDatabaseHelper: SQLiteOpenHelper =
                    QuestionsDatabaseHelper(this@ProfilActivity)
                val db = questionsDatabaseHelper.writableDatabase
                db.delete("USERS", "_id = ?", arrayOf(Integer.toString(ThisPositionTableInt[ThisPosition])))
                db.close()
            } catch (e: Exception) {
                failDelete = true
                Toast.makeText(this@ProfilActivity, this@ProfilActivity.resources.getString(R.string.SQLiteExpection), Toast.LENGTH_SHORT).show()
            } finally {
                if (!failDelete) {
                    Toast.makeText(this@ProfilActivity, this@ProfilActivity.resources.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                    loadUsersList()
                }
            }
        }

        alert.setNegativeButton(
            this@ProfilActivity.resources.getString(R.string.cancel)
        ) { dialogInterface: DialogInterface?, i: Int ->

        }
        val dialog = alert.create()
        dialog.setIcon(R.drawable.ic_launcher_foreground)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)
        dialog.show()
    }


    var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            Toast.makeText(this@ProfilActivity, this@ProfilActivity.resources.getString(R.string.loadPicutureComplete), Toast.LENGTH_LONG).show()
            val imageUri = data!!.data

            uploadImageToFirebase(imageUri!!)
        }
        if (result.resultCode == RC_SIGN_IN)
        {
            Toast.makeText(this@ProfilActivity, "TAK !", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            var error = false
            var idtokenString: String = " "
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(
                    TAG,
                    "firebaseAuthWithGoogle:" + account.id
                )

                idtokenString = account.idToken
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this@ProfilActivity, "error 403", Toast.LENGTH_SHORT).show()
                error = true
            }
            finally {
                if(!error)
                {
                    firebaseAuth!!.signOut()
                    firebaseAuthWithGoogle(idtokenString)
                }
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        // uplaod image to firebase storage
        val fileRef: StorageReference =
            storageReference!!.child("users/" + firebaseAuth!!.currentUser!!.uid + "/profile.jpg")
        fileRef.putFile(imageUri).addOnSuccessListener(OnSuccessListener<Any?> {
            fileRef.downloadUrl.addOnSuccessListener(
                OnSuccessListener<Uri?> { uri -> Picasso.get().load(uri).into(profileImageView) })
        }).addOnFailureListener(OnFailureListener {
            Toast.makeText(
                this@ProfilActivity,
                this@ProfilActivity.resources.getString(R.string.fail),
                Toast.LENGTH_SHORT
            ).show()
        })
    }



}
