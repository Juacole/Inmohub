package com.inmohub.frontend.ui.screens.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.inmohub.frontend.data.model.CreateProperty
import com.inmohub.frontend.data.model.Property
import com.inmohub.frontend.data.model.User
import com.inmohub.frontend.data.repository.PropertyRepository
import com.inmohub.frontend.core.ui.InmoButton
import com.inmohub.frontend.core.ui.InmoInput
import com.inmohub.frontend.ui.components.PropertyCard
import com.inmohub.frontend.ui.screens.login.LoginScreen
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import kotlinx.coroutines.launch

data class PropertiesListScreen(val user: User) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var showDialog by remember { mutableStateOf(false) }

        fun loadProperties() {
            coroutineScope.launch {
                isLoading = true
                try {
                    properties = if (user.role == "OWNER") {
                        PropertyRepository.getPropertiesByOwner(user.id)
                    } else {
                        PropertyRepository.getAllProperties()
                    }
                } catch (e: Exception) {
                    properties = emptyList()
                }
                isLoading = false
            }
        }

        LaunchedEffect(Unit) {
            loadProperties()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Mi Perfil InmoHub", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
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
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NavyBluePrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(TileOrangeSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = (user.firstName.take(1) + user.lastName.take(1)).uppercase()
                            Text(
                                text = initials.ifBlank { user.username.take(2).uppercase() },
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "${user.firstName} ${user.lastName}",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (user.role == "OWNER") "Propietario" else "Cliente / Buscador",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.email,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (user.role == "OWNER") {
                    InmoButton(
                        text = "Agregar Propiedad",
                        onClick = { showDialog = true }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = if (user.role == "OWNER") "Mis Propiedades Publicadas" else "Propiedades Disponibles",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBluePrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TileOrangeSecondary)
                    }
                } else if (properties.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No hay propiedades para mostrar.",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(properties) { property ->
                            PropertyCard(property = property)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AddPropertyDialog(
                userId = user.id,
                onDismiss = { showDialog = false },
                onSuccess = {
                    showDialog = false
                    loadProperties()
                }
            )
        }
    }
}

@Composable
fun AddPropertyDialog(
    userId: String,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Nueva Propiedad",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBluePrimary
                )

                InmoInput(value = title, onValueChange = { title = it }, label = "Título")
                InmoInput(value = description, onValueChange = { description = it }, label = "Descripción")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        InmoInput(value = price, onValueChange = { price = it }, label = "Precio (€)")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        InmoInput(value = area, onValueChange = { area = it }, label = "Área (m²)")
                    }
                }

                InmoInput(value = address, onValueChange = { address = it }, label = "Dirección")

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        InmoButton(
                            text = "Cancelar",
                            onClick = onDismiss,
                            isSecondary = true
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        InmoButton(
                            text = if (isSaving) "..." else "Guardar",
                            onClick = {
                                coroutineScope.launch {
                                    isSaving = true
                                    val newProperty = CreateProperty(
                                        titulo = title,
                                        descripcion = description,
                                        precio = price.toDoubleOrNull() ?: 0.0,
                                        area = area.toDoubleOrNull() ?: 0.0,
                                        direccion = address,
                                        idPropietario = userId
                                    )
                                    val result = PropertyRepository.createProperty(newProperty)
                                    if (result != null) {
                                        onSuccess()
                                    } else {
                                        errorMessage = "Error al guardar"
                                    }
                                    isSaving = false
                                }
                            },
                            isSecondary = false
                        )
                    }
                }
            }
        }
    }
}