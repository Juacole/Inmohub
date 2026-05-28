package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.in.file;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.Result;
import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;
import com.inmohub.fsbo.ingestor.service.domain.models.OwnerDetails;
import com.inmohub.fsbo.ingestor.service.domain.models.enums.RecordStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para {@link OpenCsvParserAdapter}.
 * Verifica el parseo de archivos CSV con datos validos, filas con errores,
 * extraccion de features en formato key:value y filtrado de filas cortas.
 */
@DisplayName("OpenCsvParserAdapter")
class OpenCsvParserAdapterTest {

    private final OpenCsvParserAdapter parser = new OpenCsvParserAdapter();
    private final UUID ownerId = UUID.randomUUID();
    private final OwnerDetails owner = new OwnerDetails(ownerId, "Pepe Montana", "pepe@test.com", "600123456");

    @Test
    @DisplayName("Debe parsear un archivo CSV valido con datos correctos")
    void parsearCsvValido() {
        String csvContent = "title,description,price,area_m2,address,city,state,country,features\n" +
                "Chalet en Madrid,Amplio chalet con piscina,450000,250.5,Calle Mayor 123,Madrid,Comunidad de Madrid,España,Habitaciones:3;Baños:2\n" +
                "Apartamento Barcelona,Piso reformado,180000,80.0,Avenida Diagonal 456,Barcelona,Cataluña,España,";

        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        Result<FsboBatch, String> result = parser.parse(inputStream, owner);

        assertTrue(result.isSuccess());
        FsboBatch batch = result.getValue();
        assertEquals(2, batch.totalRecords());
        assertEquals(2, batch.getValidProperties().size());
        assertEquals(owner, batch.getOwnerDetails());

        var prop1 = batch.getProperties().get(0);
        assertEquals("Chalet en Madrid", prop1.getTitle());
        assertEquals(new java.math.BigDecimal("450000"), prop1.getPrice());
        assertEquals(250.5, prop1.getAreaM2());
        assertEquals("Calle Mayor 123", prop1.getAddress());
        assertEquals("Madrid", prop1.getCity());
        assertEquals("Comunidad de Madrid", prop1.getState());
        assertEquals("España", prop1.getCountry());
        assertEquals(2, prop1.getFeatures().size());
        assertEquals("3", prop1.getFeatures().get("Habitaciones"));
        assertEquals("2", prop1.getFeatures().get("Baños"));

        var prop2 = batch.getProperties().get(1);
        assertEquals("Apartamento Barcelona", prop2.getTitle());
        assertEquals("Barcelona", prop2.getCity());
        assertEquals(new java.math.BigDecimal("180000"), prop2.getPrice());
    }

    @Test
    @DisplayName("Debe marcar como ERROR las filas con datos invalidos")
    void filasInvalidasSeMarcanComoError() {
        String csvContent = "title,description,price,area_m2,address,city,state,country,features\n" +
                "Valido,Desc,100,100.0,Calle 1,Madrid,Madrid,España,\n" +
                "Valido2,Desc,NaN,100.0,Calle 2,Madrid,Madrid,España,";

        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        Result<FsboBatch, String> result = parser.parse(inputStream, owner);

        assertTrue(result.isSuccess());
        FsboBatch batch = result.getValue();
        assertEquals(2, batch.totalRecords());
        assertEquals(1, batch.getValidProperties().size());
        assertEquals(1, batch.getProperties().stream()
                .filter(p -> p.getStatus() == RecordStatus.ERROR).count());
    }

    @Test
    @DisplayName("Debe manejar features con formato key:value separadas por punto y coma")
    void parsearFeatures() {
        String csvContent = "title,description,price,area_m2,address,city,state,country,features\n" +
                "Chalet,Desc,100,100.0,Calle Madrid,Madrid,Madrid,España,Habitaciones:4;Baños:2;Piscina:Si";

        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        Result<FsboBatch, String> result = parser.parse(inputStream, owner);

        assertTrue(result.isSuccess());
        var prop = result.getValue().getProperties().get(0);
        assertEquals(3, prop.getFeatures().size());
        assertEquals("4", prop.getFeatures().get("Habitaciones"));
        assertEquals("2", prop.getFeatures().get("Baños"));
        assertEquals("Si", prop.getFeatures().get("Piscina"));
    }

    @Test
    @DisplayName("Debe ignorar filas con menos de 5 columnas")
    void filasCortasIgnoradas() {
        String csvContent = "title,description,price,area_m2,address,city,state,country,features\n" +
                "Chalet,Desc,100,100.0,Calle Madrid,Madrid,Madrid,España,\n" +
                "Solo,tres,columnas\n" +
                "Otro,Desc,200,150.0,Otra Calle,Barcelona,Barcelona,España,";

        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        Result<FsboBatch, String> result = parser.parse(inputStream, owner);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getValue().totalRecords());
    }
}
