package com.charlesadebolaministries.whatsappclone.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.charlesadebolaministries.whatsappclone.R
import com.charlesadebolaministries.whatsappclone.activities.StatusActivity
import com.charlesadebolaministries.whatsappclone.adapters.StatusListAdapter
import com.charlesadebolaministries.whatsappclone.listeners.StatusItemClickListener
import com.charlesadebolaministries.whatsappclone.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_status_list.*


class StatusListFragment : Fragment(), StatusItemClickListener {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var statusListAdapter = StatusListAdapter(arrayListOf())

    lateinit var element: StatusListElement

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

//        userStatusDetails()
//
//        val layout = view.findViewById<RelativeLayout>(R.id.userItemLayout)
//        val elementIV = view.findViewById<ImageView>(R.id.userStatusIV)
//        val elementNameTV = view.findViewById<TextView>(R.id.userNameTV)
//        val elementTimeTV = view.findViewById<TextView>(R.id.userTimeTV)
//
//        populateImage(elementIV.context, element.statusUrl, elementIV, R.drawable.profile_icon_small)
//        elementNameTV.text = element.userName
//        elementTimeTV.text = element.statusTime
//        layout.setOnClickListener{onItemClicked(element)}

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

    fun userStatusDetails(){
        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .get()
            .addOnSuccessListener { result ->
                val res = result.toObject(User::class.java)
                if(res != null){
                    element = StatusListElement(res.name, res.imageUrl, res.status, res.statusUrl, res.statusTime)
                }
            }

    }

}
