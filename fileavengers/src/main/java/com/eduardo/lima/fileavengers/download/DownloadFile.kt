package com.eduardo.lima.fileavengers.download

import android.content.Context
import android.os.Environment
import androidx.core.content.FileProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.io.File
import java.io.OutputStream

class DownloadFile(private val listener: DownloadListener) {

    private var outputStream: OutputStream? = null
    private val coroutineScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.IO) }
    private val ktor: HttpClient by lazy { HttpClient(Android) }

    class Builder {
        fun build(listener: DownloadListener) = DownloadFile(listener = listener)
    }

    fun execute(
        context: Context,
        fileUrl: String,
        fileName: String = FILE_DOWNLOAD_NAME,
        authority: String
    ) {
        val folder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val file = File(folder, fileName)
        val uri = context.let {
            FileProvider.getUriForFile(it, authority, file)
        }

        outputStream = context.contentResolver.openOutputStream(uri)

        outputStream?.let { stream ->
            coroutineScope.launch {
                ktor.downloadFile(stream, fileUrl).collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is DownloadResult.Success -> {
                                listener.onSuccess(folder, fileName)
                            }

                            is DownloadResult.Error -> {
                                listener.onError(it.message, it.exception)
                            }

                            is DownloadResult.Progress -> {
                                listener.onProgress(it.progress)
                            }
                        }
                    }
                }
            }
        }
    }

    fun destroy() {
        coroutineScope.cancel()
        ktor.close()
        outputStream?.close()
    }

    companion object {
        private const val FILE_DOWNLOAD_NAME = "fileAvengersDownloadName"
    }
}