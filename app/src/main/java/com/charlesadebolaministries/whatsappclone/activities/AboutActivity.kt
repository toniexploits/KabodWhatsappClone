package com.charlesadebolaministries.whatsappclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.charlesadebolaministries.whatsappclone.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, AboutActivity::class.java)
    }
}
