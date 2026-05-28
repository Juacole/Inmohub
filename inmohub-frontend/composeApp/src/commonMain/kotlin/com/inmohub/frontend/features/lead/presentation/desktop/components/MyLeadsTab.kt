package com.inmohub.frontend.features.lead.presentation.desktop.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inmohub.frontend.core.themes.NavyBluePrimary
import com.inmohub.frontend.features.lead.data.LeadRepository
import com.inmohub.frontend.features.lead.dtos.LeadDetailDto
import com.inmohub.frontend.features.lead.dtos.LeadSummaryDto
import com.inmohub.frontend.features.property.responses.PagedListResponse
import kotlinx.coroutines.launch

@Composable
fun MyLeadsTab(
    agentId: String,
    modifier: Modifier = Modifier,
    onPropertyClick: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var leads by remember { mutableStateOf<List<LeadSummaryDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var hasMore by remember { mutableStateOf(true) }
    var currentPage by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    var selectedLead by remember { mutableStateOf<LeadDetailDto?>(null) }

    suspend fun loadLeads(page: Int, append: Boolean = false) {
        if (append) {
            isLoadingMore = true
        } else {
            isLoading = true
        }
        error = null

        val result: PagedListResponse<LeadSummaryDto>? = LeadRepository.getLeadsByAgentId(
            agentId = agentId,
            page = page,
            size = 20
        )

        if (result != null) {
            val newLeads = result.content
            if (append) {
                leads = leads + newLeads
            } else {
                leads = newLeads
            }
            hasMore = newLeads.isNotEmpty() && page < result.totalPages - 1
            currentPage = page
        } else {
            error = "Error al cargar tus leads"
            hasMore = false
        }

        isLoading = false
        isLoadingMore = false
    }

    LaunchedEffect(Unit) {
        loadLeads(0)
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItems > 0 && lastVisibleItem >= totalItems - 6 && !isLoadingMore && hasMore && !isLoading && error == null
        }.collect { shouldLoadMore ->
            if (shouldLoadMore) {
                loadLeads(currentPage + 1, append = true)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = NavyBluePrimary)
                Text(
                    "Cargando tus leads...",
                    modifier = Modifier.padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (error != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else if (leads.isEmpty()) {
            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "No tienes leads asignados",
                        fontSize = 18.sp,
                        color = NavyBluePrimary
                    )
                    Text(
                        "Los leads que te asignen aparecerán aquí",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Mis Leads Asignados",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBluePrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(leads, key = { it.id }) { lead ->
                    LeadCard(
                        lead = lead,
                        onClick = {
                            selectedLead = LeadDetailDto(
                                id = lead.id,
                                name = lead.name,
                                email = lead.email,
                                phone = lead.phone,
                                status = lead.status,
                                source = lead.source ?: "",
                                propertyId = lead.propertyId,
                                senderParticipantId = lead.senderParticipantId,
                                agentId = agentId,
                                createdAt = lead.createdAt
                            )
                        }
                    )
                }

                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = NavyBluePrimary)
                        }
                    }
                }
            }
        }

        selectedLead?.let { lead ->
            LeadDetailDialog(
                lead = lead,
                agentId = agentId,
                onDismiss = { selectedLead = null },
                onAssignSuccess = { selectedLead = null },
            onStatusChangeSuccess = { newStatus ->
                scope.launch { loadLeads(0) }
            },
                onPropertyClick = { onPropertyClick(lead.propertyId) }
            )
        }
    }
}