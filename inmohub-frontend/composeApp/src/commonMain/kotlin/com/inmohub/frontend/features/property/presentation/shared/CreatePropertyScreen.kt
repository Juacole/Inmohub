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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.features.property.data.PropertyRepository
import kotlinx.coroutines.launch

class CreatePropertyScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var areaM2 by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var province by remember { mutableStateOf("") }
        var country by remember { mutableStateOf("España") }
        var featuresText by remember { mutableStateOf("") }

        var selectedImages by remember { mutableStateOf<List<Pair<ByteArray, String>>>(emptyList()) }
        var selectedImageCount by remember { mutableStateOf(0) }

        var isPublishing by remember { mutableStateOf(false) }
        var publishSuccess by remember { mutableStateOf(false) }
        var publishError by remember { mutableStateOf<String?>(null) }

        val canPublish = title.isNotBlank() && description.isNotBlank() &&
                (price.toDoubleOrNull() ?: 0.0) > 0 &&
                (areaM2.toDoubleOrNull() ?: 0.0) > 0 &&
                address.isNotBlank() && city.isNotBlank() && !isPublishing

        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
            TopAppBar(
                title = { Text("Publicar Propiedad", fontWeight = FontWeight.Bold, color = NavyBluePrimary) },
                navigationIcon = {
                    TextButton(onClick = { navigator.pop() }) {
                        Text("Volver", color = NavyBluePrimary, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )

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

                        InmoInput(value = title, onValueChange = { title = it }, label = "Título *")
                        InmoInput(value = description, onValueChange = { description = it }, label = "Descripción *")
                        InmoInput(value = price, onValueChange = { price = it }, label = "Precio (€) *")
                        InmoInput(value = areaM2, onValueChange = { areaM2 = it }, label = "Superficie (m²) *")
                        InmoInput(value = address, onValueChange = { address = it }, label = "Dirección *")
                        InmoInput(value = city, onValueChange = { city = it }, label = "Ciudad *")
                        InmoInput(value = province, onValueChange = { province = it }, label = "Provincia")
                        InmoInput(value = country, onValueChange = { country = it }, label = "País")
                        InmoInput(value = featuresText, onValueChange = { featuresText = it }, label = "Características (Hab:4;Baños:3;Piscina:Sí)")
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
                            "Imágenes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = NavyBluePrimary
                        )

                        if (selectedImages.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                selectedImages.take(5).forEach { (bytes, _) ->
                                    AsyncImage(
                                        model = bytes,
                                        contentDescription = "Imagen seleccionada",
                                        modifier = Modifier.size(80.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                if (selectedImages.size > 5) {
                                    Text("+${selectedImages.size - 5}", fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }

                        InmoButton(
                            text = if (selectedImages.isNotEmpty()) "${selectedImages.size} imágenes seleccionadas" else "Seleccionar Imágenes",
                            onClick = {
                                scope.launch {
                                    val images = pickImageFiles()
                                    if (images.isNotEmpty()) {
                                        selectedImages = images
                                    }
                                }
                            },
                            isSecondary = true,
                            enabled = !isPublishing
                        )
                    }
                }

                if (publishSuccess) {
                    Text(
                        "Propiedad publicada correctamente",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (publishError != null) {
                    Text(
                        publishError!!,
                        color = ErrorRed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                InmoButton(
                    text = if (isPublishing) "PUBLICANDO..." else "Publicar Propiedad",
                    onClick = {
                        scope.launch {
                            isPublishing = true
                            publishError = null
                            publishSuccess = false

                            val featuresList = featuresText
                                .split(";")
                                .filter { it.contains(":") }
                                .map {
                                    val parts = it.split(":", limit = 2)
                                    com.inmohub.frontend.features.property.dtos.PropertyFeatureDto(
                                        featureName = parts[0].trim(),
                                        featureValue = parts[1].trim()
                                    )
                                }

                            val request = com.inmohub.frontend.features.property.dtos.CreateProperty(
                                titulo = title,
                                descripcion = description,
                                precio = price.toDoubleOrNull() ?: 0.0,
                                area = areaM2.toDoubleOrNull() ?: 0.0,
                                direccion = address,
                                ciudad = city,
                                provincia = province,
                                pais = country,
                                caracteristicas = featuresList
                            )

                            val result = PropertyRepository.createPropertyWithImages(
                                datos = request,
                                imageBytes = selectedImages.map { it.first }
                            )
                            if (result != null) {
                                publishSuccess = true
                            } else {
                                publishError = "Error al publicar la propiedad. Verifica los datos."
                            }
                            isPublishing = false
                        }
                    },
                    enabled = canPublish
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
