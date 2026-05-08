package com.inmohub.property.service.specifications;

import com.inmohub.property.service.models.Property;
import com.inmohub.property.service.models.enums.PropertyStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class PropertySpecifications {

    private static Specification<Property> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    private static Specification<Property> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<Property> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) {
                return null;
            }
            try {
                PropertyStatus enumStatus = PropertyStatus.valueOf(status.toUpperCase());
                return cb.equal(root.get("status"), enumStatus);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Property> buildSpecification(
            String city,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status
    ) {
        List<Specification<Property>> specs = new ArrayList<>();

        if (city != null && !city.isBlank()) {
            specs.add(hasCity(city));
        }

        if (minPrice != null || maxPrice != null) {
            specs.add(priceBetween(minPrice, maxPrice));
        }

        if (status != null && !status.isBlank()) {
            specs.add(hasStatus(status));
        }

        if (specs.isEmpty()) {
            return Specification.allOf();
        }

        return specs.stream().reduce(Specification::and).orElse(Specification.allOf());
    }
}
