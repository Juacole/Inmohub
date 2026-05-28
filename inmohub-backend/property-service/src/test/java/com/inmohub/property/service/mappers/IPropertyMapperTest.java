package com.inmohub.property.service.mappers;

import com.inmohub.property.service.models.Property;
import com.inmohub.property.service.models.PropertyFeature;
import com.inmohub.property.service.models.PropertyPhoto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para {@link IPropertyMapper}.
 * Verifica los metodos del mapper que contienen logica de negocio:
 * extraccion de la foto principal y enlace de features con su propiedad padre.
 */
@DisplayName("IPropertyMapper")
class IPropertyMapperTest {

    private final IPropertyMapper mapper = new IPropertyMapperImpl();

    @Test
    @DisplayName("extractPrimaryPhotoUrl debe retornar null si la lista es null")
    void fotosNulasRetornaNull() {
        assertNull(mapper.extractPrimaryPhotoUrl(null));
    }

    @Test
    @DisplayName("extractPrimaryPhotoUrl debe retornar null si la lista esta vacia")
    void fotosVaciasRetornaNull() {
        assertNull(mapper.extractPrimaryPhotoUrl(List.of()));
    }

    @Test
    @DisplayName("extractPrimaryPhotoUrl debe retornar la foto primaria")
    void retornarFotoPrimaria() {
        PropertyPhoto primary = new PropertyPhoto();
        primary.setPhotoUrl("https://firebase.com/primary.jpg");
        primary.setIsPrimary(true);

        PropertyPhoto secondary = new PropertyPhoto();
        secondary.setPhotoUrl("https://firebase.com/secondary.jpg");
        secondary.setIsPrimary(false);

        String url = mapper.extractPrimaryPhotoUrl(List.of(primary, secondary));

        assertEquals("https://firebase.com/primary.jpg", url);
    }

    @Test
    @DisplayName("extractPrimaryPhotoUrl debe retornar la primera foto si no hay primaria marcada")
    void retornarPrimeraFotoSiNoHayPrimaria() {
        PropertyPhoto photo1 = new PropertyPhoto();
        photo1.setPhotoUrl("https://firebase.com/first.jpg");
        photo1.setIsPrimary(false);

        PropertyPhoto photo2 = new PropertyPhoto();
        photo2.setPhotoUrl("https://firebase.com/second.jpg");
        photo2.setIsPrimary(false);

        String url = mapper.extractPrimaryPhotoUrl(List.of(photo1, photo2));

        assertEquals("https://firebase.com/first.jpg", url);
    }

    @Test
    @DisplayName("linkFeatures debe asignar property a cada feature")
    void enlazarFeaturesAPropiedad() {
        Property property = new Property();
        property.setId(java.util.UUID.randomUUID());
        PropertyFeature feature1 = new PropertyFeature();
        PropertyFeature feature2 = new PropertyFeature();
        property.setFeatures(new ArrayList<>(List.of(feature1, feature2)));

        mapper.linkFeatures(property);

        for (PropertyFeature feature : property.getFeatures()) {
            assertEquals(property, feature.getProperty());
        }
    }
}
