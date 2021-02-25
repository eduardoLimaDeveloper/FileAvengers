package com.eduardo.lima.fileavengers.unzip


abstract class UnzipListener {
    open fun onSuccess(unzippedFolder: String) {
        // Implementation done where this class is extended
    }

    open fun onError(exception: Exception) {
        // Implementation done where this class is extended
    }
}