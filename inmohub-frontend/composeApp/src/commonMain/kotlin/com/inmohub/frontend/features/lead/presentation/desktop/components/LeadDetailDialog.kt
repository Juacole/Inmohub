package com.inmohub.frontend.features.lead.presentation.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.inmohub.frontend.core.components.InmoButton
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.SuccessGreen
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.features.lead.dtos.LeadDetailDto
import com.inmohub.frontend.features.lead.data.LeadRepository
import com.inmohub.frontend.features.property.data.PropertyRepository
import com.inmohub.frontend.features.property.dtos.PropertyDto
import kotlinx.coroutines.launch

@Composable
fun LeadDetailDialog(
    lead: LeadDetailDto,
    agentId: String,
    onDismiss: () -> Unit,
    onAssignSuccess: () -> Unit,
    onStatusChangeSuccess: (String) -> Unit = {},
    onPropertyClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    var property by remember { mutableStateOf<PropertyDto?>(null) }
    var isLoadingProperty by remember { mutableStateOf(true) }
    var isAssigning by remember { mutableStateOf(false) }
    var assignSuccess by remember { mutableStateOf(false) }
    var assignError by remember { mutableStateOf<String?>(null) }

    var currentStatus by remember { mutableStateOf(lead.status) }
    var isChangingStatus by remember { mutableStateOf(false) }
    var statusChangeError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(lead.propertyId) {
        isLoadingProperty = true
        property = PropertyRepository.getPropertyById(lead.propertyId)
        isLoadingProperty = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalle del Lead",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBluePrimary
                    )
                    if (lead.agentId != null) {
                        StatusDropdown(
                            currentStatus = currentStatus,
                            onStatusChange = { newStatus ->
                                scope.launch {
                                    isChangingStatus = true
                                    statusChangeError = null
                                    val success = LeadRepository.changeLeadStatus(lead.id, newStatus)
                                    isChangingStatus = false
                                    if (success) {
                                        currentStatus = newStatus
                                        onStatusChangeSuccess(newStatus)
                                    } else {
                                        statusChangeError = "Error al cambiar el estado del lead"
                                    }
                                }
                            },
                            isLoading = isChangingStatus
                        )
                    }
                }

                if (statusChangeError != null) {
                    Text(
                        text = statusChangeError!!,
                        color = Color(0xFFF44336),
                        fontSize = 14.sp
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Información del Contacto",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyBluePrimary
                        )

                        InfoRow("Nombre", lead.name)
                        InfoRow("Email", lead.email)
                        InfoRow("Teléfono", lead.phone ?: "No disponible")
                        InfoRow("Fuente", lead.source)

                        if (!lead.message.isNullOrBlank()) {
                            InfoRow("Mensaje", lead.message)
                        }

                        if (lead.agentId != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Este lead ya está asignado",
                                fontSize = 14.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Propiedad de Interés",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyBluePrimary
                        )

                        if (isLoadingProperty) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = NavyBluePrimary)
                            }
                        } else if (property != null) {
                            PropertyPreviewCard(property = property!!, onClick = onPropertyClick)
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "No se pudo cargar la propiedad",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (assignSuccess) {
                        Text(
                            text = "¡Lead asignado correctamente!",
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    } else if (assignError != null) {
                        Text(
                            text = assignError!!,
                            color = Color(0xFFF44336),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    InmoButton(
                        text = if (isAssigning) "Asignando..." else "Asignarme este Lead",
                        onClick = {
                            scope.launch {
                                isAssigning = true
                                assignError = null
                                val result = LeadRepository.assignLead(
                                    leadId = lead.id,
                                    agentId = agentId
                                )
                                isAssigning = false
                                if (result != null) {
                                    assignSuccess = true
                                    onAssignSuccess()
                                } else {
                                    assignError = "Error al asignar el lead"
                                }
                            }
                        },
                        enabled = !isAssigning && lead.agentId == null
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = NavyBluePrimary
        )
    }
}

@Composable
fun PropertyPreviewCard(property: PropertyDto, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (property.photos.isNotEmpty()) {
                AsyncImage(
                    model = property.photos.first().photoUrl,
                    contentDescription = "Foto de propiedad",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = property.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NavyBluePrimary
            )

            Text(
                text = "€${String.format("%,.0f", property.price)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TileOrangeSecondary
            )

            Text(
                text = "${property.address}, ${property.city}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            if (!property.features.isNullOrEmpty()) {
                val rooms = property.features.find { it.featureName.contains("Habitacion", ignoreCase = true) }?.featureValue
                val baths = property.features.find { it.featureName.contains("Baño", ignoreCase = true) }?.featureValue
                val area = property.features.find { it.featureName.contains("area", ignoreCase = true) || it.featureName.contains("m2", ignoreCase = true) }?.featureValue

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (rooms != null) {
                        Text("$rooms hab", fontSize = 11.sp, color = Color.Gray)
                    }
                    if (baths != null) {
                        Text("$baths baños", fontSize = 11.sp, color = Color.Gray)
                    }
                    if (area != null) {
                        Text("$area m²", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}