package com.example.otp_authantication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_phone.*
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId :String
    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        auth= FirebaseAuth.getInstance()
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Toast.makeText(applicationContext,"aaya 1",Toast.LENGTH_LONG).show()

                val code=credential.smsCode
                if(code!=null)
                {
                    et_otp.setText(code)
                }
                else
                    Toast.makeText(applicationContext,"Auth failed!",Toast.LENGTH_LONG).show()

            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext,"Auth failed",Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Toast.makeText(applicationContext,"aaya 2",Toast.LENGTH_LONG).show()

                storedVerificationId = verificationId
                resendToken = token
                phone_layout.visibility=View.GONE
                otp_layout.visibility=View.VISIBLE


            }
        }

        bt_getOtp.setOnClickListener {
            var phoneNo=et_phone.text.toString().trim()
            if(phoneNo.isNotEmpty())
            {
                Toast.makeText(applicationContext,"ver",Toast.LENGTH_LONG).show()


                sendVerificationCode(phoneNo)
            }
            else
                Toast.makeText(applicationContext,"Please Enter PhoneNumber",Toast.LENGTH_LONG).show()

        }

        bt_signup.setOnClickListener {
            var otp= et_otp.text.toString().trim()
            if(otp.isNotEmpty())
            {
                verifyVerificationCode(otp)
            }
            else
                Toast.makeText(applicationContext,"Wrong OTP",Toast.LENGTH_LONG).show()
        }
    }

    private fun sendVerificationCode(phoneNo:String)
    {
        Toast.makeText(applicationContext,"ver 1",Toast.LENGTH_LONG).show()

//
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phoneNo)       // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(this)                 // Activity (for callback binding)
//            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNo,
            60,
            TimeUnit.SECONDS,
            this,
            callbacks
        )

    }

    private fun verifyVerificationCode(code:String)
    {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signUp(credential)
    }

    private fun signUp(credential:PhoneAuthCredential)
    {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Toast.makeText(applicationContext,"Successful",Toast.LENGTH_LONG).show()
                    val intent= Intent(applicationContext,HomeActivity::class.java)
                    startActivity(intent)


                }
                else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(applicationContext,"Code is incorrect",Toast.LENGTH_LONG).show()
                        et_otp.setText("")
                    }

                }
            }



    }




}