package com.inmohub.frontend.ui.screens.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.inmohub.frontend.data.model.Property
import com.inmohub.frontend.data.repository.PropertyRepository
import com.inmohub.frontend.data.repository.UserRepository
import com.inmohub.frontend.data.repository.UserSummary
import com.inmohub.frontend.ui.components.PropertyCard
import com.inmohub.frontend.ui.screens.login.LoginScreen
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary

class DashboardScreen(val agentUsername: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Clientes", "Propietarios", "Propiedades")

        var clients by remember { mutableStateOf<List<UserSummary>>(emptyList()) }
        var owners by remember { mutableStateOf<List<UserSummary>>(emptyList()) }
        var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            clients = UserRepository.getUsersByRole("CLIENT")
            owners = UserRepository.getUsersByRole("OWNER")
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
                                Text("Bienvenido, $agentUsername", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        },
                        actions = {
                            TextButton(onClick = { navigator.replaceAll(LoginScreen()) }) {
                                Text("Cerrar Sesión", color = TileOrangeSecondary, fontWeight = FontWeight.Bold)
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
                    }
                }
            }
        }
    }

    @Composable
    fun UserList(users: List<UserSummary>, title: String, cardColor: Color) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyBluePrimary, modifier = Modifier.padding(bottom = 8.dp))
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
                                .background(cardColor, shape = RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(user.username.take(1).uppercase(), fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(user.username, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(user.email, fontSize = 14.sp, color = Color.Gray)
                            if (user.phone != null) {
                                Text("Tel: ${user.phone}", fontSize = 12.sp, color = NavyBluePrimary)
                            }
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
                Text("Propiedades en Cartera", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyBluePrimary, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(properties) { property ->
                PropertyCard(property)
            }
        }
    }
}