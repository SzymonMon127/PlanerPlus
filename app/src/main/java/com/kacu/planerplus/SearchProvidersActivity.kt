package com.kacu.planerplus

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
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
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.*
import java.lang.Exception
import java.util.ArrayList





private var mainActivityRecyclerView: RecyclerView? = null
private var progressBar: ProgressBar? = null
private  var firebaseDatabase: FirebaseDatabase? = null
private var usersProfileItem: List<UsersProfileItemWithCity>? = null
private var searchView: SearchView? = null
private var queryString: String? = null
private var  searchInputToLower: String? = null
private var searchInputTOUpper: String? = null
private var dbPersons: DatabaseReference? = null


class SearchProvidersActivity : AppCompatActivity(), ValueEventListener {
    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_providers)
        queryString = intent.getStringExtra("query")
        searchInputToLower = intent.getStringExtra("query")!!.lowercase()




        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        progressBar!!.max = 1000
        progressBar!!.visibility = View.VISIBLE
        ObjectAnimator.ofInt(progressBar, "progress", 1000).setDuration(2000).start()



        mainActivityRecyclerView = findViewById(R.id.rv_requests_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        searchView = findViewById<View>(R.id.searchView) as SearchView

    }


    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbPersons =firebaseDatabase!!.getReference("Providers")

        Toast.makeText(applicationContext, "$searchInputToLower/$searchInputTOUpper", Toast.LENGTH_SHORT).show()
        val query: Query = FirebaseDatabase.getInstance().getReference("Providers").orderByChild("name").startAt(
            queryString).endAt(queryString + "\uf8ff")
        query.addValueEventListener(this)
    }

    private fun usersManagment(ThisUid:String?, ThisName:String?) {
        val alert = androidx.appcompat.app.AlertDialog.Builder(this)




        alert.setMessage(this@SearchProvidersActivity.resources.getString(R.string.decide)+"\n\n"+ this@SearchProvidersActivity.resources.getString(R.string.user) +" "+ThisName)
        alert.setTitle(this@SearchProvidersActivity.resources.getString(R.string.userManagment))

        alert.setPositiveButton(
            this@SearchProvidersActivity.resources.getString(R.string.showProfile)
        ) { dialog: DialogInterface?, whichButton: Int ->

            val intent = Intent(this@SearchProvidersActivity, ProfilUserActivity::class.java)
            intent.putExtra("uId",ThisUid)
            intent.putExtra("type","provider")
            intent.putExtra("addingType","true")
            startActivity(intent)
        }



        alert.setNegativeButton(
            this@SearchProvidersActivity.resources.getString(R.string.cancel)
        ) { dialogInterface: DialogInterface?, i: Int ->

        }
        val dialog = alert.create()
        dialog.setIcon(R.drawable.ic_launcher_foreground)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)
        dialog.show()
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        try {

                        if (snapshot.childrenCount.toInt() != 0) {

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
                                (usersProfileItem as ArrayList<UsersProfileItemWithCity>).add(UsersProfileItemWithCity(newPerson.Id, newPerson.name, "Kato"))

                            }

                            progressBar!!.visibility = View.GONE
                            val adapter = CardCaptionedAccountsAdapterWhiteSeaarchCity(usersProfileItem)
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

                            mainActivityRecyclerView!!.layoutManager = layoutManager
                            adapter.setListener { position ->
                                val Uid=idTable[position]
                                val Name = nameTable[position]
                                usersManagment(Uid, Name)
                            }

                        } else {
                            progressBar!!.visibility = View.GONE
                            val emptyTextView = findViewById<TextView>(R.id.emptyTv)
                            emptyTextView.visibility = View.VISIBLE
                        }
                    }

         catch (e: Exception) {
            Toast.makeText(
                this@SearchProvidersActivity,
                this@SearchProvidersActivity.resources.getString(R.string.firebaseInitFail),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCancelled(error: DatabaseError) {

    }


}