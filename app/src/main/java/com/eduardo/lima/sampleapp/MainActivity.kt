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

package com.eduardo.lima.sampleapp

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.eduardo.lima.fileavengers.download.DownloadFile
import com.eduardo.lima.fileavengers.download.DownloadListener
import com.eduardo.lima.fileavengers.readfile.ReadFile
import com.eduardo.lima.fileavengers.readfile.ReadFileListener
import com.eduardo.lima.fileavengers.unzip.Unzip
import com.eduardo.lima.fileavengers.unzip.UnzipListener
import com.eduardo.lima.fileavengers.writefile.WriteFile
import com.eduardo.lima.fileavengers.writefile.WriteFileListener
import com.eduardo.lima.sampleapp.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.io.File


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val writeFileBt: MaterialButton
        get() = binding.writeFileBt
    private val readFileBt: MaterialButton
        get() = binding.readFileBt
    private val unzipFileBt: MaterialButton
        get() = binding.unzipFileBt
    private val downloadFileBt: MaterialButton
        get() = binding.downloadFileBt

    private val appFolder by lazy {
        getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    }

    private val writeFile by lazy { WriteFile.Builder().build(writeFileListener) }
    private val readFile by lazy { ReadFile.Builder().build(readFileListener) }
    private val unzip by lazy { Unzip.Builder().build(unzipListener) }
    private val downloadFile by lazy { DownloadFile.Builder().build(downloadListener) }

    private val readFileListener = object : ReadFileListener() {
        override fun onSuccess(fileContent: String) {
            Snackbar.make(binding.root, fileContent, Snackbar.LENGTH_LONG).show()
        }

        override fun onFailed(exception: Exception) {
            Snackbar.make(binding.root, exception.toString(), Snackbar.LENGTH_LONG).show()
        }

        override fun onFileNotAvailable() {
            Snackbar.make(
                binding.root,
                R.string.file_not_available, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private val writeFileListener = object : WriteFileListener() {
        override fun onSuccess() {
            Snackbar.make(
                binding.root,
                R.string.file_created, Snackbar.LENGTH_LONG
            ).show()
        }

        override fun onFailed(exception: Exception) {
            Snackbar.make(binding.root, exception.toString(), Snackbar.LENGTH_LONG).show()
        }
    }

    private val unzipListener = object : UnzipListener() {
        override fun onSuccess(unzippedFolder: String) {
            Snackbar.make(
                binding.root,
                R.string.unzip_success, Snackbar.LENGTH_LONG
            ).show()
        }

        override fun onError(exception: Exception) {
            Snackbar.make(binding.root, exception.toString(), Snackbar.LENGTH_LONG).show()
        }
    }

    private val downloadListener = object : DownloadListener() {
        override fun onSuccess(folder: File?, fileName: String) {
            Snackbar.make(
                binding.root,
                R.string.download_success, Snackbar.LENGTH_LONG
            ).show()
        }

        override fun onError(message: String, exception: Exception?) {
            Snackbar.make(binding.root, exception.toString(), Snackbar.LENGTH_LONG).show()
        }

        override fun onProgress(progress: Int) {
            Log.d("FileAvengersDummy", "progress: $progress")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestPermission()
        prepareView()
    }

    private fun prepareView() {
        writeFileBt.setOnClickListener {
            writeDemoFile()
        }

        readFileBt.setOnClickListener {
            readDemoFile()
        }

        downloadFileBt.setOnClickListener {
            downloadDemoFile()
        }

        unzipFileBt.setOnClickListener {
            unzipDemoFile()
        }
    }

    private fun writeDemoFile() {
        val demoFileContent = "This is a demo file content data"
        writeFile.execute(
            appFolder?.absolutePath.orEmpty(),
            DEMO_FILE, demoFileContent
        )
    }

    private fun readDemoFile() {
        readFile.execute(
            appFolder?.absolutePath.orEmpty(),
            DEMO_FILE
        )
    }

    private fun downloadDemoFile() {
        downloadFile.execute(
            context = this,
            fileUrl = DUMMY_FILE_TO_DOWNLOAD,
            authority = "${BuildConfig.APPLICATION_ID}.provider"
        )
    }

    private fun unzipDemoFile() {
        unzip.execute(
            appFolder?.absolutePath.orEmpty(),
            UNZIP_FILE
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            EXTERNAL_STORAGE,
            PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val DEMO_FILE = "demoFile.txt"
        private const val UNZIP_FILE = "demo.zip"
        private const val DUMMY_FILE_TO_DOWNLOAD = "http://ipv4.download.thinkbroadband.com/100MB.zip"

        private const val PERMISSION_REQUEST_CODE = 200
        private val EXTERNAL_STORAGE = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}