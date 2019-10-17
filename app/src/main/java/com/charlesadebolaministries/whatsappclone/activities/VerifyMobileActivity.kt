package com.charlesadebolaministries.whatsappclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.charlesadebolaministries.whatsappclone.R
import com.charlesadebolaministries.whatsappclone.util.DATA_USERS
import com.charlesadebolaministries.whatsappclone.util.User
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit


class VerifyMobileActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB = FirebaseFirestore.getInstance()

    private var country : String? = null
    private var phoneNumber : String? = null
    var mVerifyId : String? = null
    lateinit var mCallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks


//    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
//        val user = firebaseAuth.currentUser?.uid
//        if(user != null){
//            startActivity(MainActivity.newIntent(this))
//            finish()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_mobile)

        //println("We are here.")

        val otpTxt = findViewById<EditText>(R.id.otpcodeET)
        val btn = findViewById<Button>(R.id.phoneSignupBtn)

        phoneNumber = intent.extras?.getString(PARAM_PHONE_NO)
        country = intent.extras?.getString(PARAM_COUNTRY)

        mCallbacks = object:PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@VerifyMobileActivity, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(code: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(code, p1)
                mVerifyId = code
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                //Getting the code sent by SMS
                val code = phoneAuthCredential.smsCode
                //sometime the code is not detected automatically, so user manually enters the code
                if (code != null)
                {
                    otpTxt.setText(code)
                    //verifying the code
                    verifyVerificationCode(code)

                }
            }
        }

        sendVerificationCode(phoneNumber!!)

        btn.setOnClickListener {
            val otp = otpTxt.text.toString().trim()
            if(otp.isNullOrEmpty() || otp.length < 6){
                otpTxt.error = "Enter valid code"
                otpTxt.requestFocus()
            }
            //verifying the code entered manually
            verifyVerificationCode(otp)
        }


    }


    fun sendVerificationCode(num : String){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(num, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks)
    }

    fun verifyVerificationCode(code : String){
        val credential = PhoneAuthProvider.getCredential(mVerifyId!!, code)
        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                    if (!task.isSuccessful) {
                        //verification unsuccessful.. display an error message
                        Toast.makeText(this@VerifyMobileActivity, "Signup Error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT).show()
                    } else if(firebaseAuth.uid != null) {
                        val phone = phoneNumber
                        val country = country
                        val user = User("", "", phone,country, "Hello, I'm new!", "", "")
                        firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).set(user)

                        //verification successful we will start the profile activity
                        startActivity(ProfileActivity.newIntent(this))
//                        val intent = Intent(this@VerifyMobileActivity, ProfileActivity::class.java)
//                        intent.flags =
//                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(intent)
                    }
                }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }


    companion object{
        private val PARAM_COUNTRY = "Country"
        private val PARAM_PHONE_NO = "Phone Number"

        fun newIntent(context: Context?, phoneNo: String?, country: String?) : Intent{
            val intent = Intent(context, VerifyMobileActivity::class.java)
            intent.putExtra(PARAM_COUNTRY, country)
            intent.putExtra(PARAM_PHONE_NO, phoneNo)
            return intent
        }
    }
}
