package ro.pub.cs.systems.eim.practicaltest01

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PracticalTest01MainActivity : AppCompatActivity() {

    private lateinit var  field1TextView: EditText
    private lateinit var field2TextView: EditText

    private var serviceStarted = false

    private var isVisible = false

    private fun startSericeIfNeeded(){
        val leftValue = field1TextView.text.toString().toInt()
        val rightValue = field2TextView.text.toString().toInt()

        if(leftValue+rightValue > Constants.NUMBER_OF_CLICKS_THRESHOLD && !serviceStarted){
            val intent = Intent(applicationContext, PracticalTest01Service::class.java)
            intent.putExtra(Constants.FIRST_NUMBER, leftValue)
            intent.putExtra(Constants.SECOND_NUMBER, rightValue)
            ContextCompat.startForegroundService(this,intent)
            serviceStarted = true
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        granted->
        Log.d(Constants.TAG, "Notification permission granted : $granted")
    }

    private val messageBroadcastReceiver = MessageBroadcastReceiver()

    private inner class MessageBroadcastReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra(Constants.DATA) ?: return
            Log.d(Constants.BROADCAST_RECEIVER_TAG, "${intent?.action} → $message")

            if(!isVisible){
                showNotification(message)
            }
        }
    }

    private fun showNotification(message: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return                                              // no permission → nothing to show

        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    Constants.MESSAGES_CHANNEL_ID,
                    "Messages",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        // what happens when the user taps the notification
        val openIntent = Intent(this, PracticalTest01MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(Constants.DATA, message)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, Constants.MESSAGES_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("New message")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(Constants.MESSAGE_NOTIFICATION_ID, notification)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val message = intent.getStringExtra(Constants.DATA)
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private val intentFilter = IntentFilter().apply{
        addAction(Constants.ACTION_ARITHMETIC_MEAN)
        addAction(Constants.ACTION_GEOMETRIC_MEAN)
        addAction(Constants.ACTION_BOTH_MEANS)
    }
    private val secondActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        result->
        Toast.makeText(this, "The activity returned with result ${result.resultCode}", Toast.LENGTH_LONG).show()
    }

    private val buttonClickListener = ButtonClickListener()

    private inner class ButtonClickListener: View.OnClickListener{
        override fun onClick(view:View){
            when(view.id){
                R.id.left_button ->{
                    val leftValue = field1TextView.text.toString().toInt()
                    field1TextView.setText((leftValue + 1).toString())
                }
                R.id.right_button->{
                    val rightValue = field2TextView.text.toString().toInt()
                    field2TextView.setText((rightValue +1).toString())
                }
                R.id.second_activity_button->{
                    val leftValue = field1TextView.text.toString().toInt()
                    val rightValue = field2TextView.text.toString().toInt()

                    val intent =
                        Intent("ro.pub.cs.systems.eim.practicaltest01.intent.action.PracticalTest01SecondaryActivity")

                    intent.putExtra(Constants.NUMBER_OF_CLICKS, leftValue+rightValue)
                    secondActivityLauncher.launch(intent)


                }
            }
            startSericeIfNeeded()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_practical_test01_main)
        field1TextView = findViewById(R.id.field1_text_view)
        field2TextView = findViewById(R.id.field2_text_view)

        findViewById<Button>(R.id.left_button).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.right_button).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.second_activity_button).setOnClickListener(buttonClickListener)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        ContextCompat.registerReceiver(
            this,
            messageBroadcastReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString(Constants.LEFT_COUNT, field1TextView.text.toString())
        outState.putString(Constants.RIGHT_COUNT, field2TextView.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle){
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState.containsKey(Constants.LEFT_COUNT)){
            field1TextView.setText(savedInstanceState.getString(Constants.LEFT_COUNT))

        }
        if(savedInstanceState.containsKey(Constants.RIGHT_COUNT)){
            field2TextView.setText(savedInstanceState.getString(Constants.RIGHT_COUNT))
        }
    }

    override fun onDestroy(){
        unregisterReceiver(messageBroadcastReceiver)
        val intent = Intent(applicationContext, PracticalTest01Service::class.java)
        stopService(intent)
        super.onDestroy()
    }

    override fun onResume(){
        super.onResume()
        isVisible = true
    }

    override fun onPause(){
        isVisible = false
        super.onPause()
    }
}