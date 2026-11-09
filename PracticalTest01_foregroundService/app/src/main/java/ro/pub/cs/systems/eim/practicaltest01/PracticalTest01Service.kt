package ro.pub.cs.systems.eim.practicaltest01

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class PracticalTest01Service: Service() {

    private  var processingThread :ProcessingThread ?=null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int{
        startAsForeground()
        val first = intent?.getIntExtra("firstNumber",0) ?: 0
        val second = intent?.getIntExtra("secondNumber", 0) ?: 0

        processingThread = ProcessingThread(this, first, second)
        processingThread?.start()

        return START_REDELIVER_INTENT
    }

    private fun startAsForeground(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.SERVICE_CHANNEL_ID,
                "Background service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, Constants.SERVICE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("PracticalTest01")
            .setContentText("Sending messages every 10 seconds")
            .build()

        ServiceCompat.startForeground(
            this,
            Constants.SERVICE_NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy(){
        processingThread?.stopThread()
        super.onDestroy()
    }
}