package com.inmohub.frontend.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.inmohub.frontend.core.themes.InmoTypography
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TileOrangeSecondary

@Composable
fun InmoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion,
    isSecondary: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSecondary) TileOrangeSecondary else NavyBluePrimary,
            contentColor = Color.Companion.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text.uppercase(), style = InmoTypography.labelLarge)
    }
}