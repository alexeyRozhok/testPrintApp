package com.example.printingapp.service

import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import com.example.printingapp.utils.logDebug
import java.io.FileOutputStream
import java.nio.file.Path

class PdfDocumentAdapter(private val filePath: Path) : PrintDocumentAdapter() {
    override fun onStart() {
        logDebug("pdf print start")
    }

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
        } else {
            val docInfo: PrintDocumentInfo = PrintDocumentInfo
                .Builder("test name")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build()

            callback?.onLayoutFinished(docInfo, false)
        }
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        val file = filePath.toFile()

        val fileInputStr = file.inputStream()
        val outputStream = FileOutputStream(destination?.fileDescriptor)

        fileInputStr.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        callback?.onWriteFinished(
            arrayOf(PageRange.ALL_PAGES)
        )
    }
}