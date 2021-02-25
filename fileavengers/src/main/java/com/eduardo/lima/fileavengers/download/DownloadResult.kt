package com.eduardo.lima.fileavengers.download

sealed class DownloadResult {
    object Success : DownloadResult()

    data class Error(val message: String, val exception: Exception? = null) : DownloadResult()

    data class Progress(val progress: Int): DownloadResult()
}
