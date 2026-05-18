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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.inmohub.frontend.core.components.InmoButton
import com.inmohub.frontend.core.components.InmoInput
import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.features.property.data.PropertyRepository
import com.inmohub.frontend.features.property.dtos.PropertyDto
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.features.auth.presentation.LoginScreen
import com.inmohub.frontend.features.lead.data.LeadRepository
import com.inmohub.frontend.features.lead.dtos.LeadSummaryDto
import com.inmohub.frontend.features.lead.requests.CreateLeadRequest
import com.inmohub.frontend.core.utils.JwtUtils
import com.inmohub.frontend.features.property.presentation.mobile.components.LeadInterestCard
import kotlinx.coroutines.launch

data class PropertyDetailScreen(val propertyId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        var property by remember { mutableStateOf<PropertyDto?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        var leadName by remember { mutableStateOf("") }
        var leadEmail by remember { mutableStateOf("") }
        var leadPhone by remember { mutableStateOf("") }
        var leadMessage by remember { mutableStateOf("") }
        var isSendingLead by remember { mutableStateOf(false) }
        var leadSuccess by remember { mutableStateOf(false) }
        var leadError by remember { mutableStateOf<String?>(null) }

        val hasSession by NetworkClient.sessionManager.isSessionActive.collectAsState(initial = false)

        var userRole by remember { mutableStateOf<String?>(null) }
        var userId by remember { mutableStateOf<String?>(null) }
        var leadsForProperty by remember { mutableStateOf<List<LeadSummaryDto>>(emptyList()) }
        var isLoadingLeads by remember { mutableStateOf(false) }

        LaunchedEffect(propertyId) {
            coroutineScope.launch {
                isLoading = true
                property = PropertyRepository.getPropertyById(propertyId)
                isLoading = false
            }
        }

        LaunchedEffect(Unit) {
            val token = NetworkClient.sessionManager.getAccessToken()
            if (token != null) {
                userId = JwtUtils.getUserId(token)
                userRole = JwtUtils.getUserRoleFromToken(token)
            }
        }

        LaunchedEffect(property, userRole, userId) {
            val isAgentOrAdmin = userRole == "AGENT" || userRole == "ADMIN"
            val isOwner = userRole == "OWNER" && property?.ownerId == userId

            if (property != null && (isAgentOrAdmin || isOwner)) {
                isLoadingLeads = true
                val result = LeadRepository.getLeadsByPropertyId(propertyId)
                leadsForProperty = result?.content ?: emptyList()
                isLoadingLeads = false
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

                    // Formulario dinámico para creación de leads
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                if (!hasSession) {
                                    Text(
                                        text = "¿Te interesa esta propiedad?",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = NavyBluePrimary,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Inicia sesión o regístrate para contactar con el agente y solicitar una visita.",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    InmoButton(
                                        text = "Acceder para contactar",
                                        onClick = { navigator.push(LoginScreen()) },
                                        isSecondary = true
                                    )
                                } else {
                                    if (leadSuccess) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                            Text("¡Mensaje enviado!", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50), fontSize = 18.sp)
                                            Text("El agente se pondrá en contacto contigo pronto.", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                                        }
                                    } else {
                                        Text(
                                            text = "Contactar al Agente",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = NavyBluePrimary
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))

                                        InmoInput(value = leadName, onValueChange = { leadName = it }, label = "Tu Nombre")
                                        Spacer(modifier = Modifier.height(8.dp))
                                        InmoInput(value = leadEmail, onValueChange = { leadEmail = it }, label = "Tu Email")
                                        Spacer(modifier = Modifier.height(8.dp))
                                        InmoInput(value = leadPhone, onValueChange = { leadPhone = it }, label = "Tu Teléfono")
                                        Spacer(modifier = Modifier.height(8.dp))
                                        InmoInput(value = leadMessage, onValueChange = { leadMessage = it }, label = "Mensaje (Opcional)")

                                        if (leadError != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(text = leadError!!, color = Color.Red, fontSize = 14.sp)
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                        InmoButton(
                                            text = if (isSendingLead) "ENVIANDO..." else "ENVIAR MENSAJE",
                                            onClick = {
                                                if (leadName.isBlank() || leadEmail.isBlank()) {
                                                    leadError = "El nombre y el email son obligatorios."
                                                    return@InmoButton
                                                }

                                                coroutineScope.launch {
                                                    isSendingLead = true
                                                    leadError = null
                                                    val request = CreateLeadRequest(
                                                        name = leadName,
                                                        email = leadEmail,
                                                        phone = leadPhone.takeIf { it.isNotBlank() },
                                                        message = leadMessage.takeIf { it.isNotBlank() },
                                                        source = "WEB",
                                                        propertyId = propertyId
                                                    )
                                                    val success = LeadRepository.createLead(request)
                                                    if (success) {
                                                        leadSuccess = true
                                                    } else {
                                                        leadError = "Hubo un error al enviar tu solicitud. Inténtalo de nuevo."
                                                    }
                                                    isSendingLead = false
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (leadsForProperty.isNotEmpty() && (userRole == "AGENT" || userRole == "ADMIN" || (userRole == "OWNER" && currentProperty.ownerId == userId))) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .padding(bottom = 32.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    if (isLoadingLeads) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = NavyBluePrimary)
                                        }
                                    } else {
                                        Text(
                                            "Leads Interesados",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = NavyBluePrimary
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        leadsForProperty.forEach { lead ->
                                            LeadInterestCard(lead = lead)
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
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