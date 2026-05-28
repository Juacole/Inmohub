package com.inmohub.frontend.features.property.presentation.shared

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

var mainActivity: ComponentActivity? = null

actual suspend fun pickCsvFile(): Pair<ByteArray, String>? {
    val activity = mainActivity ?: return null
    return withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val key = "csv_picker_${System.currentTimeMillis()}"
            val launcher = activity.activityResultRegistry.register(
                key,
                ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                if (uri != null) {
                    try {
                        val inputStream = activity.contentResolver.openInputStream(uri)
                        val bytes = inputStream?.readBytes()
                        val fileName = uri.lastPathSegment ?: "document.csv"
                        continuation.resume(bytes?.let { it to fileName })
                    } catch (e: Exception) {
                        continuation.resume(null)
                    }
                } else {
                    continuation.resume(null)
                }
            }
            launcher.launch(arrayOf("text/csv", "text/comma-separated-values", "text/plain", "*/*"))
        }
    }
}

actual suspend fun pickImageFiles(): List<Pair<ByteArray, String>> {
    val activity = mainActivity ?: return emptyList()
    return withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val key = "img_picker_${System.currentTimeMillis()}"
            val launcher = activity.activityResultRegistry.register(
                key,
                ActivityResultContracts.GetMultipleContents()
            ) { uris: List<Uri> ->
                val results = uris.mapNotNull { uri ->
                    try {
                        val inputStream = activity.contentResolver.openInputStream(uri)
                        val bytes = inputStream?.readBytes()
                        val fileName = uri.lastPathSegment ?: "image.jpg"
                        bytes?.let { it to fileName }
                    } catch (e: Exception) {
                        null
                    }
                }
                continuation.resume(results)
            }
            launcher.launch("image/*")
        }
    }
}
