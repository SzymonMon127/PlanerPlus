package com.kacu.planerplus

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.NumberPicker.OnValueChangeListener
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.ArrayList

private var mainActivityRecyclerView: RecyclerView? = null
private var id: String? = null
private var progressBar: ProgressBar? = null

private var firebaseDatabase: FirebaseDatabase? = null
private var firebaseAuth: FirebaseAuth? = null

private var usersProfileItem: List<UsersProfileItem>? = null
private var searchView: SearchView? = null
private var dateUser: String? = null
private var companyName: String? = null
class MyUsersAddTaskActivity : AppCompatActivity(), ValueEventListener {
    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_users_add_task)

        dateUser=intent.getStringExtra("date")


        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        progressBar!!.max = 1000
        progressBar!!.visibility = View.VISIBLE
        ObjectAnimator.ofInt(progressBar, "progress", 1000).setDuration(2000).start()

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        mainActivityRecyclerView = findViewById(R.id.rv_users_task)

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
        firebaseDatabase!!.reference.child("Providers").child(id!!).child("UsersList").orderByChild("name")
            .removeEventListener(this)
    }

    override fun onResume() {
        super.onResume()
        firebaseDatabase!!.reference.child("Providers").child(id!!).child("UsersList").orderByChild("name")
            .addValueEventListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun usersManagment(ThisUid:String?, ThisName:String?) {
        val alert = androidx.appcompat.app.AlertDialog.Builder(this)
        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.alert_dialog_hour, null)
        val tvTitle: TextView = mDialogView.findViewById(R.id.etTitle)
        val etTask: EditText = mDialogView.findViewById(R.id.etTaskHere)

        tvTitle.text = this@MyUsersAddTaskActivity.resources.getString(R.string.decide)+"\n\n"+ this@MyUsersAddTaskActivity.resources.getString(R.string.user) +" "+ThisName
        alert.setTitle(this@MyUsersAddTaskActivity.resources.getString(R.string.userManagment))


        val numberPickerHour: NumberPicker = mDialogView.findViewById<NumberPicker>(R.id.numberPickerHour)
        val numberPickerMinutes: NumberPicker =
            mDialogView.findViewById<NumberPicker>(R.id.numberPickerMinutes)


        numberPickerHour.maxValue = 23
        numberPickerHour.minValue = 0
        numberPickerHour.value = 0
        numberPickerMinutes.minValue = 0
        numberPickerMinutes.maxValue = 59

        var hourString: String? = null
        var minuteString: String? = null

        numberPickerHour.setOnValueChangedListener { numberPicker: NumberPicker?, i: Int, i1: Int ->
            hourString = i1.toString()
        }

        numberPickerMinutes.setOnValueChangedListener { numberPicker: NumberPicker?, i: Int, i1: Int ->
            minuteString = i1.toString()
        }

        alert.setView(tvTitle)
        alert.setView(etTask)
        alert.setView(numberPickerHour)
        alert.setView(numberPickerMinutes)

        alert.setPositiveButton(
            this@MyUsersAddTaskActivity.resources.getString(R.string.addTask)
        ) { dialog: DialogInterface?, whichButton: Int ->
            var added = true
            try {
                if(minuteString==null || minuteString!!.isEmpty())
                {
                    minuteString="00"
                }
                else if (minuteString!!.toInt() <10)
                {
                    minuteString = "0$minuteString"
                    if (minuteString!!.toInt() == 0) {
                        minuteString ="00"
                    }
                }
                if(hourString==null || hourString!!.isEmpty())
                {
                    hourString="00"
                }
                else if (hourString!!.toInt() <10)
                {
                    hourString = "0$hourString"
                    if (hourString!!.toInt() == 0) {
                        hourString ="00"
                    }
                }
                val hourTask: String = "$hourString:$minuteString"
                val taskString: String = etTask.text.toString()

                firebaseDatabase!!.reference.child("Providers").child(id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.childrenCount.toInt() != 0) {
                            val newPerson = dataSnapshot.getValue(Person::class.java)
                            companyName = newPerson!!.name



                            val calendarUser = CalendarUser(hourTask, ThisUid, ThisName, taskString, companyName)
                            firebaseDatabase!!.reference.child("Providers").child(id.toString()).child("Calendar").child(
                                dateUser.toString()).child(hourTask).setValue(calendarUser)
                            firebaseDatabase!!.reference.child("Users").child(ThisUid.toString()).child("Calendar").
                            child(dateUser.toString()).child(hourTask).setValue(calendarUser)

                        } else {
                            Toast.makeText(
                                this@MyUsersAddTaskActivity,
                                this@MyUsersAddTaskActivity.resources.getString(R.string.firebaseInitFail),
                                Toast.LENGTH_SHORT
                            ).show()
                            added = false

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })


            }
            catch (e: Exception)
            {
                added = false
            }
            finally {
                if (added)
                {
                    Toast.makeText(applicationContext, this@MyUsersAddTaskActivity.resources.getString(R.string.addingSuccesful), Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(applicationContext, this@MyUsersAddTaskActivity.resources.getString(R.string.fail), Toast.LENGTH_SHORT).show()

                }
            }


        }


        alert.setNegativeButton(
            this@MyUsersAddTaskActivity.resources.getString(R.string.cancel)
        ) { dialogInterface: DialogInterface?, i: Int ->

        }
        alert.setIcon(R.drawable.ic_launcher_foreground)
        alert.setView(mDialogView)
        val dialog = alert.create()
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
            0
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

            searchView!!.imeOptions = EditorInfo.IME_ACTION_DONE

            searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    adapter.filter.filter(newText)
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
                this@MyUsersAddTaskActivity,
                this@MyUsersAddTaskActivity.resources.getString(R.string.firebaseInitFail),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCancelled(error: DatabaseError) {

    }

}