package com.kacu.planerplus


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle

import android.os.Build
import android.widget.Toast
import android.content.Intent
import android.graphics.Color
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.lang.Exception


import java.util.*





class MainActivity : AppCompatActivity() {
    private var idCurrent: String? = null

    private var firebaseDatabase: FirebaseDatabase? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var userType: String? = null
    private var searchView: SearchView? = null
    private var buttonRequests: Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        searchView = findViewById<SearchView>(R.id.serch_View) as SearchView


        searchView!!.setImeOptions(EditorInfo.IME_ACTION_DONE)


        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val intent = Intent(this@MainActivity, SearchProvidersActivity::class.java)
                intent.putExtra("query", query)
                startActivity(intent)


                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }
        })
        buttonRequests = findViewById<Button>(R.id.button_request_from_users) as Button

        if (firebaseAuth!!.currentUser != null) {
            get()
            loadInfo()
        } else {
            LogOut()
        }
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setSubtitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val viewPager = findViewById<ViewPager2>(R.id.pager)

        viewPager.adapter = FragmentAdapter(this)
        TabLayoutMediator(tabLayout, viewPager)
        { tab, position->
            when(position)
            {
                0->tab.text = this@MainActivity.resources.getString(R.string.year1)
                1->tab.text = this@MainActivity.resources.getString(R.string.year2)
            }
        }.attach()



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }





    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.m_lo) {
            val toast = Toast.makeText(
                this@MainActivity,
                this.resources.getString(R.string.loggedOut),
                Toast.LENGTH_SHORT
            )
            toast.show()
            firebaseAuth!!.signOut()
            LogOut()
            return true
        } else if (id == R.id.m_prof) {
            val intent = Intent(this@MainActivity, ProfilActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class FragmentAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity)
    {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when(position)
            {
                0->YearOneFragment()
                1->  YearTwoFragment()
                else-> YearOneFragment()
            }
        }

    }

    private fun  get() {
        val idNumber: String = FirebaseAuth.getInstance().currentUser!!.uid
        idCurrent = idNumber
    }

    private fun LogOut() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
        startActivity(intent)
    }

    fun onClick(view: View?) {
        when (view!!.id) {
            R.id.button_request_from_users -> {
                val intent = Intent(this, RequestsListActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                startActivity(intent)
            }

            R.id.calendarFirst1 -> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",0)
                startActivity(intent)
            }
            R.id.calendarFirst2-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",1)
                startActivity(intent)
            }
            R.id.calendarFirst3-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",2)
                startActivity(intent)
            }
            R.id.calendarFirst4-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",3)
                startActivity(intent)
            }
            R.id.calendarFirst5-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",4)
                startActivity(intent)
            }
            R.id.calendarFirst6-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",5)
                startActivity(intent)
            }
            R.id.calendarFirst7-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",6)
                startActivity(intent)
            }
            R.id.calendarFirst8-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",7)
                startActivity(intent)
            }
            R.id.calendarFirst9-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",8)
                startActivity(intent)
            }
            R.id.calendarFirst10-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",9)
                startActivity(intent)
            }

            R.id.calendarFirst11-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",10)
                startActivity(intent)
            }
            R.id.calendarFirst12-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2021)
                intent.putExtra("mouth",11)
                startActivity(intent)
            }

            R.id.calendarTwo1-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",0)
                startActivity(intent)
            }
            R.id.calendarTwo2-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",1)
                startActivity(intent)
            }
            R.id.calendarTwo3-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",2)
                startActivity(intent)
            }
            R.id.calendarTwo4-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",3)
                startActivity(intent)
            }
            R.id.calendarTwo5-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",4)
                startActivity(intent)
            }
            R.id.calendarTwo6-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",5)
                startActivity(intent)
            }
            R.id.calendarTwo7-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",6)
                startActivity(intent)
            }
            R.id.calendarTwo8-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",7)
                startActivity(intent)
            }
            R.id.calendarTwo9-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",8)
                startActivity(intent)
            }
            R.id.calendarTwo10-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",9)
                startActivity(intent)
            }
            R.id.calendarTwo11-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",10)
                startActivity(intent)
            }
            R.id.calendarTwo12-> {
                val intent = Intent(this, CalendarViewActivity::class.java)
                intent.putExtra("uId",idCurrent)
                intent.putExtra("type",userType)
                intent.putExtra("year",2022)
                intent.putExtra("mouth",11)
                startActivity(intent)
            }
        }
    }

    private fun loadInfo() {
        try {
            firebaseDatabase!!.reference.child("Providers").child(idCurrent!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.childrenCount.toInt() != 0) {
                            val newPerson = dataSnapshot.getValue(
                                Person::class.java
                            )
                            userType = newPerson!!.userType
                            if (userType == "Service provider") {
                                buttonRequests!!.visibility = View.VISIBLE
                            }
                        }
                        else
                        {
                            searchView!!.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        } catch (e: Exception) {
            Toast.makeText(
                this@MainActivity,
                this.resources.getString(R.string.firebaseInitFail),
                Toast.LENGTH_LONG
            ).show()

        }
    }

    }



