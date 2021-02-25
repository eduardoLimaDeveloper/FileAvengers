package com.eduardo.lima.fileavengers.unzip

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class Unzip(private val listener: UnzipListener) {

    class Builder {
        fun build(listener: UnzipListener) = Unzip(listener = listener)
    }

    fun execute(path: String, fileName: String, unzipFolderName: String = UNZIP_FOLDER_NAME) {
        val inputStream: InputStream
        val zipInputStream: ZipInputStream
        var unzippedFolder = ""

        try {
            var filename: String
            inputStream = FileInputStream("$path/$fileName")
            zipInputStream = ZipInputStream(BufferedInputStream(inputStream))

            var zipEntry: ZipEntry
            val buffer = ByteArray(1024)
            var count: Int

            unzippedFolder = "$path/$unzipFolderName"
            val folder = File(unzippedFolder)

            if (folder.exists()) {
                folder.delete()
            }
            folder.mkdirs()

            while (zipInputStream.nextEntry.also {
                    if (it == null) {
                        listener.onSuccess(unzippedFolder = unzippedFolder)
                        return
                    }
                    zipEntry = it
                } != null) {
                filename = zipEntry.name

                if (zipEntry.isDirectory) {
                    val file = File("$path/$unzipFolderName/$filename")
                    file.mkdirs()
                    continue
                }
                val fileOutputStream = FileOutputStream("$path/$unzipFolderName/$filename")
                while (zipInputStream.read(buffer).also { count = it } != -1) {
                    fileOutputStream.write(buffer, 0, count)
                }
                fileOutputStream.close()
                zipInputStream.closeEntry()
            }
            zipInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            listener.onError(exception = e)
        }
        listener.onSuccess(unzippedFolder = unzippedFolder)
    }

    companion object {
        private const val UNZIP_FOLDER_NAME = "fileAvengersUnzipFolder"
    }
}