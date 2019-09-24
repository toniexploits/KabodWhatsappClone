package com.example.whatsappclone.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.whatsappclone.R
import com.example.whatsappclone.activities.StatusActivity
import com.example.whatsappclone.adapters.StatusListAdapter
import com.example.whatsappclone.listeners.StatusItemClickListener
import com.example.whatsappclone.util.DATA_USERS
import com.example.whatsappclone.util.DATA_USER_CHATS
import com.example.whatsappclone.util.StatusListElement
import com.example.whatsappclone.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_status_list.*


class StatusListFragment : Fragment(), StatusItemClickListener {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var statusListAdapter = StatusListAdapter(arrayListOf())

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status_list, container, false)
    }

    override fun onItemClicked(statusElement: StatusListElement) {
        startActivity(StatusActivity.getIntent(context, statusElement))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusListAdapter.setOnItemClickListener(this)

        statusListRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = statusListAdapter
            addItemDecoration(DividerItemDecoration(this@StatusListFragment.context, DividerItemDecoration.VERTICAL))
        }
    }

    fun onVisible(){
        statusListAdapter.onRefresh()
        refreshList()
    }

    fun refreshList(){
        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .get()
            .addOnSuccessListener {doc ->
                if(doc.contains(DATA_USER_CHATS)){
                    val partners = doc[DATA_USER_CHATS]
                    for(partner in (partners as HashMap<String, String>).keys){
                        firebaseDB.collection(DATA_USERS)
                            .document(partner)
                            .get()
                            .addOnSuccessListener {documentSnapshot ->
                                val partner = documentSnapshot.toObject(User::class.java)
                                if(partner != null){
                                    if(!partner.status.isNullOrEmpty() && !partner.statusUrl.isNullOrEmpty()){
                                        val newElement = StatusListElement(partner.name, partner.imageUrl,
                                            partner.status, partner.statusUrl, partner.statusTime)
                                        statusListAdapter.addElement(newElement)
                                    }
                                }
                            }
                    }
                }
            }
    }

}
