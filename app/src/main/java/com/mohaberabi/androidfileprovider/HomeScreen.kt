package com.mohaberabi.androidfileprovider

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File


@Composable
fun HomeScreen(modifier: Modifier = Modifier) {


    val context = LocalContext.current
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) {

                perms ->

        }

    var tempImageUri = remember {
        mutableStateOf<Uri?>(null)
    }
    var imgUri = remember {
        mutableStateOf<Uri?>(null)
    }

    var imageFile = remember {
        mutableStateOf<File?>(null)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
        ) { taken ->
            if (taken) {
                imgUri = tempImageUri
            }
        }

    LaunchedEffect(key1 = Unit) {

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        )
    }
    Scaffold {

            padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {


            Button(
                onClick = {
                    val image = "capture_${System.currentTimeMillis()}"
                    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val file = File.createTempFile(image, ".jpg", storageDir)
                    imageFile.value = file
                    val uri =
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                    tempImageUri.value = uri
                    cameraLauncher.launch(uri)
                },
            ) {
                Text(text = "Take Image")
            }


            Button(
                onClick = {
                    imageFile.value?.let {
                        val uri =
                            FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                it
                            )

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        context.startActivity(intent)

                    }


                },
            ) {
                Text(text = "Share Image")
            }

        }
    }
}