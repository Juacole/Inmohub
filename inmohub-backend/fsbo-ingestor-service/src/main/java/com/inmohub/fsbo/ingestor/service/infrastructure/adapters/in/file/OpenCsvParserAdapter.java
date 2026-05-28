package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.in.file;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.Result;
import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;
import com.inmohub.fsbo.ingestor.service.domain.models.OwnerDetails;
import com.inmohub.fsbo.ingestor.service.domain.models.PropertyRecord;
import com.inmohub.fsbo.ingestor.service.domain.ports.ICsvParser;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
/**
 * Adaptador que implementa el parseo de archivos CSV usando la libreria OpenCSV.
 * Convierte filas CSV en registros de dominio PropertyRecord.
 */
public class OpenCsvParserAdapter implements ICsvParser {

    @Override
    public Result<FsboBatch, String> parse(InputStream fileStream, OwnerDetails ownerDetails) {
        List<PropertyRecord> records = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(fileStream))) {
            String[] nextLine;
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length < 5) continue;

                try {
                    String rawFeatures = (nextLine.length > 8 && nextLine[8] != null) ? nextLine[8] : "";
                    Map<String, String> featuresMap = extractFeatures(rawFeatures);

                    PropertyRecord record = PropertyRecord.create(
                            nextLine[0].trim(), // title
                            nextLine[1].trim(), // description
                            new BigDecimal(nextLine[2].trim().replace(",", ".")), // price
                            Double.parseDouble(nextLine[3].trim().replace(",", ".")), // area
                            nextLine[4].trim(), // address
                            nextLine[5].trim(), // city
                            nextLine[6].trim(), // state
                            nextLine[7].trim(), // country
                            featuresMap // features
                    );
                    records.add(record);
                } catch (Exception e) {
                    PropertyRecord errorRecord = PropertyRecord.createInvalid("Error formato fila CSV: " + e.getMessage());
                    records.add(errorRecord);
                }
            }

            return Result.success(FsboBatch.create(ownerDetails, LocalDateTime.now(), records));

        } catch (Exception e) {
            return Result.error("Error crítico de lectura: " + e.getMessage());
        }
    }

    private Map<String, String> extractFeatures(String rawFeatures) {
        Map<String, String> features = new HashMap<>();
        if (rawFeatures == null || rawFeatures.trim().isEmpty()) {
            return features;
        }

        String[] pairs = rawFeatures.split(";");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);

            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                if (!key.isEmpty() && !value.isEmpty()) {
                    features.put(key, value);
                }
            }
        }
        return features;
    }
}