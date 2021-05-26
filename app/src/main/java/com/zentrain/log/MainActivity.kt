package com.zentrain.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity() {

//    private var isCancelled = false
//    lateinit var button_start : Button
//    lateinit var button_stop : Button
//    lateinit var text_view : TextView


    var  isCancelled = false


     //60 seconds (1 minute)
        val minute:Long = 1000 * 60 // 1000 milliseconds = 1 second

        // 1 day 2 hours 35 minutes 50 seconds
        val millisInFuture:Long = (minute * 0) + (minute * 0) + (1000 * 60)

        // Count down interval 1 second
        val countDownInterval:Long = 1000



// [START declare_auth]
private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    lateinit var  phno : EditText
    lateinit var  sendbtn : Button
    lateinit var  otp : EditText
    lateinit var submit : Button
    lateinit var resend : Button
    lateinit var Countdown : TextView



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide(); // hide the title bar


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        id()

        sendbtn.setOnClickListener {
        val phnum =    phno.text.toString()
            sendbtn.visibility =View.GONE
            startPhoneNumberVerification("+91"+phnum)
           // Toast.makeText(this,"OTP Sent",Toast.LENGTH_SHORT).show()

        }
     resend.setOnClickListener {
         Countdown.text = ""
         val phnum =    phno.text.toString()
         resendVerificationCode("+91"+phnum, resendToken)
     Toast.makeText(this,"code sent",Toast.LENGTH_SHORT).show()
         resend.isClickable = false
     }
      val tV:TextView = findViewById(R.id.tV)
        tV.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }

        submit.setOnClickListener {
            val otp = otp.text.toString()

            verifyPhoneNumberWithCode(storedVerificationId,otp)


            if (otp.trim().isBlank()) {
                Toast.makeText(this,"enter OTP",Toast.LENGTH_SHORT).show()
            }else{
                submit.isClickable= true
            }

            startActivity(Intent(this, Aswin::class.java ))


        }


        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("TAG", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)

            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }
        // [END phone_auth_callbacks]
    }

    private fun id(){

        submit = findViewById(R.id.button3)
        sendbtn = findViewById(R.id.otp)
       otp = findViewById(R.id.otpnum)
        resend = findViewById(R.id.resend)
        phno = findViewById(R.id.phno)
      Countdown = findViewById(R.id.countdown)


    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

    }
    // [END on_start_check_user]

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        timer(millisInFuture,countDownInterval).start()
        // [END start_phone_auth]
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]
    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,"enter valid otp",Toast.LENGTH_LONG).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

// Method to configure and return an instance of CountDownTimer object
    private fun timer(millisInFuture:Long,countDownInterval:Long):CountDownTimer{
        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                val timeRemaining = timeString(millisUntilFinished)
                if (isCancelled){
                    Countdown.text = "${Countdown.text}\nStopped.(Cancelled)"
                    cancel()
                }else{
                    Countdown.text = timeRemaining
                }
            }

            override fun onFinish() {
                Countdown.text = "Done"
                resend.visibility = View.VISIBLE
               sendbtn.isEnabled = false
                resend.isEnabled = true

            }
        }
    }

   //  Method to get days hours minutes seconds from milliseconds
    private fun timeString(millisUntilFinished:Long):String{
        var millisUntilFinished:Long = millisUntilFinished
        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
        millisUntilFinished -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

        // Format the string
        return String.format(
                Locale.getDefault(),
                "%02d : %02d ", minutes,seconds
        )
    }




    // [END sign_in_with_phone]


}














//
//        button_start = findViewById(R.id.otp)
//        button_stop = findViewById(R.id.resend)
//        text_view = findViewById(R.id.tV)
//
//
//        // Count down timer start button
//        button_start.setOnClickListener{
//            // Start the timer
//            button_start.visibility = View.GONE
//            Toast.makeText(this,"OTP Sent",Toast.LENGTH_SHORT).show()
//            timer(millisInFuture,countDownInterval).start()
//
//
//            it.isEnabled = false
//            button_stop.isEnabled = false
//
//            isCancelled = false
//        }
//
//
//        // Count down timer stop/cancel button
//        button_stop.setOnClickListener{
//            // Start the timer
//           // button_stop.visibility = View.GONE
//            Toast.makeText(this,"OTP Sent",Toast.LENGTH_SHORT).show()
//            timer(millisInFuture,countDownInterval).start()
//            it.isEnabled = false
//            button_stop.isEnabled = false
//
//            isCancelled = false
//            button_stop.isClickable = false
//
//
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//// Extension function to show toast message
//fun Context.toast(message: String) {
//    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//}


//            mTextField.visibility = View.VISIBLE
//            object : CountDownTimer(80000, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
//                    mTextField.setText("Enter otp within: " + millisUntilFinished / 1000)
//                }
//
//                override fun onFinish() {
//                   button_stop.visibility = View.VISIBLE
//                }
//            }.start()
//
//        }
//
//        button_stop.setOnClickListener {
//            button_stop.visibility = View.GONE
//
//
//
//            val mTextField: TextView = findViewById(R.id.time)
//
//            mTextField.visibility = View.VISIBLE
//            object : CountDownTimer(80000, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
//                    mTextField.setText("Enter otp within: " + millisUntilFinished / 1000)
//                }
//
//                override fun onFinish() {
//                    button_stop.visibility = View.VISIBLE
//                }
//            }.start()
//        }
//}
//}
















