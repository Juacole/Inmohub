package com.inmohub.frontend.features.property.presentation.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.features.auth.data.AuthRepository
import com.inmohub.frontend.features.auth.presentation.LoginScreen
import com.inmohub.frontend.features.lead.data.LeadRepository
import com.inmohub.frontend.features.lead.dtos.LeadSummaryDto
import com.inmohub.frontend.features.property.data.PropertyRepository
import com.inmohub.frontend.features.property.dtos.PropertyDto
import kotlinx.coroutines.launch

class OwnerDashboardScreen(val ownerId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Mis Publicaciones", "Carga Masiva (FSBO)")

        var properties by remember { mutableStateOf<List<PropertyDto>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var ownerName by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            properties = PropertyRepository.getPropertiesByOwner(ownerId)
            val user = AuthRepository.getUserById(ownerId)
            ownerName = user?.firstName?.plus(" ")?.plus(user.lastName) ?: ""
            isLoading = false
        }

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Column {
                                Text("Portal del Propietario", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                if (ownerName.isNotEmpty()) {
                                    Text(
                                        "Bienvenido, $ownerName",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            TextButton(onClick = { navigator.pop() }) {
                                Text("Volver", color = NavyBluePrimary, fontWeight = FontWeight.Bold)
                            }
                        },
                    actions = {
                        TextButton(onClick = { navigator.push(CreatePropertyScreen()) }) {
                            Text("Publicar", color = NavyBluePrimary, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { navigator.replaceAll(LoginScreen()) }) {
                            Text("Cerrar Sesión", color = TileOrangeSecondary, fontWeight = FontWeight.Bold)
                        }
                    },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TileOrangeSecondary)
                    }
                } else {
                    when (selectedTabIndex) {
                        0 -> PropertiesTab(properties, navigator, scope) { propertyId ->
                            properties = properties.filter { it.id != propertyId }
                        }
                        1 -> FsboUploadTab()
                    }
                }
            }
        }
    }

    @Composable
    fun PropertiesTab(
        properties: List<PropertyDto>,
        navigator: cafe.adriel.voyager.navigator.Navigator,
        scope: kotlinx.coroutines.CoroutineScope,
        onDeleteProperty: (String) -> Unit
    ) {
        var showLeadsDialog by remember { mutableStateOf(false) }
        var leadsPropertyId by remember { mutableStateOf("") }
        var leadsList by remember { mutableStateOf<List<LeadSummaryDto>>(emptyList()) }
        var isLoadingLeads by remember { mutableStateOf(false) }

        var showDeleteDialog by remember { mutableStateOf(false) }
        var deletePropertyId by remember { mutableStateOf("") }
        var isDeleting by remember { mutableStateOf(false) }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar propiedad", fontWeight = FontWeight.Bold, color = Color(0xFFF44336)) },
                text = { Text("¿Estás seguro de que quieres eliminar esta propiedad? También se eliminarán los leads asociados.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                isDeleting = true
                                val success = PropertyRepository.deleteProperty(deletePropertyId)
                                if (success) {
                                    onDeleteProperty(deletePropertyId)
                                }
                                showDeleteDialog = false
                                isDeleting = false
                            }
                        }
                    ) {
                        Text("Eliminar", color = Color(0xFFF44336))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showLeadsDialog) {
            AlertDialog(
                onDismissRequest = { showLeadsDialog = false },
                title = { Text("Leads Interesados", fontWeight = FontWeight.Bold, color = NavyBluePrimary) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (isLoadingLeads) {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = TileOrangeSecondary)
                            }
                        } else if (leadsList.isEmpty()) {
                            Text("No hay leads para esta propiedad", color = Color.Gray)
                        } else {
                            leadsList.forEach { lead ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(lead.name, fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                        Text(lead.email, fontSize = 12.sp, color = Color.Gray)
                                        if (lead.phone != null) {
                                            Text(lead.phone, fontSize = 12.sp, color = Color.Gray)
                                        }
                                        Text("Estado: ${lead.status}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.padding(4.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLeadsDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        if (properties.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No tienes propiedades publicadas", fontSize = 18.sp, color = NavyBluePrimary)
                    Text("Crea propiedades o sube un archivo CSV para empezar", fontSize = 14.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(properties, key = { it.id }) { property ->
                    OwnerPropertyCard(
                        property = property,
                        onEdit = { navigator.push(EditPropertyScreen(property.id)) },
                        onViewLeads = {
                            leadsPropertyId = property.id
                            showLeadsDialog = true
                            scope.launch {
                                isLoadingLeads = true
                                val result = LeadRepository.getLeadsByPropertyId(property.id)
                                leadsList = result?.content ?: emptyList()
                                isLoadingLeads = false
                            }
                        },
                        onDelete = {
                            deletePropertyId = property.id
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}
