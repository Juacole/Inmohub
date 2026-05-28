package com.inmohub.frontend.features.property.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inmohub.frontend.core.components.InmoButton
import com.inmohub.frontend.core.components.InmoInput
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.core.themes.TextLightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    onApplyFilters: (
        city: String?,
        minPrice: Double?,
        maxPrice: Double?,
        status: String?
    ) -> Unit
) {
    var city by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Todos") }
    var statusDropdownExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Todos", "AVAILABLE", "SOLD", "RENTED", "OFF_MARKET")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
          modifier = Modifier
              .fillMaxSize()
              .padding(16.dp)
              .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Filtros Avanzados", style = MaterialTheme.typography.titleLarge, color = NavyBluePrimary)

            InmoInput(value = city, onValueChange = { city = it }, label = "Ciudad")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    InmoInput(value = minPrice, onValueChange = { minPrice = it }, label = "Precio Min (€)")
                }
                Box(modifier = Modifier.weight(1f)) {
                    InmoInput(value = maxPrice, onValueChange = { maxPrice = it }, label = "Precio Max (€)")
                }
            }

            Text("Estado", style = MaterialTheme.typography.labelMedium, color = TextLightGray)
            ExposedDropdownMenuBox(
                expanded = statusDropdownExpanded,
                onExpandedChange = { statusDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedStatus,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyBluePrimary,
                        unfocusedBorderColor = TextLightGray,
                        focusedLabelColor = NavyBluePrimary
                    ),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = statusDropdownExpanded,
                    onDismissRequest = { statusDropdownExpanded = false }
                ) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedStatus = option
                                statusDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            InmoButton(
                text = "Aplicar Filtros",
                onClick = {
                    onApplyFilters(
                        city.takeIf { it.isNotBlank() },
                        minPrice.toDoubleOrNull(),
                        maxPrice.toDoubleOrNull(),
                        selectedStatus.takeIf { it != "Todos" }
                    )
                }
            )
        }
    }
}
