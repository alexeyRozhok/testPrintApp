package com.example.printingapp.ui.screens.main

import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintAttributes.MediaSize
import android.print.PrintManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.printingapp.service.PdfDocumentAdapter
import com.example.printingapp.service.PrinterService
import com.example.printingapp.ui.theme.AppPreview
import com.example.printingapp.ui.theme.PrintingAPpTheme
import com.example.printingapp.utils.logDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createTempFile
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import kotlin.io.path.readBytes
import kotlin.io.path.readText

@Composable
fun MainScreen() {
    val viewModel = viewModel<MainViewModel>()
    when (viewModel.uiState) {
        is UiState.PdfChoosing -> PdfChoosing(viewModel::onUriPicked)
        is UiState.PdfChosen -> ChosenPdf((viewModel.uiState as UiState.PdfChosen))
    }
}

@Composable
private fun PdfChoosing(
    onUriPicked: (Uri?) -> Unit
) {
    val choosePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = onUriPicked
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                choosePdfLauncher.launch(
                    arrayOf("application/pdf")
                )
            }
        ) {
            Text(text = "Choose PDF")
        }
    }
}

@Composable
private fun ChosenPdf(
    uiState: UiState.PdfChosen,
) {
    val localContext = LocalContext.current
    val manager: PrintManager = remember {
        localContext.getSystemService(PrintManager::class.java)
    }
    val coroutineScope = rememberCoroutineScope()
    val contextResolver = LocalContext.current.contentResolver

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "chosen ${uiState.uri}")
        Button(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(Color.Gray)
                .padding(20.dp),
            onClick = {
                coroutineScope.launch {
                    val filePath = withContext(Dispatchers.IO) {
                        createTempFile("temp", "test").also { tempFile ->
                            contextResolver
                                .openInputStream(uiState.uri)
                                .use { input ->
                                    tempFile.outputStream()
                                        .use { output ->
                                            input?.copyTo(output)
                                        }
                                }
                        }
                    }
                    val pdfDocumentAdapter = PdfDocumentAdapter(filePath)

                    manager.print(
                        "Document ${uiState.uri.lastPathSegment}",
                        pdfDocumentAdapter,
                        PrintAttributes
                            .Builder()
                            .setMediaSize(MediaSize.ISO_A4)
                            .build()
                    )
                }
            }
        ) {
            Text(text = "Print")
        }
    }
}

@AppPreview
@Composable
private fun PdfChoosingPreview() {
    PrintingAPpTheme {
        PdfChoosing {}
    }
}

@AppPreview
@Composable
private fun ChosenPdsPreview() {
    PrintingAPpTheme {
        ChosenPdf(uiState = UiState.PdfChosen(Uri.parse("sdfsdf.pdf")))
    }
}