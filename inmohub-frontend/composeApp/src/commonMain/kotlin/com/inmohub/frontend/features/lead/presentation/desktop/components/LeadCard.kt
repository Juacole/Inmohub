package com.inmohub.frontend.features.lead.presentation.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary
import com.inmohub.frontend.features.lead.dtos.LeadSummaryDto

@Composable
fun LeadCard(
    lead: LeadSummaryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    .size(48.dp)
                    .background(
                        color = getStatusColor(lead.status).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lead.name.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = getStatusColor(lead.status)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = lead.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyBluePrimary
                )
                Text(
                    text = lead.email,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                if (!lead.phone.isNullOrBlank()) {
                    Text(
                        text = "Tel: ${lead.phone}",
                        fontSize = 12.sp,
                        color = TileOrangeSecondary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatusBadge(status = lead.status)
                Text(
                    text = lead.source ?: "",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status.uppercase()) {
        "NEW" -> Pair(Color(0xFFE3F2FD), NavyBluePrimary)
        "CONTACTED" -> Pair(Color(0xFFFFF3E0), Color(0xFFFF9800))
        "NEGOTIATION" -> Pair(Color(0xFFF3E5F5), Color(0xFF9C27B0))
        "CLOSED" -> Pair(Color(0xFFE8F5E9), Color(0xFF4CAF50))
        "LOST" -> Pair(Color(0xFFFFEBEE), Color(0xFFF44336))
        else -> Pair(Color(0xFFEEEEEE), Color.Gray)
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "NEW" -> NavyBluePrimary
        "CONTACTED" -> Color(0xFFFF9800)
        "NEGOTIATION" -> Color(0xFF9C27B0)
        "CLOSED" -> Color(0xFF4CAF50)
        "LOST" -> Color(0xFFF44336)
        else -> Color.Gray
    }
}