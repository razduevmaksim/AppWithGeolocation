package com.example.geolocation

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.geolocation.ui.map.MapFragment


class CustomService : Service() {
    private val tag = CustomService::class.java.simpleName

    private var instance: CustomService? = null

    fun getInstance(): CustomService? {
        if (instance == null) {
            instance = CustomService()
        }
        return instance
    }

    fun newIntent(context: Context?): Intent {
        return Intent(context, CustomService::class.java)
    }

    private var notificationManagerCompat: NotificationManagerCompat? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "onCreate()")
        notificationManagerCompat = NotificationManagerCompat.from(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent,flags, startId)
        Log.d(tag,"onStartCommand()")

        val notification = createNotification1(this)

        startForeground(MapFragment().channelId1,notification)

        return START_STICKY
    }

    private fun createNotification1(context: Context?): Notification? {
        return context?.let {
            NotificationCompat.Builder(it, MapFragment().channelIdName1)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE) //.setSmallIcon(R.drawable.ic_android_black)
                .build()
        }
    }

    fun createNotification2(context: Context?, title: String?, distance:Int) {
        Log.d(tag, "createNotification2")
        val intent = Intent(context, NotificationActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        if (notificationManagerCompat == null) notificationManagerCompat =
            NotificationManagerCompat.from(
                context!!
            )
        val notification: Notification? = context?.let {
            NotificationCompat.Builder(it, MapFragment().channelIdName2)
                .setOngoing(false)
                .setAutoCancel(false) //.setCategory(NotificationCompat.CATEGORY_CALL)
                .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_MAX or NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_baseline_location_on_24)
                .setContentTitle("Вы приблизитесь к точке $title через $distance м")
                .setContentText("Нажмите на уведомление, чтобы перейти на страницу с меткой и вашей текущей позицией")
                .setOnlyAlertOnce(false)
                .setContentIntent(pendingIntent)
                .build()
        }
        if (notification != null) {
            notificationManagerCompat?.notify(MapFragment().channelId2, notification)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy()")
    }


}