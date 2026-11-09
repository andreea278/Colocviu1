package ro.pub.cs.systems.eim.practicaltest01

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.savedstate.serialization.saved

class PracticalTest01MainActivity : AppCompatActivity() {
    private lateinit var firstNumberEditText: EditText
    private lateinit var secondNumberEditText: EditText

    private var startedService = false

    private fun startServiceIfNeeded(firstNumber: Int, secondNumber: Int){
        if(firstNumber + secondNumber > Constants.THRESHOLD && !startedService){
            val intent = Intent(this@PracticalTest01MainActivity, PracticalTest01Service::class.java)
            intent.setPackage("ro.pub.cs.systems.eim.practicaltest01")
            intent.putExtra(Constants.FIRST_NUMBER_KEY, firstNumber)
            intent.putExtra(Constants.SECOND_NUMBER_KEY, secondNumber)
            startService(intent)
            startedService = true
        }
    }

    private val messageBroadcastReceiver = MessageBroadcastReceiver()

    private inner class MessageBroadcastReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra(Constants.DATA_KEY)

            Log.d(Constants.TAG, "${intent?.action}: $message")

        }
    }

    private val intentFilter = IntentFilter().apply{
        addAction(Constants.ACTION_1)
        addAction(Constants.ACTION_2)
        addAction(Constants.ACTION_3)
    }

    private val secondActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result->
        when(result.resultCode){
            Activity.RESULT_OK ->{
                Toast.makeText(this,"Ok",Toast.LENGTH_LONG).show()
            }

            Activity.RESULT_CANCELED ->{
                Toast.makeText(this,"Canceled",Toast.LENGTH_LONG).show()
            }
        }
    }

    private val buttonClickListener = ButtonClickListener()

    private inner class ButtonClickListener: View.OnClickListener{
        override fun onClick(view:View) {
            when (view.id) {
                R.id.left_button -> {
                    val count = firstNumberEditText.text.toString().toInt()
                    firstNumberEditText.setText((count + 1).toString())
                    startServiceIfNeeded(firstNumberEditText.text.toString().toInt(), secondNumberEditText.text.toString().toInt())

                }
                R.id.right_button->{
                    var count = secondNumberEditText.text.toString().toInt()
                    count++
                    secondNumberEditText.setText(count.toString())
                    startServiceIfNeeded(firstNumberEditText.text.toString().toInt(), secondNumberEditText.text.toString().toInt())
                }
                R.id.navigate_to_activity_button->{
                    val intent = Intent(
                        this@PracticalTest01MainActivity,
                        PracticalTest01SecondaryActivity::class.java
                    )
                    intent.setPackage("ro.pub.cs.systems.eim.practicaltest01")
                    val sum = firstNumberEditText.text.toString().toInt() + secondNumberEditText.text.toString().toInt()
                    intent.putExtra(Constants.RESULT_KEY, sum)
                    secondActivityLauncher.launch(intent)

                }

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test01_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firstNumberEditText = findViewById(R.id.first_number_edit_text)
        secondNumberEditText = findViewById(R.id.second_number_edit_text)

        findViewById<Button>(R.id.right_button).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.left_button).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.navigate_to_activity_button).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.navigate_to_activity_button).setOnClickListener(buttonClickListener)

    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString("first_number", firstNumberEditText.text.toString())
        outState.putString("second_number", secondNumberEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle){
        super.onRestoreInstanceState(savedInstanceState)
        firstNumberEditText.setText(savedInstanceState.getString("first_number"))
        secondNumberEditText.setText(savedInstanceState.getString("second_number"))

    }

    override fun onDestroy(){
        stopService(Intent(this, PracticalTest01Service::class.java))
        super.onDestroy()
    }

    override fun onResume(){
        super.onResume()
        ContextCompat.registerReceiver(this, messageBroadcastReceiver, intentFilter,ContextCompat.RECEIVER_EXPORTED)
    }

    override fun onPause(){
        unregisterReceiver(messageBroadcastReceiver)
        super.onPause()
    }

}