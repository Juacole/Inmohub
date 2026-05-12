package com.inmohub.frontend.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TextLightGray

@Composable
fun InmoInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.Companion,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NavyBluePrimary,
            unfocusedBorderColor = TextLightGray,
            focusedLabelColor = NavyBluePrimary
        ),
        singleLine = true
    )
}