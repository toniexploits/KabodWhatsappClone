package com.charlesadebolaministries.whatsappclone.listeners

import com.charlesadebolaministries.whatsappclone.util.StatusListElement

interface StatusItemClickListener {
    fun onItemClicked(statusElement: StatusListElement)
}