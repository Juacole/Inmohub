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
import com.inmohub.frontend.features.property.presentation.shared.FilterBottomSheet
import com.inmohub.frontend.features.property.presentation.shared.PropertyCard
import com.inmohub.frontend.features.auth.presentation.LoginScreen
import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.features.auth.presentation.shared.ProfileScreen
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()

        // Session Manager desde NetworkClient (singleton)
        val sessionManager = NetworkClient.sessionManager
        val hasSession by (sessionManager?.isSessionActive ?: flowOf(false)).collectAsState(initial = false)

        var properties by remember { mutableStateOf<List<PropertySummaryDto>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        // Estados de la paginación
        var currentPage by remember { mutableStateOf(0) }
        var canLoadMore by remember { mutableStateOf(true) }
        var isPaginating by remember { mutableStateOf(false) }

        // Estado para modal de filtros
        var showFilterSheet by remember { mutableStateOf(false) }

        // Estados para recordar los filtros aplicados
        var currentCity by remember { mutableStateOf<String?>(null) }
        var currentMinPrice by remember { mutableStateOf<Double?>(null) }
        var currentMaxPrice by remember { mutableStateOf<Double?>(null) }
        var currentStatus by remember { mutableStateOf<String?>(null) }
        var isFiltered by remember { mutableStateOf(false) }

        fun loadProperties(isNextPage: Boolean = false) {
            coroutineScope.launch {
                if (isNextPage) {
                    if (isPaginating || !canLoadMore) return@launch
                    isPaginating = true
                } else {
                    isLoading = true
                    currentPage = 0
                    canLoadMore = true
                }

                val pageToLoad = if (isNextPage) currentPage + 1 else 0

                val result = if (isFiltered) {
                    PropertyRepository.searchProperties(
                        page = pageToLoad,
                        size = 10,
                        city = currentCity,
                        minPrice = currentMinPrice,
                        maxPrice = currentMaxPrice,
                        status = currentStatus
                    )
                } else {
                    PropertyRepository.getPropertySummary(page = pageToLoad, size = 10)
                }

                if (result != null) {
                    if (isNextPage) {
                        properties = properties + result.content
                        currentPage = pageToLoad
                    } else {
                        properties = result.content
                    }

                    canLoadMore = result.number < result.totalPages - 1
                } else {
                    if (!isNextPage) properties = emptyList()
                    canLoadMore = false
                }

                isLoading = false
                isPaginating = false
            }
        }

        // Carga inicial al dibujarse la pantalla
        LaunchedEffect(Unit) {
            loadProperties()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TextButton(onClick = {
                            navigator.popUntilRoot()
                        }) {
                            Text("InmoHub", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                        }
                    },
                    actions = {
                        TextButton(onClick = { showFilterSheet = true }) {
                            Text("Filtros", color = NavyBluePrimary, fontWeight = FontWeight.Bold)
                        }
                        if (hasSession) {
                            TextButton(onClick = { navigator.push(ProfileScreen()) }) {
                                Text("Perfil", color = NavyBluePrimary, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = {
                                coroutineScope.launch {
                                    sessionManager?.clearSession()
                                    navigator.popUntilRoot()
                                }
                            }) {
                                Text("Salir", color = TileOrangeSecondary, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            TextButton(onClick = { navigator.push(LoginScreen()) }) {
                                Text("Acceder", color = TileOrangeSecondary, fontWeight = FontWeight.Bold)
                            }
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
                        text = if (isFiltered) "Mostrando resultados filtrados" else "Explora nuestro catálogo de propiedades exclusivas",
                        fontSize = 14.sp,
                        color = if (isFiltered) TileOrangeSecondary else Color.Gray,
                        fontWeight = if (isFiltered) FontWeight.Bold else FontWeight.Normal
                    )

                    if (isFiltered) {
                        TextButton(
                            onClick = {
                                currentCity = null
                                currentMinPrice = null
                                currentMaxPrice = null
                                currentStatus = null
                                isFiltered = false
                                loadProperties()
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Limpiar filtros ✖", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TileOrangeSecondary)
                    }
                } else if (properties.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No se encontraron propiedades.",
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
                                onClick = {
                                    navigator.push(PropertyDetailScreen(property.id))
                                }
                            )
                        }

                        if (canLoadMore) {
                            item {
                                LaunchedEffect(Unit) {
                                    loadProperties(isNextPage = true)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = TileOrangeSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                onDismiss = { },
                onApplyFilters = { city, minPrice, maxPrice, status ->
                    currentCity = city
                    currentMinPrice = minPrice
                    currentMaxPrice = maxPrice
                    currentStatus = status
                    isFiltered = city != null || minPrice != null || maxPrice != null || status != null

                    // Al aplicar un filtro se reinicia la lista a la primera pagina
                    loadProperties()
                }
            )
        }
    }
}