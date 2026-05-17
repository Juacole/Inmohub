package com.inmohub.frontend.features.lead.presentation.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.features.auth.data.AuthRepository
import com.inmohub.frontend.features.property.domain.Property
import com.inmohub.frontend.features.property.data.PropertyRepository
import com.inmohub.frontend.features.auth.responses.UserSummaryResponse
import com.inmohub.frontend.features.auth.presentation.LoginScreen
import com.inmohub.frontend.features.property.dtos.PropertySummaryDto
import com.inmohub.frontend.features.property.presentation.shared.PropertyCard
import com.inmohub.frontend.features.lead.presentation.desktop.components.LeadsBagTab

class DashboardScreen(val agentUsername: String, val agentId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Clientes", "Propietarios", "Propiedades", "Bolsa de Leads")

        var clients by remember { mutableStateOf<List<UserSummaryResponse>>(emptyList()) }
        var owners by remember { mutableStateOf<List<UserSummaryResponse>>(emptyList()) }
        var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            clients = AuthRepository.getUsersByRole("CLIENT")
            owners = AuthRepository.getUsersByRole("OWNER")
            properties = PropertyRepository.getAllProperties()
            isLoading = false
        }

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Column {
                                Text("Panel de Agente", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                Text(
                                    "Bienvenido, $agentUsername",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        },
                        actions = {
                            TextButton(onClick = { navigator.replaceAll(LoginScreen()) }) {
                                Text(
                                    "Cerrar Sesión",
                                    color = TileOrangeSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = NavyBluePrimary,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            if (selectedTabIndex < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = TileOrangeSecondary,
                                    height = 4.dp
                                )
                            }
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title, fontWeight = FontWeight.Bold) },
                                selectedContentColor = TileOrangeSecondary,
                                unselectedContentColor = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFF0F2F5))
            ) {
                if (isLoading) {
                    Text("Cargando datos...", modifier = Modifier.align(Alignment.Center))
                } else {
                    when (selectedTabIndex) {
                        0 -> UserList(clients, "Clientes Activos", Color(0xFFE3F2FD))
                        1 -> UserList(owners, "Propietarios Registrados", Color(0xFFFFF3E0))
                        2 -> PropertyList(properties)
                        3 -> LeadsBagTab(agentId = agentId)
                    }
                }
            }
        }
    }

    @Composable
    fun UserList(users: List<UserSummaryResponse>, title: String, cardColor: Color) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBluePrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(users) { user ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    cardColor,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                user.username.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = NavyBluePrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(user.username, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(user.email, fontSize = 14.sp, color = Color.Gray)
                            Text("Tel: ${user.phone}", fontSize = 12.sp, color = NavyBluePrimary)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PropertyList(properties: List<Property>) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Propiedades en Cartera",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBluePrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(properties) { property ->
                property.id?.let {
                    PropertyCard(
                        property = PropertySummaryDto(
                            it,
                            property.title,
                            property.price,
                            property.address,
                            property.status,
                            ""
                        ),
                        onClick = {}
                    )
                }
            }
        }
    }
}