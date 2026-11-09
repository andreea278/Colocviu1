package ro.pub.cs.systems.eim.practicaltest01

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class PracticalTest01SecondaryActivity: AppCompatActivity() {
    private lateinit var nrOfClicksButton: EditText

    private val buttonClickListener = ButtonClickListener()


    private inner class ButtonClickListener: View.OnClickListener{
        override fun onClick(view:View){
            when(view.id){
                R.id.ok_button -> setResult(Activity.RESULT_OK)
                R.id.cancel_button -> setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test01_secondary)
        nrOfClicksButton = findViewById<EditText>(R.id.nr_of_clicks_edit_text)
        val numberOfClicks = intent.getIntExtra(Constants.NUMBER_OF_CLICKS, 0)
        nrOfClicksButton.setText(numberOfClicks.toString())


        findViewById<Button>(R.id.ok_button).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.cancel_button).setOnClickListener(buttonClickListener)
    }
}