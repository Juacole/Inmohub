package com.inmohub.frontend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.core.themes.inmohubColorScheme
import com.inmohub.frontend.core.utils.JwtUtils
import com.inmohub.frontend.features.auth.data.AuthRepository
import com.inmohub.frontend.features.auth.data.local.SessionManager
import com.inmohub.frontend.features.auth.data.local.createDataStore
import com.inmohub.frontend.features.lead.presentation.desktop.DashboardScreen
import com.inmohub.frontend.features.property.presentation.mobile.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // Singleton
    // remember evita nuevas instancias si interfaz se redibuja
    val dataStore = remember { createDataStore() }
    val sessionManager = remember { SessionManager(dataStore) }

    // Ejecución de bloque asincrono una sola vez al iniciar aplicación
    LaunchedEffect(Unit) {
        NetworkClient.sessionManager = sessionManager
    }

    val hasSession by sessionManager.isSessionActive.collectAsState(initial = false)
    var initialScreen by remember { mutableStateOf<Screen?>(null) }

    LaunchedEffect(hasSession) {
        if (!hasSession) {
            initialScreen = HomeScreen()
        } else {
            val token = sessionManager.getAccessToken()
            if (token == null) {
                initialScreen = HomeScreen()
            } else {
                val role = JwtUtils.getUserRoleFromToken(token)
                if (role == "ADMIN" || role == "AGENT") {
                    val userId = JwtUtils.getUserId(token) ?: ""
                    val user = AuthRepository.getUserById(userId)
                    initialScreen = DashboardScreen(
                        user?.firstName + " " + user?.lastName,
                        userId
                    )
                } else {
                    initialScreen = HomeScreen()
                }
            }
        }
    }

    MaterialTheme(colorScheme = inmohubColorScheme) {
        if (initialScreen == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = inmohubColorScheme.secondary)
            }
        } else {
            // let operador seguro de kotlin, ejecuta lo que le pasen
            initialScreen?.let { Navigator(it) }
        }
    }
}