package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.messaging;

import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;
import com.inmohub.fsbo.ingestor.service.domain.models.PropertyRecord;
import com.inmohub.fsbo.ingestor.service.domain.ports.IPropertyEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaPropertyEventPublisher implements IPropertyEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_PROPERTIES = "property.bulk.create";

    @Override
    public void publishBulkProperties(FsboBatch batch) {
        List<Map<String, Object>> propertiesPayload = new ArrayList<>();

        for (PropertyRecord record : batch.getValidProperties()) {
            Map<String, Object> propertyNode = new HashMap<>();

            propertyNode.put("ownerId", batch.getOwnerId().toString());
            propertyNode.put("title", record.getTitle());
            propertyNode.put("description", record.getDescription());
            propertyNode.put("price", record.getPrice());
            propertyNode.put("areaM2", record.getAreaM2());
            propertyNode.put("address", record.getAddress());
            propertyNode.put("city", record.getCity());
            propertyNode.put("state", record.getState());
            propertyNode.put("country", record.getCountry());
            propertyNode.put("status", "AVAILABLE");

            List<Map<String, String>> featuresPayload = new ArrayList<>();
            record.getFeatures().forEach((key, value) -> {
                Map<String, String> feature = new HashMap<>();
                feature.put("featureName", key);
                feature.put("featureValue", value);
                featuresPayload.add(feature);
            });
            propertyNode.put("features", featuresPayload);

            propertyNode.put("photos", new ArrayList<>());

            propertiesPayload.add(propertyNode);
        }

        Map<String, Object> bulkEvent = new HashMap<>();
        bulkEvent.put("ownerId", batch.getOwnerId().toString());
        bulkEvent.put("properties", propertiesPayload);

        kafkaTemplate.send(TOPIC_PROPERTIES, batch.getOwnerId().toString(), bulkEvent);
    }
}