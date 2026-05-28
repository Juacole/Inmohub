package com.inmohub.frontend.features.property.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inmohub.frontend.core.components.InmoButton
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.features.property.data.PropertyRepository
import kotlinx.coroutines.launch

@Composable
fun FsboUploadTab() {
    val scope = rememberCoroutineScope()

    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileBytes by remember { mutableStateOf<ByteArray?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadSuccess by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Carga Masiva de Propiedades",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBluePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            "Sube un archivo CSV con tus propiedades para ingestar múltiples inmuebles de una sola vez.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        InmoButton(
            text = if (selectedFileName != null) "Archivo: ${selectedFileName!!}" else "Seleccionar archivo CSV",
            onClick = {
                scope.launch {
                    val result = pickCsvFile()
                    if (result != null) {
                        selectedFileName = result.second
                        selectedFileBytes = result.first
                        uploadSuccess = false
                        uploadError = null
                    }
                }
            },
            isSecondary = true
        )

        if (selectedFileName != null && selectedFileBytes != null) {
            InmoButton(
                text = if (isUploading) "PROCESANDO..." else "Subir e Ingstar",
                onClick = {
                    scope.launch {
                        isUploading = true
                        uploadSuccess = false
                        uploadError = null
                        val success = PropertyRepository.uploadCsvFile(selectedFileBytes!!)
                        if (success) {
                            uploadSuccess = true
                            selectedFileName = null
                            selectedFileBytes = null
                        } else {
                            uploadError = "Error al procesar el archivo CSV. Verifica el formato e inténtalo de nuevo."
                        }
                        isUploading = false
                    }
                },
                enabled = !isUploading
            )
        }

        if (isUploading) {
            CircularProgressIndicator(color = TileOrangeSecondary)
        }

        if (uploadSuccess) {
            Text(
                "\u2705 Archivo CSV procesado correctamente. Las propiedades se están ingiriendo.",
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (uploadError != null) {
            Text(
                uploadError!!,
                color = Color(0xFFF44336),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Formato del archivo CSV",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NavyBluePrimary
                )
                Text(
                    "El archivo debe tener la cabecera exacta y los datos separados por comas. " +
                            "Las características se especifican como \"Nombre:Valor\" separadas por punto y coma.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Ejemplo:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NavyBluePrimary
                )
                Text(
                    "Title,Description,Price,AreaM2,Address,City,State,Country,Features",
                    fontSize = 11.sp,
                    color = Color(0xFF546E7A),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                Text(
                    "\"Chalet de lujo\",\"Hermosa casa con piscina\",450000.00,250.5,\"Calle Mayor 123\"," +
                            "Madrid,Madrid,España,\"Habitaciones:4;Baños:3;Piscina:Sí\"",
                    fontSize = 11.sp,
                    color = Color(0xFF78909C),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                Text(
                    "\"Apartamento céntrico\",\"Renovado, vistas al mar\",180000.00,75.0,\"Av. del Mar 45\"," +
                            "Barcelona,Cataluña,España,\"Habitaciones:2;Baños:1\"",
                    fontSize = 11.sp,
                    color = Color(0xFF78909C),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}
