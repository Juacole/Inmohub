package com.inmohub.frontend.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.inmohub.frontend.data.model.UserSession
import com.inmohub.frontend.data.repository.AuthRepository
import com.inmohub.frontend.core.ui.InmoButton
import com.inmohub.frontend.core.ui.InmoInput
import com.inmohub.frontend.ui.screens.desktop.DashboardScreen
import com.inmohub.frontend.ui.screens.mobile.PropertiesListScreen
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TextLightGray
import kotlinx.coroutines.launch

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        color = NavyBluePrimary,
                        shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "INMOHUB",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Inicia sesión para continuar",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                InmoInput(value = email, onValueChange = { email = it }, label = "Email")
                InmoInput(value = password, onValueChange = { password = it }, label = "Contraseña")

                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                InmoButton(
                    text = if (isLoading) "VERIFICANDO..." else "LOGIN",
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null

                            val response = AuthRepository.login(email, password)

                            if (response != null) {
                                val user = response.toUser()

                                val userSession = UserSession(
                                    id = response.toUser().id.toString(),
                                    username = response.username,
                                    role = response.role,
                                    token = "token_dummy"
                                )
                                when (user.role) {
                                    "AGENT", "ADMIN" -> navigator.push(DashboardScreen(userSession.username))

                                    else -> navigator.push(PropertiesListScreen(user))
                                }
                            } else {
                                errorMessage = "Credenciales incorrectas"
                            }
                            isLoading = false
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿No tienes una cuenta? ", color = TextLightGray)
                    Text(
                        "Registrate",
                        color = NavyBluePrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navigator.push(RegisterScreen())
                        }
                    )
                }
            }
        }
    }
}