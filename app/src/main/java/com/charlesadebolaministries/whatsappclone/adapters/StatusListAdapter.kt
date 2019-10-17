package com.charlesadebolaministries.whatsappclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.charlesadebolaministries.whatsappclone.R
import com.charlesadebolaministries.whatsappclone.listeners.StatusItemClickListener
import com.charlesadebolaministries.whatsappclone.util.StatusListElement
import com.charlesadebolaministries.whatsappclone.util.populateImage

class StatusListAdapter(val statusList: ArrayList<StatusListElement>): RecyclerView.Adapter<StatusListAdapter.StatusListViewHolder>() {

    private var clickListener: StatusItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StatusListViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_status_list, parent, false)
    )

    fun onRefresh(){
        statusList.clear()
        notifyDataSetChanged()
    }

    fun addElement(element: StatusListElement){
        statusList.add(element)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: StatusItemClickListener){
        clickListener = listener
        notifyDataSetChanged()
    }

    override fun getItemCount() = statusList.size

    override fun onBindViewHolder(holder: StatusListViewHolder, position: Int) {
        holder.bind(statusList[position], clickListener)
    }

    class StatusListViewHolder (view: View): RecyclerView.ViewHolder(view){

        private var layout = view.findViewById<RelativeLayout>(R.id.itemLayout)
        private var elementIV = view.findViewById<ImageView>(R.id.thisUserStatusIV)
        private var elementNameTV = view.findViewById<TextView>(R.id.thisUserNameTV)
        private var elementTimeTV = view.findViewById<TextView>(R.id.thisUserTimeTV)

        fun bind(element: StatusListElement, listener: StatusItemClickListener?){
            populateImage(elementIV.context, element.statusUrl, elementIV, R.drawable.profile_icon_small)
            elementNameTV.text = element.userName
            elementTimeTV.text = element.statusTime
            layout.setOnClickListener{listener?.onItemClicked(element)}
        }
    }
}