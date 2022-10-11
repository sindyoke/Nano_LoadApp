package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val bundle = intent.extras
        val filename = bundle?.getString("downloadDetails")
        val status = bundle?.getBoolean("downloadStatus")

        tv_filename.text = filename
        tv_status.text = if (status == true) "Success" else "Fail"

        fab.setOnClickListener {
            finish()
        }
    }



}
