package ro.pub.cs.systems.eim.practicaltest01



import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.util.Log
import java.util.Date
import kotlin.math.sqrt
import kotlin.random.Random

class ProcessingThread(
    private val context: Context,
    private val firstNumber: Int,
    private val secondNumber: Int
) : Thread() {

    @Volatile
    private var isRunning = false;

    override fun run() {
        while (true) {
            sendMessage()
            sleepThread()

        }
    }


    private fun sleepThread() {
        try {
            sleep(Constants.SLEEP_TIME)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun sendMessage() {
        val media_aritmetica = (firstNumber + secondNumber) / 2
        val media_geometrica = sqrt(firstNumber.toDouble() * secondNumber)
        val intent = Intent()

        intent.action= when(Random.nextInt(3)){
            0->Constants.ACTION_1
            1-> Constants.ACTION_2
            else -> Constants.ACTION_3
        }
        intent.putExtra(Constants.DATA_KEY,"${Date()} | $media_aritmetica | $media_geometrica")
        // Set the target package for cross-app broadcasts
        intent.setPackage(context.packageName)
        context.sendBroadcast(intent)
        Log.d(Constants.TAG, "Broadcast sent: ${intent.action}")
    }

    fun stopThread(){
        isRunning = false
        interrupt()
    }
}