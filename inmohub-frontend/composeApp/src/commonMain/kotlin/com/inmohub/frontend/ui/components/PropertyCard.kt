package com.inmohub.frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inmohub.frontend.data.model.Property
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.SuccessGreen
import com.inmohub.frontend.core.themes.TileOrangeSecondary

@Composable
fun PropertyCard(property: Property) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.Gray)
            ) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    color = if (property.status == "AVAILABLE") SuccessGreen else TileOrangeSecondary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = property.status,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${property.price} €",
                    color = NavyBluePrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = property.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = property.address, color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text("${property.areaM2} m²", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }
    }
}