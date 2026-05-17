package com.inmohub.frontend.features.lead.data

import com.inmohub.frontend.core.network.NetworkClient
import com.inmohub.frontend.features.lead.requests.AssignLeadRequest
import com.inmohub.frontend.features.lead.requests.CreateLeadRequest
import com.inmohub.frontend.features.lead.responses.LeadAssignmentResponse
import com.inmohub.frontend.features.lead.dtos.LeadSummaryDto
import com.inmohub.frontend.features.property.responses.PagedListResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object LeadRepository {
    suspend fun createLead(request: CreateLeadRequest): Boolean {
        return try {
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/leads/create") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            println("Error al crear lead: ${e.message}")
            false
        }
    }

    suspend fun getAllLeads(
        page: Int = 0,
        size: Int = 10
    ): PagedListResponse<LeadSummaryDto>? {
        return try {
            val response = NetworkClient.client.get("${NetworkClient.BASE_URL}/leads/all") {
                parameter("page", page)
                parameter("size", size)
            }
            if (response.status.value == 200) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error al obtener listado de leads: ${e.message}")
            null
        }
    }

    suspend fun assignLead(
        leadId: String,
        agentId: String,
        assignmentNotes: String? = null
    ): LeadAssignmentResponse? {
        return try {
            val request = AssignLeadRequest(
                agentId = agentId,
                assignmentNotes = assignmentNotes
            )
            val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/leads/$leadId/assign") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.value == 200) {
                response.body()
            } else {
                println("Error al asignar lead con ID ${leadId} al agente ${agentId}: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("Error al asignar lead: ${e.message}")
            null
        }
    }
}