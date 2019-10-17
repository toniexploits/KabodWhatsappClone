package com.charlesadebolaministries.whatsappclone.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.charlesadebolaministries.whatsappclone.R
import com.charlesadebolaministries.whatsappclone.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.progressLayout
import java.text.SimpleDateFormat


class ProfileActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if(userId.isNullOrEmpty()){
            finish()
        }

        progressLayout.setOnTouchListener { v, event -> true }
        photoIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, "video/*")
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }

        populateInfo()

    }

    private fun populateInfo(){
        progressLayout.visibility = View.GONE
        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                imageUrl = user?.imageUrl
                nameET.setText(user?.name, TextView.BufferType.EDITABLE)
                emailET.setText(user?.email, TextView.BufferType.EDITABLE)
                //phoneET.setText(user?.phone, TextView.BufferType.EDITABLE)
                occupationeET.setText(user?.occupation, TextView.BufferType.EDITABLE)
                maritalStatusET.setText(user?.maritalStatus, TextView.BufferType.EDITABLE)
                //dobET.setText(user?.birthDate, TextView.BufferType.EDITABLE)
                cityStateET.setText(user?.cityState, TextView.BufferType.EDITABLE)
                if(imageUrl != null){
                    populateImage(this, user?.imageUrl, photoIV, R.drawable.profile_icon)
                }
                progressLayout.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }

    fun onUpdate(v: View){
        progressLayout.visibility = View.GONE
        val dd = birthDatePicker.dayOfMonth
        val mm = birthDatePicker.month + 1
        val yy = birthDatePicker.year
        val birthDate = "$dd-$mm-$yy"
        val dob = SimpleDateFormat("dd-MM-yyyy").parse(birthDate)

        val dateOfBirth = SimpleDateFormat("MMM dd, yyyy").format(dob)

//        println(birthDate)
//        println("Date formatted: $dateOfBirth")

        val name = nameET.text.toString()
        val email = emailET.text.toString()
        //val phone = phoneET.text.toString()
        val cityState = cityStateET.text.toString()

        val maritalStatus = maritalStatusET.text.toString()
        val occupation = occupationeET.text.toString()

        val map = HashMap<String, Any>()
        map[DATA_USER_NAME] = name
        map[DATA_USER_EMAIL] = email
        //map[DATA_USER_PHONE] = phone
        map[DATA_USER_CITY_STATE] = cityState
        map[DATA_USER_BIRTHDATE] = dateOfBirth
        map[DATA_USER_MARITAL_STATUS] = maritalStatus
        map[DATA_USER_OCCUPATION] = occupation

        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .update(map)
            .addOnSuccessListener {
                Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                progressLayout.visibility = View.GONE
            }

        startActivity(MainActivity.newIntent(this))
    }

    fun onDelete(v: View){
        progressLayout.visibility = View.VISIBLE
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("This will delete your profile account. Are you sure you want to do this?")
            .setPositiveButton("Yes"){dialog, which ->
                firebaseDB.collection(DATA_USERS).document(userId!!).delete()
                firebaseStorage.child(DATA_USERS).child(userId).delete()
                firebaseAuth.currentUser?.delete()
                    ?.addOnSuccessListener {
                        finish()
                    }
                    ?.addOnFailureListener {
                        finish()
                    }
                //Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                    //finish()
            }
            .setNegativeButton("No"){dialog, which ->
                progressLayout.visibility = View.GONE
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO){
            storeImage(data?.data)
        }
    }

    private fun storeImage(imageUri: Uri?){
        if(imageUri != null) {
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
            progressLayout.visibility = View.VISIBLE
            val filePath: StorageReference = firebaseStorage.child(DATA_IMAGES).child(userId!!)

            filePath.putFile(imageUri)
                .addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener { taskSnapshot ->
                            val url: String = taskSnapshot.toString()
                            firebaseDB.collection(DATA_USERS)
                                .document(userId)
                                .update(DATA_USER_IMAGE_URL, url)
                                .addOnSuccessListener {
                                    imageUrl = url
                                    populateImage(this, imageUrl, photoIV, R.drawable.profile_icon)
                                }
                            progressLayout.visibility = View.GONE
                        }
                        .addOnFailureListener {
                            uploadFailure()
                        }
                }
                .addOnFailureListener{
                    uploadFailure()
                }
        }
    }

    private fun uploadFailure(){
        Toast.makeText(this, "Image upload failed. Please try again later", Toast.LENGTH_SHORT).show()
        progressLayout.visibility = View.GONE
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, ProfileActivity::class.java)
    }
}
