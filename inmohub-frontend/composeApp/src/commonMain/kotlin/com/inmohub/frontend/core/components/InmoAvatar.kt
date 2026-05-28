package com.inmohub.frontend.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inmohub.frontend.core.themes.TileOrangeSecondary

@Composable
fun InmoAvatar(
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val initials = buildString {
        if (firstName.isNotEmpty()) append(firstName.first().uppercase())
        if (lastName.isNotEmpty()) append(lastName.first().uppercase())
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 2))
            .background(TileOrangeSecondary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.ifEmpty { "?" },
            color = Color.White,
            fontSize = (size.value / 2.5).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
