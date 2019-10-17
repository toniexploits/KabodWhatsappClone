package com.charlesadebolaministries.whatsappclone.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.charlesadebolaministries.whatsappclone.R
import com.charlesadebolaministries.whatsappclone.fragments.ChatsFragment
import com.charlesadebolaministries.whatsappclone.fragments.StatusListFragment
import com.charlesadebolaministries.whatsappclone.fragments.StatusUpdateFragment
import com.charlesadebolaministries.whatsappclone.listeners.FailureCallback
import com.charlesadebolaministries.whatsappclone.util.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity(), FailureCallback {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var nSectionsPagerAdapter: SectionPagerAdapter? = null

    private val chatsFragment = ChatsFragment()
    private val statusUpdateFragment = StatusUpdateFragment()
    private val statusListFragment = StatusListFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatsFragment.setFailureCallbackListener(this)

        setSupportActionBar(toolbar)
        nSectionsPagerAdapter = SectionPagerAdapter(supportFragmentManager)

        container.adapter = nSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        resizeTabs()
        tabs.getTabAt(1)?.select()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {fab.hide()}
                    1 -> {fab.show()}
                    2 -> {
                        fab.hide()
                        statusListFragment.onVisible()
                    }
                }
            }

        })



    }

    override fun onUserError() {
        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        startActivity(LoginActivity.newIntent(this))

        finish()
    }

    fun onNewChat(view: View){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            //permission not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)){
                AlertDialog.Builder(this)
                    .setTitle("Contact Permission")
                    .setMessage("This app requires access to your contacts to initiate conversation")
                    .setPositiveButton("Ask Me"){dialog, which -> requestContactsPermission()}
                    .setNegativeButton("No"){dialog, which ->  }
                    .show()
            }
            else{
                requestContactsPermission()
            }
        }
        else{
           //permission granted
            startNewActivity(REQUEST_NEW_CHAT)
        }

    }
    
    fun requestContactsPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startNewActivity(REQUEST_NEW_CHAT)
                }
            }
        }
    }

    fun startNewActivity(requestCode: Int){
        when(requestCode){
            REQUEST_NEW_CHAT -> startActivityForResult(ContactsActivity.newIntent(this), REQUEST_NEW_CHAT)
            REQUEST_CODE_PHOTO -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE_PHOTO)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CODE_PHOTO -> statusUpdateFragment.storeImage(data?.data)
                REQUEST_NEW_CHAT -> {
                    val name = data?.getStringExtra(PARAM_NAME) ?: ""
                    val phone = data?.getStringExtra(PARAM_PHONE) ?: ""
                    checkNewChatUser(name, phone)
                }
            }
        }
    }

    fun checkNewChatUser(name: String, phone: String){
        if(!name.isNullOrEmpty() && !phone.isNullOrEmpty()){

//            val userId = firebaseAuth.currentUser?.uid
//
//            firebaseDB.collection(DATA_USERS)
//                .document(userId!!)
//                .get()
//                .addOnSuccessListener { documentSnapshot ->
//                    val user = documentSnapshot.toObject(User::class.java)
//                    val ph = user?.phone
//                    if(getPhoneSubstring(ph.toString()) == getPhoneSubstring(phone)){
//                        chatsFragment.newChat(documentSnapshot.id)
//                    }else{
//                        AlertDialog.Builder(this)
//                            .setTitle("User not found")
//                            .setMessage("$name does not have an account. Send them an sms to install this app.")
//                            .setPositiveButton("Ok"){dialog, which ->
//                                val intent = Intent(Intent.ACTION_VIEW)
//                                intent.data = Uri.parse("sms:$phone")
//                                intent.putExtra("sms_body", "Hi! I'm using this new cool chat app, Kabod App. " +
//                                        "It has many useful functionalities you would love. Click now to install it <<link>>")
//                                startActivity(intent)
//                            }
//                            .setNegativeButton("Cancel", null)
//                            .show()
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this, "An error occured. Pls try again later.", Toast.LENGTH_SHORT).show()
//                    e.printStackTrace()
//                }


            firebaseDB.collection(DATA_USERS)
                .whereEqualTo(DATA_USER_PHONE, getPhoneSubstring(phone))
                .get()
                .addOnSuccessListener { result ->
                    if(result.documents.size > 0){
                        chatsFragment.newChat(result.documents[0].id)
                    }else{
                        AlertDialog.Builder(this)
                            .setTitle("User not found")
                            .setMessage("$name does not have an account. Send them an sms to install this app.")
                            .setPositiveButton("Ok"){dialog, which ->
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("sms:$phone")
                                intent.putExtra("sms_body", "Hi! I'm using this new cool chat app, Kabod App. " +
                                        "It has many useful functionalities you would love. Click now to install it <<link>>")
                                startActivity(intent)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "An error occured. Pls try again later.", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        }
    }

    fun getPhoneSubstring(string: String): String{
        return string.substring(string.length - 10)
    }

    fun resizeTabs(){
        val layout = (tabs.getChildAt(0) as LinearLayout).getChildAt(0) as LinearLayout
        val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 0.4f
        layout.layoutParams = layoutParams
    }

    override fun onResume() {
        super.onResume()

        if(firebaseAuth.currentUser == null){
            startActivity(LoginActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.action_profile -> onProfile()
//            R.id.action_logout -> onLogout()
            R.id.action_about -> onAbout()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onProfile(){
        startActivity(ProfileActivity.newIntent(this))
    }

//    private fun onLogout(){
////        firebaseAuth.signOut()
////        startActivity(LoginActivity.newIntent(this))
////        finish()
////    }

    private fun onAbout(){
        startActivity((AboutActivity.newIntent(this)))
    }

    inner class SectionPagerAdapter(fm: FragmentManager):FragmentPagerAdapter(fm){
        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> statusUpdateFragment
                1 -> chatsFragment
                2 -> statusListFragment
                else -> statusUpdateFragment
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }

    companion object{
        val PARAM_NAME = "Param name"
        val PARAM_PHONE = "Param phone"

        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}
