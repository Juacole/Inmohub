package com.inmohub.frontend.features.auth.presentation.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import com.inmohub.frontend.core.components.InmoAvatar
import com.inmohub.frontend.core.components.InmoButton
import com.inmohub.frontend.core.components.InmoInput
import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.core.themes.ErrorRed
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.core.utils.JwtUtils
import com.inmohub.frontend.features.auth.data.AuthRepository
import com.inmohub.frontend.features.auth.domain.User
import com.inmohub.frontend.features.auth.requests.UpdateUserProfileRequest
import com.inmohub.frontend.features.property.presentation.mobile.HomeScreen
import com.inmohub.frontend.features.property.presentation.shared.OwnerDashboardScreen
import kotlinx.coroutines.launch

class ProfileScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var user by remember { mutableStateOf<User?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        var originalFirstName by remember { mutableStateOf("") }
        var originalLastName by remember { mutableStateOf("") }
        var originalPhone by remember { mutableStateOf("") }

        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }

        var isSaving by remember { mutableStateOf(false) }
        var saveSuccess by remember { mutableStateOf(false) }
        var saveError by remember { mutableStateOf<String?>(null) }

        var showDeleteDialog by remember { mutableStateOf(false) }
        var deleteConfirmText by remember { mutableStateOf("") }
        var isDeleting by remember { mutableStateOf(false) }

        val hasChanges by remember {
            derivedStateOf {
                firstName != originalFirstName || lastName != originalLastName || phone != originalPhone
            }
        }

    var userId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val token = NetworkClient.sessionManager?.getAccessToken()
        userId = JwtUtils.getUserId(token.orEmpty()) ?: ""
        user = AuthRepository.getUserById(userId)
        user?.let { u ->
                firstName = u.firstName
                lastName = u.lastName
                phone = u.phone
                originalFirstName = u.firstName
                originalLastName = u.lastName
                originalPhone = u.phone
            }
            isLoading = false
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text("Eliminar cuenta", fontWeight = FontWeight.Bold, color = ErrorRed)
                },
                text = {
                    Column {
                        Text("Esta acción es irreversible. Se eliminarán tu cuenta, tus propiedades y todos los datos asociados.")
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Escribe \"ELIMINAR\" para confirmar:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        InmoInput(
                            value = deleteConfirmText,
                            onValueChange = { deleteConfirmText = it },
                            label = "ELIMINAR"
                        )
                    }
                },
                confirmButton = {
                    InmoButton(
                        text = if (isDeleting) "ELIMINANDO..." else "Confirmar",
                        onClick = {
                            scope.launch {
                                isDeleting = true
                                val success = AuthRepository.deleteUser(userId)
                                if (success) {
                                    NetworkClient.sessionManager?.clearSession()
                                    navigator.replaceAll(HomeScreen())
                                }
                                isDeleting = false
                            }
                        },
                        enabled = deleteConfirmText == "ELIMINAR" && !isDeleting,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold, color = NavyBluePrimary) },
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
            } else if (user != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    InmoAvatar(
                        firstName = user!!.firstName,
                        lastName = user!!.lastName,
                        size = 100.dp
                    )

                    Text(
                        "Bienvenido, ${user!!.firstName} ${user!!.lastName}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBluePrimary
                    )
                    Text(
                        user!!.email,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    if (user!!.roles.contains("OWNER")) {
                        InmoButton(
                            text = "Panel de Propietario",
                            onClick = { navigator.push(OwnerDashboardScreen(userId)) },
                            isSecondary = true
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Editar Información",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = NavyBluePrimary
                            )

                            InmoInput(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = "Nombre"
                            )
                            InmoInput(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = "Apellidos"
                            )
                            InmoInput(
                                value = phone,
                                onValueChange = { phone = it },
                                label = "Teléfono"
                            )

                            if (saveSuccess) {
                                Text("Perfil actualizado correctamente", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                            }
                            if (saveError != null) {
                                Text(saveError!!, color = ErrorRed)
                            }

                            InmoButton(
                                text = if (isSaving) "GUARDANDO..." else "Guardar Cambios",
                                onClick = {
                                    scope.launch {
                                        isSaving = true
                                        saveSuccess = false
                                        saveError = null
                                        val request = UpdateUserProfileRequest(
                                            firstName = firstName.takeIf { it != originalFirstName },
                                            lastName = lastName.takeIf { it != originalLastName },
                                            phone = phone.takeIf { it != originalPhone }
                                        )
                                        val updatedUser = AuthRepository.updateProfile(request)
                                        if (updatedUser != null) {
                                            user = updatedUser
                                            originalFirstName = updatedUser.firstName
                                            originalLastName = updatedUser.lastName
                                            originalPhone = updatedUser.phone
                                            firstName = updatedUser.firstName
                                            lastName = updatedUser.lastName
                                            phone = updatedUser.phone
                                            saveSuccess = true
                                        } else {
                                            saveError = "Error al guardar los cambios"
                                        }
                                        isSaving = false
                                    }
                                },
                                enabled = hasChanges && !isSaving
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

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
                                "Zona de Peligro",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = ErrorRed
                            )
                            Text(
                                "Eliminar tu cuenta es una acción irreversible. Se eliminarán todas tus propiedades y datos asociados.",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            InmoButton(
                                text = "Eliminar mi cuenta",
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se pudo cargar el perfil", color = Color.Red)
                }
            }
        }
    }
}
