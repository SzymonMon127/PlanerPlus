package com.kacu.planerplus

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.apache.commons.lang3.StringUtils
import java.lang.Exception
import com.google.type.LatLng
import java.io.IOException
import java.util.ArrayList


private var tvUserName: TextView? = null
private var tvUserEmail: TextView? = null
private var tvUserType: TextView? = null
private var tvUserPhone: TextView? = null
private var tvUserCattegory: TextView? = null
private var tvUserAddress: TextView? = null
private var progressBar: ProgressBar? = null
private var addRequestButton: Button? = null
var storageReference: StorageReference? = null
private var profileImageView: CircleImageView? = null
private var profileUId: String? = null
private var type: String? = null
private var addingType: String? = null
private var firebaseDatabase: FirebaseDatabase? = null
private var firebaseAuth: FirebaseAuth? = null
private var currentId: String? = null
private var viewOnClick: View? = null
private var buttonAddToGroup: Button? = null
private var LnMail: LinearLayout? = null
private var LnPhone: LinearLayout? = null
private var LnCattegory: LinearLayout? = null
private var LnAdress: LinearLayout? = null



class ProfilUserActivity : AppCompatActivity() {
    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_user)

        firebaseDatabase = FirebaseDatabase.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth!!.currentUser != null)
        {
            currentId = firebaseAuth!!.uid
        }
        else
        {
            LogOut()
        }

        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        progressBar!!.max = 1000
        progressBar!!.visibility = View.VISIBLE
        ObjectAnimator.ofInt(progressBar, "progress", 1000).setDuration(2000).start()

        tvUserName = findViewById<View>(R.id.userName) as TextView
        tvUserEmail = findViewById<View>(R.id.userEmail) as TextView
        tvUserType = findViewById<View>(R.id.userType) as TextView
        tvUserPhone = findViewById<View>(R.id.userPhone) as TextView
        tvUserCattegory = findViewById<View>(R.id.userCattegory) as TextView
        tvUserAddress = findViewById<View>(R.id.userAddress) as TextView
        profileImageView = findViewById<View>(R.id.circleImageViewProfile) as CircleImageView
        addRequestButton = findViewById<View>(R.id.button_request) as Button
        buttonAddToGroup = findViewById<View>(R.id.button_add_to_group) as Button
        LnMail = findViewById<View>(R.id.LnUserEmail) as LinearLayout
        LnPhone = findViewById<View>(R.id.LnUerPhone) as LinearLayout
        LnCattegory = findViewById<View>(R.id.LnUerCattegory) as LinearLayout
        LnAdress = findViewById<View>(R.id.LnUserAdress) as LinearLayout




        profileUId=intent.getStringExtra("uId")
         type=intent.getStringExtra("type")
         addingType=intent.getStringExtra("addingType")

        val profileRef = storageReference!!.child("users/" + profileUId + "/profile.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(profileImageView)
        }




        if (addingType=="true")
        {
            if (type=="provider")
            {
                addRequestButton!!.visibility = View.VISIBLE
            }
            else if (type=="user")
            {
                buttonAddToGroup!!.visibility = View.VISIBLE
            }
        }

        if (type=="user")
        {
            loadProfileUser()
        }
        else if (type=="provider")
        {
            loadProfileProvider()
            LnCattegory!!.visibility = View.VISIBLE
            LnAdress!!.visibility = View.VISIBLE
        }
        else{

        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }
    }

    private fun loadProfileProvider() {
        try {
           firebaseDatabase!!.reference.child("Providers").child(profileUId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.childrenCount.toInt() != 0) {
                            val newPerson = dataSnapshot.getValue(Person::class.java)


                            tvUserName!!.text = newPerson!!.name
                            tvUserEmail!!.text = newPerson.email
                            tvUserType!!.text = newPerson.userType
                            tvUserPhone!!.text =newPerson.companyPhone
                            tvUserCattegory!!.text =newPerson.companyCattegory
                            tvUserAddress!!.text =newPerson.companyAdress



                            progressBar!!.visibility = View.GONE
                        }
                        else
                        {
                            Toast.makeText(
                                this@ProfilUserActivity,
                                this@ProfilUserActivity.resources.getString(R.string.failLoad),
                                Toast.LENGTH_LONG
                            ).show()
                            progressBar!!.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        } catch (e: Exception) {
            Toast.makeText(
                this@ProfilUserActivity,
                this@ProfilUserActivity.resources.getString(R.string.firebaseInitFail),
                Toast.LENGTH_LONG
            ).show()

        }
    }

    private fun loadProfileUser() {
        try {
           firebaseDatabase!!.reference.child("Users").child(profileUId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.childrenCount.toInt() != 0) {
                            val newPerson = dataSnapshot.getValue(Person::class.java)
                            tvUserName!!.text =newPerson!!.name
                            tvUserName!!.text =newPerson!!.name
                            tvUserEmail!!.text = newPerson.email
                            tvUserType!!.text = newPerson.userType
                            tvUserPhone!!.text = newPerson.companyPhone

                            progressBar!!.visibility = View.GONE
                        }
                        else
                        {
                            Toast.makeText(
                                this@ProfilUserActivity,
                                this@ProfilUserActivity.resources.getString(R.string.failLoad),
                                Toast.LENGTH_LONG
                            ).show()
                            progressBar!!.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        } catch (e: Exception) {
            Toast.makeText(
                this@ProfilUserActivity,
                this@ProfilUserActivity.resources.getString(R.string.firebaseInitFail),
                Toast.LENGTH_LONG
            ).show()

        }
    }
    private fun LogOut() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
        startActivity(intent)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.LnUserEmail -> {


                val email = Intent(Intent.ACTION_SEND)
                email.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(tvUserEmail!!.text.toString()))
                email.putExtra(Intent.EXTRA_SUBJECT, this@ProfilUserActivity.resources.getString(R.string.app_name))
                email.type = "message/rfc822"

                startActivity(Intent.createChooser(
                    email,
                    this@ProfilUserActivity.resources.getString(R.string.emailApplication)
                ))
            }
            R.id.LnUerPhone -> {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                val parseString = "tel:" + tvUserPhone!!.text.toString()
                intent.data = Uri.parse(parseString)
                startActivity(intent)

            }
            R.id.LnUserAdress -> {
                val streetString = this@ProfilUserActivity.resources.getString(R.string.street)
                val stringBeforeChanges: String = tvUserAddress!!.text as String
                val stringAfterFirstChange: String = StringUtils.substringAfter(stringBeforeChanges, "  -  ")
                val city: String = StringUtils.substringBefore(stringAfterFirstChange, ",   ")
                val stringBeforeLastChange = StringUtils.substringAfter(stringBeforeChanges,",   \n $streetString ")
                val stringStreetToGps = StringUtils.substringBeforeLast(stringBeforeLastChange, "/")
                var latitude  = "0.0"
                var longitude = "0.0"
                if (Geocoder.isPresent()) {


                    try {
                        val gc = Geocoder(this)
                        val addresses = gc.getFromLocationName(city, 1) // get the found Address Objects

                        for (a in addresses) {
                            if (a.hasLatitude() && a.hasLongitude()) {
                            latitude = a.latitude.toString()
                                longitude = a.longitude.toString()
                            }
                        }
                    }  catch (e: IOException) {

                    }
                    finally {

                    }
                }
                val gmmIntentUri = Uri.parse("geo:"+ latitude+"," + longitude +"?q=" + Uri.encode("$city $stringStreetToGps"))
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                mapIntent.resolveActivity(packageManager)?.let {
                    startActivity(mapIntent)
                }
            }

            R.id.button_request ->
            {

                viewOnClick = view
                try {
                    firebaseDatabase = FirebaseDatabase.getInstance()

                    firebaseDatabase!!.reference.child("Users").child(currentId.toString()).child("ProvidersList").child(profileUId.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.childrenCount.toInt() != 0) {
                                    val snackbar_fail = Snackbar
                                        .make(viewOnClick!!,
                                            this@ProfilUserActivity.resources.getString(R.string.userAlredyinGroup),
                                            Snackbar.LENGTH_LONG)
                                    snackbar_fail.show()

                                } else {
                                    try {
                                        firebaseDatabase = FirebaseDatabase.getInstance()

                                        firebaseDatabase!!.reference.child("Providers").child(profileUId.toString()).child("Requests").child(
                                            currentId.toString())
                                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    if (dataSnapshot.childrenCount.toInt() != 0) {

                                                        val snackbar_fail = Snackbar
                                                            .make(viewOnClick!!,
                                                                this@ProfilUserActivity.resources.getString(R.string.requestAlredySend),
                                                                Snackbar.LENGTH_LONG)
                                                        snackbar_fail.show()


                                                    } else {
                                                        var fail = false
                                                        try {
                                                            firebaseDatabase = FirebaseDatabase.getInstance()

                                                            firebaseDatabase!!.reference.child("Users").child(currentId.toString())
                                                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                                        if (dataSnapshot.childrenCount.toInt() != 0) {
                                                                            val newPerson = dataSnapshot.getValue(Person::class.java)

                                                                            var currentPersonName = newPerson!!.name

                                                                            val person = Person(currentId, currentPersonName)
                                                                            firebaseDatabase!!.reference.child("Providers").child(profileUId.toString()).child("Requests").child(
                                                                                currentId.toString()).setValue(person)

                                                                        }
                                                                    }

                                                                    override fun onCancelled(databaseError: DatabaseError) {}
                                                                })
                                                        } catch (e: Exception) {
                                                            fail = true
                                                            val snackbar_fail = Snackbar
                                                                .make(viewOnClick!!,
                                                                    this@ProfilUserActivity.resources.getString(R.string.firebaseInitFail),
                                                                    Snackbar.LENGTH_LONG)
                                                            snackbar_fail.show()
                                                        }
                                                        finally {
                                                            if (!fail)
                                                            {
                                                                val snackbar_fail = Snackbar
                                                                    .make(viewOnClick!!,
                                                                        this@ProfilUserActivity.resources.getString(R.string.requestSendSuccess),
                                                                        Snackbar.LENGTH_LONG)
                                                                snackbar_fail.show()
                                                            }
                                                            else
                                                            {
                                                                val snackbar_fail = Snackbar
                                                                    .make(viewOnClick!!,
                                                                        this@ProfilUserActivity.resources.getString(R.string.noUsersToLoad),
                                                                        Snackbar.LENGTH_LONG)
                                                                snackbar_fail.show()
                                                            }
                                                        }

                                                    }
                                                }

                                                override fun onCancelled(databaseError: DatabaseError) {}
                                            })
                                    } catch (e: Exception) {
                                        val snackbar_fail = Snackbar
                                            .make(viewOnClick!!,
                                                this@ProfilUserActivity.resources.getString(R.string.firebaseInitFail),
                                                Snackbar.LENGTH_LONG)
                                        snackbar_fail.show()
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                } catch (e: Exception) {
                    val snackbar_fail = Snackbar
                        .make(viewOnClick!!,
                            this@ProfilUserActivity.resources.getString(R.string.firebaseInitFail),
                            Snackbar.LENGTH_LONG)
                    snackbar_fail.show()
                }
            }

            R.id.button_add_to_group -> {
                var badAdding = false
                viewOnClick = view

                try {
                    try {
                        firebaseDatabase = FirebaseDatabase.getInstance()

                        firebaseDatabase!!.reference.child("Providers").child(currentId.toString())
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.childrenCount.toInt() != 0) {
                                        val newPerson = dataSnapshot.getValue(Person::class.java)

                                        val currentProviderName = newPerson!!.name




                                        val person = Person(currentId, currentProviderName)
                                        firebaseDatabase!!.reference.child("Users").child(profileUId.toString()).child("ProvidersList").child(currentId.toString()).setValue(person)

                                        val userName = tvUserName!!.text
                                        val person1 = Person(profileUId, userName.toString())
                                        firebaseDatabase!!.reference.child("Providers").child(currentId.toString()).child("UsersList").child(profileUId.toString()).setValue(person1)

                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                    } catch (e: Exception) {
                        badAdding = true
                    }
                }
                catch (e: Exception)
                {
                    badAdding = true
                }
                finally {
                    if (!badAdding)
                    {
                        firebaseDatabase!!.reference.child("Providers").child(currentId.toString()).child("Requests").child(
                            profileUId.toString()).removeValue()
                        onBackPressed()
                        val snackbar_fail = Snackbar
                            .make(viewOnClick!!,
                                this@ProfilUserActivity.resources.getString(R.string.addingSuccesful),
                                Snackbar.LENGTH_LONG)
                        snackbar_fail.show()
                    }
                    else
                    {
                        val snackbar_fail = Snackbar
                            .make(viewOnClick!!,
                                this@ProfilUserActivity.resources.getString(R.string.firebaseInitFail),
                                Snackbar.LENGTH_LONG)
                        snackbar_fail.show()
                    }

                }

            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}