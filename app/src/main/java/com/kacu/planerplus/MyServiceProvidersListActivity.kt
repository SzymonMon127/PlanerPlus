package com.kacu.planerplus

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*

class MyServiceProvidersListActivity : AppCompatActivity(), ValueEventListener {

    private var mainActivityRecyclerView: RecyclerView? = null
    private var id: String? = null
    private var progressBar: ProgressBar? = null
    private var usersProfileItem: List<UsersProfileItem>? = null
    private var searchView: SearchView? = null
    var firebaseDatabase: FirebaseDatabase? = null
    private var firebaseAuth: FirebaseAuth? = null

    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_service_providers_list)

        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        progressBar!!.max = 1000
        progressBar!!.visibility = View.VISIBLE
        ObjectAnimator.ofInt(progressBar, "progress", 1000).setDuration(2000).start()

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        mainActivityRecyclerView = findViewById(R.id.rv_Providers_list)

        if (firebaseAuth!!.currentUser != null) {
            getId()



        } else {
            LogOut()
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        searchView = findViewById<View>(R.id.searchView) as SearchView
    }



    private fun getId() {
        val idNumber: String = FirebaseAuth.getInstance().currentUser!!.uid
        id = idNumber
    }

    private fun LogOut() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
    }

    override fun onPause() {
        super.onPause()
        firebaseDatabase!!.reference.child("Users").child(id!!).child("ProvidersList").orderByChild("name")
            .removeEventListener(this)
    }

    override fun onResume() {
        super.onResume()
        firebaseDatabase!!.reference.child("Users").child(id!!).child("ProvidersList").orderByChild("name")
            .addValueEventListener(this)
    }

    private fun usersManagment(ThisUid:String?, ThisName:String?) {
        val alert = androidx.appcompat.app.AlertDialog.Builder(this)




        alert.setMessage(this@MyServiceProvidersListActivity.resources.getString(R.string.decide)+"\n\n"+ this@MyServiceProvidersListActivity.resources.getString(R.string.user) +" "+ThisName)
        alert.setTitle(this@MyServiceProvidersListActivity.resources.getString(R.string.userManagment))

        alert.setPositiveButton(
            this@MyServiceProvidersListActivity.resources.getString(R.string.showProfile)
        ) { dialog: DialogInterface?, whichButton: Int ->

            val intent = Intent(this@MyServiceProvidersListActivity, ProfilUserActivity::class.java)
            intent.putExtra("uId",ThisUid)
            intent.putExtra("type","provider")
            intent.putExtra("addingType","no")
            startActivity(intent)
        }

        alert.setNeutralButton(
            this@MyServiceProvidersListActivity.resources.getString(R.string.deleteProfile)
        ) { dialog: DialogInterface?, whichButton: Int ->
            firebaseDatabase!!.reference.child("Users").child(id!!).child("ProvidersList").child(ThisUid!!).removeValue()
            Toast.makeText(applicationContext, this@MyServiceProvidersListActivity.resources.getString(R.string.deleted), Toast.LENGTH_SHORT).show()

        }

        alert.setNegativeButton(
            this@MyServiceProvidersListActivity.resources.getString(R.string.cancel)
        ) { dialogInterface: DialogInterface?, i: Int ->

        }
        val dialog = alert.create()
        dialog.setIcon(R.drawable.ic_launcher_foreground)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)
        dialog.show()
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        try {



                            val size = snapshot.childrenCount.toInt()

                            val idTable = arrayOfNulls<String>(size)
                            val nameTable = arrayOfNulls<String>(size)


                            for ((i, postSnapshot) in snapshot.children.withIndex()) {
                                val newPerson: Person = postSnapshot.getValue(Person::class.java)!!
                                idTable[i] = newPerson.Id
                            }

                            for ((j, postSnapshot) in snapshot.children.withIndex()) {
                                val newPerson: Person = postSnapshot.getValue(Person::class.java)!!
                                nameTable[j] = newPerson.name
                            }

                            usersProfileItem = ArrayList()
                            for ((j, postSnapshot) in snapshot.children.withIndex()) {
                                val newPerson: Person = postSnapshot.getValue(Person::class.java)!!
                                (usersProfileItem as ArrayList<UsersProfileItem>).add(UsersProfileItem(newPerson.Id, newPerson.name))

                            }

                            progressBar!!.visibility = View.GONE
                            val adapter = CardCaptionedAccountsAdapterWhiteSeaarch(usersProfileItem)
                            mainActivityRecyclerView!!.adapter = adapter

                            val layoutManager = LinearLayoutManager(applicationContext)
                            mainActivityRecyclerView!!.layoutManager = layoutManager

                            searchView!!.setImeOptions(EditorInfo.IME_ACTION_DONE)

                            searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                                override fun onQueryTextSubmit(query: String): Boolean {
                                    return false
                                }

                                override fun onQueryTextChange(newText: String): Boolean {
                                    adapter.getFilter().filter(newText)
                                    return false
                                }
                            })


                            adapter.setListener { position ->
                                val Uid=idTable[position]
                                val Name = nameTable[position]
                                usersManagment(Uid, Name)
                            }

                            if (snapshot.childrenCount.toInt() == 0) {
                                progressBar!!.visibility = View.GONE
                                val emptyTextView = findViewById<TextView>(R.id.emptyTv)
                                emptyTextView.visibility = View.VISIBLE
                            }





        } catch (e: Exception) {
            Toast.makeText(
                this@MyServiceProvidersListActivity,
                this@MyServiceProvidersListActivity.resources.getString(R.string.firebaseInitFail),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCancelled(error: DatabaseError) {

    }
}