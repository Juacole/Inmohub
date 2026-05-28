package com.inmohub.frontend.features.property.presentation.shared

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun pickCsvFile(): Pair<ByteArray, String>? {
    return withContext(Dispatchers.IO) {
        val chooser = JFileChooser().apply {
            dialogTitle = "Seleccionar archivo CSV"
            fileFilter = FileNameExtensionFilter("Archivos CSV (*.csv)", "csv")
            fileSelectionMode = JFileChooser.FILES_ONLY
        }
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile
            file.readBytes() to file.name
        } else {
            null
        }
    }
}

actual suspend fun pickImageFiles(): List<Pair<ByteArray, String>> {
    return withContext(Dispatchers.IO) {
        val chooser = JFileChooser().apply {
            dialogTitle = "Seleccionar imagenes"
            fileFilter = FileNameExtensionFilter("Imagenes (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png")
            fileSelectionMode = JFileChooser.FILES_ONLY
            isMultiSelectionEnabled = true
        }
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFiles.map { file ->
                file.readBytes() to file.name
            }
        } else {
            emptyList()
        }
    }
}
