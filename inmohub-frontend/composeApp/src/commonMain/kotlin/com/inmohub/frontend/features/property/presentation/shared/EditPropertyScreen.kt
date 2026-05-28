package com.inmohub.frontend.features.property.presentation.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.inmohub.frontend.core.components.InmoButton
import com.inmohub.frontend.core.components.InmoInput
import com.inmohub.frontend.core.themes.ErrorRed
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TextLightGray
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.features.property.data.PropertyRepository
import com.inmohub.frontend.features.property.dtos.PropertyDto
import com.inmohub.frontend.features.property.dtos.PropertyPatchRequest
import kotlinx.coroutines.launch

data class EditPropertyScreen(val propertyId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var property by remember { mutableStateOf<PropertyDto?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var areaM2 by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var status by remember { mutableStateOf("AVAILABLE") }

        var isSaving by remember { mutableStateOf(false) }
        var saveError by remember { mutableStateOf<String?>(null) }
        var saveSuccess by remember { mutableStateOf(false) }

        var isUploadingImages by remember { mutableStateOf(false) }
        var imageUploadSuccess by remember { mutableStateOf(false) }
        var imageUploadError by remember { mutableStateOf<String?>(null) }

        var statusDropdownExpanded by remember { mutableStateOf(false) }
        val statusOptions = listOf("AVAILABLE", "SOLD", "RENTED", "OFF_MARKET")

        LaunchedEffect(propertyId) {
            property = PropertyRepository.getPropertyById(propertyId)
            property?.let { p ->
                title = p.title
                description = p.description
                price = p.price.toString()
                areaM2 = p.areaM2.toString()
                address = p.address
                city = p.city
                status = p.status
            }
            isLoading = false
        }

        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
            TopAppBar(
                title = { Text("Editar Propiedad", fontWeight = FontWeight.Bold, color = NavyBluePrimary) },
                navigationIcon = {
                    TextButton(onClick = { navigator.pop() }) {
                        Text("Volver", color = NavyBluePrimary, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TileOrangeSecondary)
                }
            } else if (property != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Datos de la Propiedad",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = NavyBluePrimary
                            )

                            InmoInput(value = title, onValueChange = { title = it }, label = "Título")
                            InmoInput(value = description, onValueChange = { description = it }, label = "Descripción")
                            InmoInput(value = price, onValueChange = { price = it }, label = "Precio (€)")
                            InmoInput(value = areaM2, onValueChange = { areaM2 = it }, label = "Superficie (m²)")
                            InmoInput(value = address, onValueChange = { address = it }, label = "Dirección")
                            InmoInput(value = city, onValueChange = { city = it }, label = "Ciudad")

                            Text("Estado", fontSize = 14.sp, color = Color.Gray)
                            ExposedDropdownMenuBox(
                                expanded = statusDropdownExpanded,
                                onExpandedChange = { statusDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = status,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NavyBluePrimary,
                                        unfocusedBorderColor = TextLightGray,
                                        focusedLabelColor = NavyBluePrimary
                                    ),
                                    singleLine = true
                                )
                                ExposedDropdownMenu(
                                    expanded = statusDropdownExpanded,
                                    onDismissRequest = { statusDropdownExpanded = false }
                                ) {
                                    statusOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                status = option
                                                statusDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            if (saveSuccess) {
                                Text("Propiedad actualizada correctamente", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                            }
                            if (saveError != null) {
                                Text(saveError!!, color = ErrorRed)
                            }

                            InmoButton(
                                text = if (isSaving) "GUARDANDO..." else "Guardar Cambios",
                                onClick = {
                                    scope.launch {
                                        isSaving = true
                                        saveError = null
                                        saveSuccess = false
                                        val request = PropertyPatchRequest(
                                            title = title.takeIf { it != property?.title },
                                            description = description.takeIf { it != property?.description },
                                            price = price.toDoubleOrNull()?.takeIf { it.toString() != property?.price.toString() },
                                            areaM2 = areaM2.toDoubleOrNull()?.takeIf { it.toString() != property?.areaM2.toString() },
                                            address = address.takeIf { it != property?.address },
                                            city = city.takeIf { it != property?.city },
                                            status = status.takeIf { it != property?.status }
                                        )
                                        val updated = PropertyRepository.updateProperty(propertyId, request)
                                        if (updated != null) {
                                            property = updated
                                            saveSuccess = true
                                        } else {
                                            saveError = "Error al guardar los cambios"
                                        }
                                        isSaving = false
                                    }
                                },
                                enabled = !isSaving
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Imágenes de la Propiedad",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = NavyBluePrimary
                            )

                            val photos = property!!.photos
                            if (photos.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    photos.take(5).forEach { photo ->
                                        AsyncImage(
                                            model = photo.photoUrl,
                                            contentDescription = "Foto",
                                            modifier = Modifier.size(80.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    if (photos.size > 5) {
                                        Text("+${photos.size - 5}", fontSize = 14.sp, color = Color.Gray)
                                    }
                                }
                            } else {
                                Text("No hay imágenes", fontSize = 14.sp, color = Color.Gray)
                            }

                            if (imageUploadSuccess) {
                                Text("Imágenes subidas correctamente", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                            }
                            if (imageUploadError != null) {
                                Text(imageUploadError!!, color = ErrorRed)
                            }

                            InmoButton(
                                text = if (isUploadingImages) "SUBIENDO..." else "Añadir Imágenes",
                                onClick = {
                                    scope.launch {
                                        isUploadingImages = true
                                        imageUploadSuccess = false
                                        imageUploadError = null
                                        val images = pickImageFiles()
                                        if (images.isNotEmpty()) {
                                            val uploaded = PropertyRepository.uploadPropertyImages(
                                                propertyId = propertyId,
                                                imageBytes = images.map { it.first }
                                            )
                                            if (uploaded != null) {
                                                property = PropertyRepository.getPropertyById(propertyId)
                                                imageUploadSuccess = true
                                            } else {
                                                imageUploadError = "Error al subir las imágenes"
                                            }
                                        }
                                        isUploadingImages = false
                                    }
                                },
                                enabled = !isUploadingImages,
                                isSecondary = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se pudo cargar la propiedad", color = Color.Red)
                }
            }
        }
    }
}
