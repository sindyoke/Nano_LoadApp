package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var downloadID: Long = 0
    private var filename = ""
    private var URL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val items = resources.getStringArray(R.array.download_options)
        Log.d(TAG, "${items.size} items loaded")
        val itemUrls = resources.getStringArray(R.array.download_urls)
        val radioGroup: RadioGroup = findViewById(R.id.download_options)

        for (item in items) {
            val radioButton = RadioButton(this)
            val params = RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(16, 24, 16, 8)
            radioButton.layoutParams = params
            radioButton.text = item
            radioButton.textSize = 18f
            radioGroup.addView(radioButton)
        }

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                1 -> {
                    URL = itemUrls[0]
                    filename = items[0]
                }
                2 -> {
                    URL = itemUrls[1]
                    filename = items[1]
                }
                3 -> {
                    URL = itemUrls[2]
                    filename = items[2]
                }
            }
        }

        custom_button.setOnClickListener {
            custom_button.setButtonState(ButtonState.Clicked)
            if (radioGroup.checkedRadioButtonId != -1) {
                custom_button.setButtonState(ButtonState.Clicked)
                download()
            } else {
                Toast.makeText(this, "Please choose option", Toast.LENGTH_SHORT).show()
            }
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.d(TAG, "Main: downloadId is $id")
            if (id == downloadID) {
                val notificationManager = context?.let {
                    ContextCompat.getSystemService(
                        it,
                        NotificationManager::class.java
                    )
                } as NotificationManager
                custom_button.setButtonState(ButtonState.Completed)
                notificationManager.sendNotification(
                    getText(R.string.notification_text).toString(),
                    context,
                    filename,
                    intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                )
            }
        }
    }

    private fun download() {
        custom_button.setButtonState(ButtonState.Loading)
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                    .apply {
                        setShowBadge(false)
                    }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "File downloaded"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
