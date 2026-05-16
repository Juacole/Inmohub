package com.inmohub.frontend.features.property.presentation.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.inmohub.frontend.features.property.data.PropertyRepository
import com.inmohub.frontend.features.property.dtos.PropertySummaryDto
import com.inmohub.frontend.features.property.presentation.shared.PropertyCard
import com.inmohub.frontend.features.auth.presentation.LoginScreen
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import kotlinx.coroutines.launch

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()

        var properties by remember { mutableStateOf<List<PropertySummaryDto>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                isLoading = true
                // Se solicita la primera página del endpoint
                val result = PropertyRepository.getPropertySummary(page = 0, size = 20)
                properties = result?.content ?: emptyList()
                isLoading = false
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("InmoHub", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                    },
                    actions = {
                        TextButton(onClick = { navigator.push(LoginScreen()) }) {
                            Text(
                                "Acceder",
                                color = TileOrangeSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Encuentra tu próximo hogar",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBluePrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Explora nuestro catálogo de propiedades exclusivas",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TileOrangeSecondary)
                    }
                } else if (properties.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay propiedades disponibles en este momento.",
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(properties) { property ->
                            PropertyCard(
                                property = property,
                                onClick = { // Con un click entramos a los detalles del inmueble
                                    navigator.push(PropertyDetailScreen(property.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}