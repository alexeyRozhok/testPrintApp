package com.example.printingapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintAttributes.MediaSize
import android.print.PrintManager
import android.printservice.PrintJob
import android.printservice.PrintService
import android.printservice.PrinterDiscoverySession
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.printingapp.R
import com.example.printingapp.utils.logDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

//Not in work
class PrinterService: PrintService() {
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val CHANNEL_ID = "printing_notification"
    }

    private val printManager: PrintManager by lazy {
        this.getSystemService(PrintManager::class.java)
    }


    override fun onCreatePrinterDiscoverySession(): PrinterDiscoverySession? {
        TODO("Not yet implemented")
    }

    override fun onRequestCancelPrintJob(printJob: PrintJob?) {
        TODO("Not yet implemented")
    }

    override fun onPrintJobQueued(printJob: PrintJob?) {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra("START", false) == true) {
            (intent.getParcelableExtra<Uri>("filePath"))
                ?.let {
                    start(it)
                }
        } else {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun start(uri: Uri) {
        val notification: Notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Printing")
            .setContentText(
                "Printing in process"
            )
            .build()

        createNotificationChannel()

        startForeground(11, notification)

        coroutineScope.launch {

//            this@PrinterService
//                .contentResolver
//                .openInputStream(uri)
//                ?.bufferedReader()
//                ?.use {
//                    it.readText().also {
//                        logDebug(it, "printer")
//                    }
//                }

            stopSelf()
        }
    }

    override fun onDestroy() {
        logDebug("printer")
        coroutineScope.cancel()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, "Print Service", importance)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(mChannel)
    }

    private fun startPrint() {

    }
}