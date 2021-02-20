package com.appsmiths.lima.fileavengers.writefile

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException


class WriteFile {

    class Builder {
        fun build() = WriteFile()
    }

    fun execute(filePath: String, fileName: String, content: String) {
        val file = File(filePath, fileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val fileWriter = FileWriter(file, true)
        val bufferedWriter = BufferedWriter(fileWriter)

        bufferedWriter.append(content)
        bufferedWriter.newLine()
        bufferedWriter.close()
    }
}