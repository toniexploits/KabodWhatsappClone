package com.charlesadebolaministries.whatsappclone.listeners

interface PhoneAuthListener {
    fun onPhoneAuthClicked(country: String?, phoneNumber: String?)
}