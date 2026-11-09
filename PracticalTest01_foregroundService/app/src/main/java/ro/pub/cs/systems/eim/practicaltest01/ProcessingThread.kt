package ro.pub.cs.systems.eim.practicaltest01

import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Date
import kotlin.math.sqrt
import kotlin.random.Random

class ProcessingThread(
    private val context: Context,
    firstNumber: Int,
    secondNumber: Int
) : Thread() {

    private val arithmeticMean = (firstNumber + secondNumber)/ 2
    private val geometricMean = sqrt(firstNumber.toDouble() * secondNumber)

    @Volatile
    private var isRunning = true

    override fun run() {
        Log.d(Constants.TAG, "Thread started")
        while (isRunning) {
            sendMessage(Random.nextInt(3))
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

    private fun sendMessage(messageType: Int) {
        val intent = Intent()
        val date = Date()

        when (messageType) {
            Constants.MESSAGE_ARITHMETIC_MEAN -> {
                intent.action = Constants.ACTION_ARITHMETIC_MEAN
            }

            Constants.MESSAGE_GEOMETRIC_MEAN-> {
                intent.action = Constants.ACTION_GEOMETRIC_MEAN
            }

            Constants.MESSAGE_BOTH_MEANS ->{
                intent.action = Constants.ACTION_BOTH_MEANS
            }

        }
        intent.putExtra(
            Constants.DATA,
            "$date | arithmetic mean: $arithmeticMean | geometric mean: $geometricMean"
        )
        // Set the target package for cross-app broadcasts
        intent.setPackage(context.packageName)
        context.sendBroadcast(intent)
        Log.d(Constants.TAG, "Broadcast sent: ${intent.action}")
    }

    fun stopThread() {
        isRunning = false
        interrupt()
    }
}
