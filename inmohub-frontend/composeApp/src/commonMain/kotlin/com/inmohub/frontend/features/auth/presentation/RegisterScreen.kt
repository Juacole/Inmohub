package com.inmohub.frontend.features.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TextLightGray
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.core.components.InmoButton
import com.inmohub.frontend.core.components.InmoInput
import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.core.utils.JwtUtils
import com.inmohub.frontend.features.auth.data.AuthRepository
import com.inmohub.frontend.features.auth.requests.RegisterRequest
import com.inmohub.frontend.features.lead.presentation.desktop.DashboardScreen
import com.inmohub.frontend.features.property.presentation.mobile.HomeScreen
import kotlinx.coroutines.launch

class RegisterScreen : Screen {
    private val AGENT_SECRET_CODE = "INMO-AGENT-2026"

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        var selectedRole by remember { mutableStateOf("CLIENT") }
        var isAgentMode by remember { mutableStateOf(false) }
        var agentCodeInput by remember { mutableStateOf("") }

        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        color = NavyBluePrimary,
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "INMOHUB",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Crear nueva cuenta",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isAgentMode) {
                    Text("¿Cómo quieres usar InmoHub?", fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        RoleOption(label = "Comprar/Alquilar", selected = selectedRole == "CLIENT") {
                            selectedRole = "CLIENT"
                        }
                        RoleOption(label = "Soy Propietario", selected = selectedRole == "OWNER") {
                            selectedRole = "OWNER"
                        }
                    }
                }

                InmoInput(value = firstName, onValueChange = { firstName = it }, label = "Nombre")
                InmoInput(value = lastName, onValueChange = { lastName = it }, label = "Apellidos")
                InmoInput(value = username, onValueChange = { username = it }, label = "Nombre de Usuario")
                InmoInput(value = email, onValueChange = { email = it }, label = "Email")
                InmoInput(value = phone, onValueChange = { phone = it }, label = "Teléfono (9 dígitos)")
                InmoInput(value = password, onValueChange = { password = it }, label = "Contraseña")

                TextButton(onClick = { isAgentMode = !isAgentMode }) {
                    Text(
                        if (isAgentMode) "Cancelar registro empleado" else "¿Eres empleado de la inmobiliaria?",
                        color = if (isAgentMode) TileOrangeSecondary else TextLightGray
                    )
                }

                if (isAgentMode) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Zona Corporativa", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            Text("Introduce el código proporcionado por el Administrador.", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            InmoInput(
                                value = agentCodeInput,
                                onValueChange = { agentCodeInput = it },
                                label = "Código de Delegación"
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                InmoButton(
                    text = if (isLoading) "REGISTRANDO..." else "REGISTRARSE",
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null

                            if (phone.length != 9) {
                                errorMessage = "El teléfono debe tener 9 dígitos."
                                isLoading = false
                                return@launch
                            }

                            val finalRole = if (isAgentMode) {
                                if (agentCodeInput == AGENT_SECRET_CODE) "AGENT"
                                else {
                                    errorMessage = "Código de agente inválido."
                                    isLoading = false
                                    return@launch
                                }
                            } else {
                                selectedRole
                            }

                            val request = RegisterRequest(
                                username = username,
                                email = email,
                                password = password,
                                firstName = firstName,
                                lastName = lastName,
                                phone = phone,
                                roles = listOf(finalRole)
                            )

                            val success = AuthRepository.register(request)
                            if (success) {
                                val authResponse = AuthRepository.login(email, password)

                                if (authResponse != null) {
                                    NetworkClient.sessionManager?.saveTokens(authResponse.accessToken, authResponse.refreshToken)

                                    val roleFrommToken = JwtUtils.getUserRoleFromToken(authResponse.accessToken)
                                    when(roleFrommToken) {
                                        "AGENT", "ADMIN" -> {
                                            val userId = JwtUtils.getUserId(authResponse.accessToken) ?: ""
                                            val user = AuthRepository.getUserById(userId)
                                            navigator.replaceAll(
                                                DashboardScreen(
                                                    user?.firstName + " " + user?.lastName,
                                                    userId
                                                )
                                            )
                                        }
                                        else -> navigator.replaceAll(HomeScreen())
                                    }
                                } else {
                                    navigator.pop()
                                }
                            } else {
                                errorMessage = "Error al registrar. Revisa los datos o el servidor."
                            }
                            isLoading = false
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("¿Ya tienes cuenta? ")
                    Text(
                        "Inicia Sesión",
                        color = NavyBluePrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navigator.pop() }
                    )
                }
            }
        }
    }

    @Composable
    fun RoleOption(label: String, selected: Boolean, onClick: () -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(8.dp)
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = NavyBluePrimary)
            )
            Text(label, fontSize = 14.sp)
        }
    }
}