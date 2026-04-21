package com.inmohub.lead.service.domain.ports;

import com.inmohub.lead.service.domain.model.Lead;

public interface ILeadEventPublisher {
    void publishLeadCreatedEvent(Lead lead);
}
