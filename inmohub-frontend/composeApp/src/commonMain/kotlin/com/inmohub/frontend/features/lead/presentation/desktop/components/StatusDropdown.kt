package com.inmohub.frontend.features.lead.presentation.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.inmohub.frontend.core.themes.NavyBluePrimary

@Composable
fun StatusDropdown(
    currentStatus: String,
    onStatusChange: (String) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val statuses = listOf("NEW", "CONTACTED", "NEGOTIATION", "CLOSED", "LOST")

    Column(modifier = modifier) {
        Text(
            text = "Estado del Lead",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isLoading) { expanded = true },
                colors = CardDefaults.cardColors(
                    containerColor = getStatusColor(currentStatus).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp),
                            color = NavyBluePrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        StatusBadge(status = currentStatus)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "▼",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBluePrimary
                    )
                }
            }

            if (expanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .heightIn(max = 160.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        statuses.forEach { status ->
                            val isSelected = status == currentStatus
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) getStatusColor(status).copy(alpha = 0.1f)
                                        else Color.Transparent,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        if (!isSelected && !isLoading) {
                                            onStatusChange(status)
                                            expanded = false
                                        }
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(status = status)
                            }
                        }
                    }
                }
            }
        }
    }
}