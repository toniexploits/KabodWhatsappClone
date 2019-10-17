package com.charlesadebolaministries.whatsappclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.charlesadebolaministries.whatsappclone.R
import com.charlesadebolaministries.whatsappclone.util.AttachOption

class AttachFileAdapter(val context: Context, val attachOptions: List<AttachOption>, val itemClick: (AttachOption) -> Unit) : RecyclerView.Adapter<AttachFileAdapter.AttachFileHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachFileHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_attach_option, parent, false)
        return AttachFileHolder(view, itemClick)
    }

    override fun getItemCount()= attachOptions.count()

    override fun onBindViewHolder(holder: AttachFileHolder, position: Int) {
        holder.bindAttachOption(context, attachOptions[position])
    }

    inner class AttachFileHolder(v: View, itemClick: (AttachOption) -> Unit) : RecyclerView.ViewHolder(v){
        val optionImage = v.findViewById<ImageView>(R.id.optionImageIV)
        val optionTitle = v.findViewById<TextView>(R.id.optionTitleTV)

        fun bindAttachOption(context: Context, attachOption: AttachOption){
            val resourceId = context.resources.getIdentifier(attachOption.image, "drawable", attachOption.image.toString())
            optionImage.setImageResource(resourceId)
            optionTitle.text = attachOption.title

            itemView.setOnClickListener{itemClick(attachOption)}
        }
    }
}