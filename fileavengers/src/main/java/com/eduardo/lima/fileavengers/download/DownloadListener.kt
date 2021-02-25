package com.eduardo.lima.fileavengers.download

import java.io.File


abstract class DownloadListener {
    open fun onSuccess(folder: File?, fileName: String) {
        // Implementation done where this class is extended
    }

    open fun onError(message: String, exception: Exception?) {
        // Implementation done where this class is extended
    }

    open fun onProgress(progress: Int) {
        // Implementation done where this class is extended
    }
}