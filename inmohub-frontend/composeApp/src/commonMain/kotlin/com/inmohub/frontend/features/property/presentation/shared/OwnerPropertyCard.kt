package com.inmohub.frontend.features.property.presentation.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
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
import com.inmohub.frontend.features.property.dtos.PropertyDto

@Composable
fun OwnerPropertyCard(
    property: PropertyDto,
    onEdit: () -> Unit,
    onViewLeads: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusLabel = property.status.uppercase()
    val statusColor = if (property.status == "AVAILABLE") Color(0xFF4CAF50) else TileOrangeSecondary

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = property.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = NavyBluePrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "€%,.0f".format(property.price),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TileOrangeSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${property.address}, ${property.city}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = statusLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onViewLeads) {
                    Text("\uD83D\uDC65", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onEdit) {
                    Text("\u270F\uFE0F", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete) {
                    Text("\uD83D\uDDD1\uFE0F", fontSize = 18.sp)
                }
            }
        }
    }
}
