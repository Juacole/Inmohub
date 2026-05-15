package com.inmohub.frontend.features.property.presentation.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.inmohub.frontend.features.property.data.PropertyRepository
import com.inmohub.frontend.features.property.dtos.PropertyDto
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import kotlinx.coroutines.launch

data class PropertyDetailScreen(val propertyId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        var property by remember { mutableStateOf<PropertyDto?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(propertyId) {
            coroutineScope.launch {
                isLoading = true
                property = PropertyRepository.getPropertyById(propertyId)
                isLoading = false
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalle", color = NavyBluePrimary) },
                    navigationIcon = {
                        TextButton(onClick = { navigator.pop() }) {
                            Text(
                                text = "Volver",
                                color = NavyBluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TileOrangeSecondary)
                }
            } else if (property != null) {
                val currentProperty = property!!
                LazyColumn( // Carrousel de fotos
                    modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF5F5F5))
                ) {
                    item {
                        if (currentProperty.photos.isNotEmpty()) {
                            val state = rememberPagerState(pageCount = { currentProperty.photos.size })
                            HorizontalPager(
                                state = state,
                                modifier = Modifier.fillMaxWidth().height(250.dp)
                            ) { page ->
                                AsyncImage(
                                    model = currentProperty.photos[page],
                                    contentDescription = "Imagen $page",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(250.dp).background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Sin imágenes", color = Color.DarkGray)
                            }
                        }
                    }

                    // Info principal
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${currentProperty.price} €",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyBluePrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentProperty.title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentProperty.address}, ${currentProperty.city}",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Features
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Características", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyBluePrimary)
                                Spacer(modifier = Modifier.height(12.dp))

                                @OptIn(ExperimentalLayoutApi::class)
                                FlowRow( // Contenedor inteligente
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    FeatureItem("Superficie", "${currentProperty.areaM2} m²")
                                    currentProperty.features.forEach { feature ->
                                        FeatureItem(feature.featureName, feature.featureValue)
                                    }
                                }
                            }
                        }
                    }

                    // Descripción
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyBluePrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentProperty.description,
                                fontSize = 16.sp,
                                color = Color.DarkGray,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Error al cargar la propiedad.", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun FeatureItem(label: String, value: String) {
    Column(
        modifier = Modifier.background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyBluePrimary)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}