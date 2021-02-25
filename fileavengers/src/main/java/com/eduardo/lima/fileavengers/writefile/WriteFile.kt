/*
 *   Copyright 2021 Eduardo Lima
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.eduardo.lima.fileavengers.writefile

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter


class WriteFile(private val listener: WriteFileListener) {

    class Builder {
        fun build(listener: WriteFileListener) =
            WriteFile(listener = listener)
    }

    fun execute(filePath: String, fileName: String, content: String, deleteIfExist: Boolean = false) {
        val file = File(filePath, fileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (exception: Exception) {
                listener.onFailed(exception)
            }
        } else if(file.exists() && deleteIfExist) {
            file.delete()
            file.createNewFile()
        }

        try {
            val fileWriter = FileWriter(file, true)
            val bufferedWriter = BufferedWriter(fileWriter)

            bufferedWriter.append(content)
            bufferedWriter.newLine()
            bufferedWriter.close()
            listener.onSuccess()
        } catch (exception: java.lang.Exception) {
            listener.onFailed(exception)
        }
    }
}