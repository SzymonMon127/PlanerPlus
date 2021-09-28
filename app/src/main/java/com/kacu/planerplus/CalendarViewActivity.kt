package com.kacu.planerplus

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import java.util.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception


private var calendarView: CalendarView? = null

val mcurrentDate: Calendar = Calendar.getInstance()
val mcurrentDateMin: Calendar = Calendar.getInstance()
private var profileUid:String? = null
private var typeUser: String? = null
private var yearInt: Int? = null
private var mouthInt: Int? = null
private var buttonAdd: Button? = null
private var dateString: String?= null

private var mainActivityRecyclerView: RecyclerView? = null
private var firebaseDatabase: FirebaseDatabase? = null
private var progressBar: ProgressBar? = null
private var userIntance: String? = null

class CalendarViewActivity : AppCompatActivity(), ValueEventListener {

    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)

        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        progressBar!!.max = 1000
        progressBar!!.visibility = View.VISIBLE
        ObjectAnimator.ofInt(progressBar, "progress", 1000).setDuration(2000).start()

        firebaseDatabase = FirebaseDatabase.getInstance()
        mainActivityRecyclerView = findViewById(R.id.rv_calendar)

        buttonAdd = findViewById<View>(R.id.add_to_callendar) as Button
        try {
            profileUid=intent.getStringExtra("uId")
            typeUser=intent.getStringExtra("type")
            yearInt=intent.getIntExtra("year", 0)
            mouthInt=intent.getIntExtra("mouth", 0)
        }
        catch (e: Exception)
        {

        }
        finally {
            calendarView = findViewById<View>(R.id.calendarView) as CalendarView
            mcurrentDate.set(Calendar.YEAR, yearInt!!)
            mcurrentDate.set(Calendar.MONTH, mouthInt!!)
            mcurrentDate.set(Calendar.DAY_OF_MONTH, mcurrentDate.getActualMaximum(Calendar.DATE))

            mcurrentDateMin.set(Calendar.YEAR, yearInt!!)
            mcurrentDateMin.set(Calendar.MONTH, mouthInt!!)
            mcurrentDateMin.set(Calendar.DAY_OF_MONTH, mcurrentDate.getMinimum(Calendar.DATE))

            val endOfMonth = mcurrentDate.timeInMillis
            val startOfMonth = mcurrentDateMin.timeInMillis
            calendarView!!.maxDate = endOfMonth
            calendarView!!.minDate = startOfMonth

            if(typeUser=="Service provider")
            {
                userIntance="Providers"
            }else
            {
                userIntance="Users"
            }
            val calendar = Calendar.getInstance()
            calendar.set(yearInt!!, mouthInt!!, 1)

            calendarView!!.date = calendar.timeInMillis
            dateString="$yearInt-$mouthInt-1"
        }


        calendarView!!.setOnDateChangeListener { view, year, month, dayOfMonth ->
            progressBar!!.visibility = View.VISIBLE
            val emptyTextView = findViewById<TextView>(R.id.emptyTv)
            emptyTextView.visibility = View.GONE
            firebaseDatabase!!.reference.child(userIntance.toString()).child(profileUid.toString()).child("Calendar").child(dateString.toString()
            ).removeEventListener(this)
            dateString ="$year-$month-$dayOfMonth"
            firebaseDatabase!!.reference.child(userIntance.toString()).child(profileUid.toString()).child("Calendar").child(dateString.toString()
            ).addValueEventListener(this)
        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }
    }



    fun onClick(view: View) {
        when (view.id) {
            R.id.add_to_callendar -> {
                if(typeUser=="Service provider")
                {
                    val intent = Intent(this, MyUsersAddTaskActivity::class.java)
                    intent.putExtra("date", dateString)
                    startActivity(intent)
                }
                else
                {
                    usersManagment()
                }

            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun usersManagment() {
        val alert = androidx.appcompat.app.AlertDialog.Builder(this)
        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.alert_dialog_hour, null)
        val etTask: EditText = mDialogView.findViewById(R.id.etTaskHere)

        alert.setTitle(this@CalendarViewActivity.resources.getString(R.string.newTask))


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


        alert.setView(etTask)
        alert.setView(numberPickerHour)
        alert.setView(numberPickerMinutes)

        alert.setPositiveButton(
            this@CalendarViewActivity.resources.getString(R.string.addTask)
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


                            val companyName = "Own task"



                            val calendarUser = CalendarUser(hourTask, profileUid,  taskString, companyName)

                            firebaseDatabase!!.reference.child("Users").child(profileUid.toString()).child("Calendar").
                            child(dateString.toString()).child(hourTask).setValue(calendarUser)


            }
            catch (e: Exception)
            {
                added = false
            }
            finally {
                if (added)
                {
                    Toast.makeText(applicationContext, this@CalendarViewActivity.resources.getString(R.string.addingSuccesful), Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(applicationContext, this@CalendarViewActivity.resources.getString(R.string.fail), Toast.LENGTH_SHORT).show()

                }
            }


        }


        alert.setNegativeButton(
            this@CalendarViewActivity.resources.getString(R.string.cancel)
        ) { dialogInterface: DialogInterface?, i: Int ->

        }
        alert.setIcon(R.drawable.ic_launcher_foreground)
        alert.setView(mDialogView)
        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        firebaseDatabase!!.reference.child(userIntance.toString()).child(profileUid.toString()).child("Calendar").child(dateString.toString()
        ).removeEventListener(this)
    }

    override fun onResume() {
        super.onResume()
        val emptyTextView = findViewById<TextView>(R.id.emptyTv)
        emptyTextView.visibility = View.GONE
        firebaseDatabase!!.reference.child(userIntance.toString()).child(profileUid.toString()).child("Calendar").child(dateString.toString()
        ).addValueEventListener(this)

    }

    private fun usersManagment(ThisHour:String?, ThisName:String?, ThisId:String?) {
        val alert = androidx.appcompat.app.AlertDialog.Builder(this)




        alert.setMessage(this@CalendarViewActivity.resources.getString(R.string.decide)+"\n\n"+ this@CalendarViewActivity.resources.getString(R.string.user) +" "+ThisName
        + "\n\n" +this@CalendarViewActivity.resources.getString(R.string.hour) +": " + ThisHour)
        alert.setTitle(this@CalendarViewActivity.resources.getString(R.string.taskManagment))



        alert.setNeutralButton(
            this@CalendarViewActivity.resources.getString(R.string.deleTask)
        ) { dialog: DialogInterface?, whichButton: Int ->
            if(typeUser=="Service provider")
            {
                firebaseDatabase!!.reference.child("Providers").child(profileUid.toString()).child("Calendar").child(dateString.toString()).child(ThisHour.toString())
                    .removeValue()
                firebaseDatabase!!.reference.child("Users").child(ThisId.toString()).child("Calendar").child(dateString.toString()).child(ThisHour.toString())
                    .removeValue()
            }
            else
            {
                firebaseDatabase!!.reference.child(userIntance.toString()).child(profileUid.toString()).child("Calendar").child(dateString.toString()).child(ThisHour.toString())
                    .removeValue()
                Toast.makeText(applicationContext, this@CalendarViewActivity.resources.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
            }
        }

        alert.setNegativeButton(
            this@CalendarViewActivity.resources.getString(R.string.cancel)
        ) { dialogInterface: DialogInterface?, i: Int ->

        }
        val dialog = alert.create()
        dialog.setIcon(R.drawable.ic_launcher_foreground)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button4_bg)
        dialog.show()
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val size = snapshot.childrenCount.toInt()

        val nameTable = arrayOfNulls<String>(size)
        val hourTAble = arrayOfNulls<String>(size)
        val taskTable = arrayOfNulls<String>(size)
        val idTable = arrayOfNulls<String>(size)


        for ((i, postSnapshot) in snapshot.children.withIndex()) {
            val newTask: CalendarUser = postSnapshot.getValue(CalendarUser::class.java)!!
            hourTAble[i] = newTask.hour
        }

        if (typeUser=="Service provider")
        {
            for ((j, postSnapshot) in snapshot.children.withIndex()) {
                val newTask: CalendarUser = postSnapshot.getValue(CalendarUser::class.java)!!
                nameTable[j] = newTask.userName
            }
        }
        else
        {
            for ((j, postSnapshot) in snapshot.children.withIndex()) {
                val newTask: CalendarUser = postSnapshot.getValue(CalendarUser::class.java)!!
                nameTable[j] = newTask.companyName
            }
        }

        for ((j, postSnapshot) in snapshot.children.withIndex()) {
            val newTask: CalendarUser = postSnapshot.getValue(CalendarUser::class.java)!!
            taskTable[j] = newTask.task
        }

        for ((j, postSnapshot) in snapshot.children.withIndex()) {
            val newTask: CalendarUser = postSnapshot.getValue(CalendarUser::class.java)!!
            idTable[j] = newTask.userId
        }

        progressBar!!.visibility = View.GONE
        val adapter = CardCaptionedAccountsAdapterWhiteTasks(hourTAble, nameTable, taskTable)
        mainActivityRecyclerView!!.adapter = adapter

        val layoutManager = LinearLayoutManager(applicationContext)
        mainActivityRecyclerView!!.layoutManager = layoutManager

        mainActivityRecyclerView!!.layoutManager = layoutManager
        adapter.setListener { position ->
            val hour =hourTAble[position]
            val Name = nameTable[position]
            val CurrentID = idTable[position]
            usersManagment(hour, Name, CurrentID)
        }

        if (snapshot.childrenCount.toInt() == 0) {
            progressBar!!.visibility = View.GONE
            val emptyTextView = findViewById<TextView>(R.id.emptyTv)
            emptyTextView.visibility = View.VISIBLE
        }
    }

    override fun onCancelled(error: DatabaseError) {
    }


}