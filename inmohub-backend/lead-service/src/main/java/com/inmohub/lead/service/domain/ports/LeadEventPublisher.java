package com.inmohub.lead.service.domain.ports;

import com.inmohub.lead.service.domain.model.Lead;

public interface LeadEventPublisher {
    void publishLeadCreatedEvent(Lead lead);
}
