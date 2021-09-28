package com.kacu.planerplus


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.widget.*

import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.FirebaseDatabase

import java.lang.Exception
import java.util.*





class SignupActivity : AppCompatActivity() {

    private var acceptStatue = false
    private var fail = false
    private var failString: String? = null
    private var checkBox: CheckBox? = null
    private var spinner: Spinner? = null
    private var cattegory: String? = null
    private var companyName: String? = null
    private var companyAdress: String? = null
    private var companyPhoneNumber: String? = null
    private var password: String? = null
    private var repeatPassword: String? = null
    private var email: String? = null

    private var etCompanyName: EditText? = null
    private var etCompanyPhoneNumber: EditText? = null
    private var etCompanyPassword: EditText? = null
    private var etCompanyRepeatPassword: EditText? = null
    private var etCompanyEmail: EditText? = null
    private var etCompanyAdress: EditText? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var id: String? = null

    private var btCattegory: Button? = null
    private var btSettAdress: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }
        firebaseAuth = FirebaseAuth.getInstance()


        btCattegory = findViewById<View>(R.id.buttonCattegory) as Button
        btSettAdress = findViewById<View>(R.id.button_set_adress) as Button

        etCompanyName = findViewById<View>(R.id.et_company_name) as EditText
        etCompanyPhoneNumber = findViewById<View>(R.id.et_phone) as EditText
        etCompanyPassword = findViewById<View>(R.id.et_password) as EditText
        etCompanyRepeatPassword = findViewById<View>(R.id.et_confirm_password) as EditText
        etCompanyEmail = findViewById<View>(R.id.et_email) as EditText
        etCompanyAdress = findViewById<View>(R.id.et_home_adress) as EditText
        etCompanyAdress!!.isSelected

        checkBox = findViewById<View>(R.id.statuteCheckBox) as CheckBox
        spinner = findViewById<View>(R.id.spinnerCattegory) as Spinner
        failString=""
        companyName=""
        companyAdress=""
        companyPhoneNumber=""
        password=""
        repeatPassword=""
        email=""

    }



    fun onClick(view: View) {
        when (view.id) {
            R.id.button_set_adress -> {
                AlertCity("", "", "", "", "")
            }
            R.id.button_statue -> {
                AlertStatue()
            }
            R.id.statuteCheckBox -> {
                acceptStatue = checkBox!!.isChecked
            }
            R.id.button_signin -> {
                tryRegister()
            }
        }

    }

    private fun AlertCity(postalCodeInit: String, cityInit: String, streetInit: String, buildInit: String, doorInit: String) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(this@SignupActivity.resources.getString(R.string.address))
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_registration_provider_adress, null)
        val etPostalCode: EditText = mDialogView.findViewById(R.id.etPostalCode)
        val etCity: EditText = mDialogView.findViewById(R.id.etCity)
        val etStreet: EditText = mDialogView.findViewById(R.id.etStreet)
        val etBuildNumber: EditText = mDialogView.findViewById(R.id.etBuildNumber)
        val etDoorNumber: EditText = mDialogView.findViewById(R.id.etDoorNumber)

        alert.setView(etPostalCode)
        alert.setView(etCity)
        alert.setView(etStreet)
        alert.setView(etBuildNumber)
        alert.setView(etDoorNumber)


        var postalCode: String =  postalCodeInit
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
            this@SignupActivity.resources.getString(R.string.next)
        ) { dialog: DialogInterface?, which: Int ->

            postalCode = etPostalCode.text.toString()
            city = etCity.text.toString()
            street = etStreet.text.toString()
            buildNumber = etBuildNumber.text.toString()
            doorNumber =etDoorNumber.text.toString()

            if (postalCode.isEmpty() || city.isEmpty() || street.isEmpty() || buildNumber.isEmpty())
            {
                Toast.makeText(
                    this@SignupActivity,this@SignupActivity.resources.getString(R.string.notFullAdress)
                    ,Toast.LENGTH_SHORT
                ).show()
                AlertCity(postalCode, city, street, buildNumber, doorNumber)
            }
            else{
                val streetString = this@SignupActivity.resources.getString(R.string.street)
                if (doorNumber.isEmpty())
                {
                    companyAdress = "$postalCode  -  $city,   \n $streetString $street $buildNumber"
                }
                else
                {
                    companyAdress = "$postalCode  -  $city,   \n $streetString $street $buildNumber/$doorNumber"
                }
                etCompanyAdress?.setText(companyAdress)
            }


        }
        alert.setNegativeButton(
            this@SignupActivity.resources.getString(R.string.cancel)
        ) { dialog: DialogInterface?, which: Int ->
            Toast.makeText(
                this@SignupActivity,
                this@SignupActivity.resources.getString(R.string.canceled),
                Toast.LENGTH_SHORT
            ).show()
        }
        alert.setIcon(R.drawable.icon_cyan)
        alert.setView(mDialogView)
        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button2_bg)
        dialog.show()
    }

    private fun AlertStatue() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(this@SignupActivity.resources.getString(R.string.statute))
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_statue_registration, null)

        alert.setPositiveButton(
            this@SignupActivity.resources.getString(R.string.next)
        ) { dialog: DialogInterface?, which: Int ->

        }

        alert.setIcon(R.drawable.icon_cyan)
        alert.setView(mDialogView)
        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawableResource(R.drawable.button2_bg)
        dialog.show()
    }

    private fun tryRegister()
    {
        cattegory = java.lang.String.valueOf(spinner!!.selectedItem)
        companyName = etCompanyName!!.text.toString()
        companyPhoneNumber = etCompanyPhoneNumber!!.text.toString()
        email = etCompanyEmail!!.text.toString()
        companyName = etCompanyName!!.text.toString()
        password = etCompanyPassword!!.text.toString()
        repeatPassword = etCompanyRepeatPassword!!.text.toString()








        if (companyName!!.length < 5)
        {
            fail=true
            failString += this@SignupActivity.resources.getString(R.string.noCompanyName) + "\n\n"
            etCompanyName!!.error = this@SignupActivity.resources.getString(R.string.noCompanyName)

        }
        if (companyAdress!!.length < 2)
        {
            fail=true
            failString += this@SignupActivity.resources.getString(R.string.nocompanyAdress) + "\n\n"
            etCompanyAdress!!.error = this@SignupActivity.resources.getString(R.string.nocompanyAdress)
            btSettAdress!!.setBackgroundResource(R.drawable.button3_bg)
        }
        else
        {
            btSettAdress!!.setBackgroundResource(R.drawable.button2_bg)
        }

        if (cattegory == "Select:") {
            fail=true
            failString += this@SignupActivity.resources.getString(R.string.noSelectedCattegory) + "\n\n"
            btCattegory!!.setBackgroundResource(R.drawable.button3_bg)
        }
        else{
            btCattegory!!.setBackgroundResource(R.drawable.button2_bg)
        }
        if (companyPhoneNumber!!.length < 9)
        {
            fail=true
            failString += this@SignupActivity.resources.getString(R.string.noPhoneNumber) + "\n\n"
            etCompanyPhoneNumber!!.error = this@SignupActivity.resources.getString(R.string.noPhoneNumber)

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            fail=true
            failString += this@SignupActivity.resources.getString(R.string.validEmail) + "\n\n"
            etCompanyEmail!!.error = this@SignupActivity.resources.getString(R.string.validEmail)

        }
        if (password!!.length < 8)
        {
            fail=true
            failString += this@SignupActivity.resources.getString(R.string.noPassword) + "\n\n"
            etCompanyPassword!!.error = this@SignupActivity.resources.getString(R.string.noPassword)
        }
        if (repeatPassword!!.length <8)
        {
            etCompanyPassword!!.error = this@SignupActivity.resources.getString(R.string.noPassword)
        }
        else
        {       if (password != repeatPassword) {
                fail=true
                failString += this@SignupActivity.resources.getString(R.string.noSamePassword) + "\n\n"
                etCompanyPassword!!.error = this@SignupActivity.resources.getString(R.string.noSamePassword)
                etCompanyRepeatPassword!!.error = this@SignupActivity.resources.getString(R.string.noSamePassword)
            }
        }




        if (!acceptStatue)
        {
            fail=true
            failString += this@SignupActivity.resources.getString(R.string.noAcceptedStatue) + "\n\n"
            checkBox!!.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this@SignupActivity, R.color.md_red_A700))
        }
        else
        {
            checkBox!!.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this@SignupActivity, R.color.md_cyan_A700))
        }


        if (!fail)
        {
        singUp(email!!, password!!)
        }
        else
        {
            Toast.makeText(
                this@SignupActivity,
                failString,
                Toast.LENGTH_SHORT
            ).show()
            fail=false
            failString=""
        }

    }
    override fun onResume() {
        super.onResume()
        setStatusBarTransparent(this@SignupActivity)
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

    private fun singUp(email: String, password: String) {

        firebaseAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getId()
                    InitFirebase()
                } else {
                    Toast.makeText(this@SignupActivity, this@SignupActivity.resources.getString(R.string.registerFAil), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getId() {
        val idNumber: String
        email = Objects.requireNonNull(firebaseAuth!!.currentUser)!!.email
        idNumber = FirebaseAuth.getInstance().currentUser!!.uid
        id = idNumber
    }

    private fun InitFirebase() {
        var initSuccces = true
        try {
                    firebaseDatabase = FirebaseDatabase.getInstance()
                         val userType: String? = "Service provider"
                       val person = Person(id, companyName!!.lowercase(), email, userType, companyAdress, companyPhoneNumber,cattegory)
                 firebaseDatabase!!.reference.child("Providers").child(id.toString()).setValue(person)


        } catch (e: Exception) {
            initSuccces = false
        }
        finally {
            if (initSuccces)
            {
                firebaseAuth!!.currentUser!!.sendEmailVerification()
                firebaseAuth!!.signOut()
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
             finishAffinity()
              Toast.makeText(
                this@SignupActivity,this@SignupActivity.resources.getString(R.string.registerSucces),Toast.LENGTH_SHORT).show()
            }
            else
            {
                firebaseAuth!!.currentUser!!.delete()
                Toast.makeText(this@SignupActivity, this@SignupActivity.resources.getString(R.string.firebaseInitFail), Toast.LENGTH_SHORT).show()
            }
        }
    }


}
