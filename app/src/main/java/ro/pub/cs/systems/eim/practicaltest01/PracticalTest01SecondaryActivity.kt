package ro.pub.cs.systems.eim.practicaltest01

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PracticalTest01SecondaryActivity : AppCompatActivity() {
    private lateinit var resultEditText: EditText
    private val buttonClickListener = ButtonClickListener()

    private inner class ButtonClickListener: View.OnClickListener{
        override fun onClick(view:View) {
            when (view.id) {
               R.id.ok_button -> {
                   setResult(RESULT_OK)
               }

               R.id.cancel_button->{
                   setResult(RESULT_CANCELED)
               }
            }
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test01_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        resultEditText = findViewById(R.id.sum_edit_text)
        val sum = intent.getIntExtra(Constants.RESULT_KEY, 0)
        resultEditText.setText(sum.toString())
        findViewById<Button>(R.id.ok_button).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.cancel_button).setOnClickListener(buttonClickListener)
    }
}