package com.company.gomicollection.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@JmixEntity
@Table(name = "COORDINATES", indexes = {
        @Index(name = "IDX_COORDINATES_ROUTE", columnList = "ROUTE_ID")
})
@Entity
public class Coordinates {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "ROUTE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Route route;
    @Column(name = "LATITUDE", nullable = false, precision = 8, scale = 6)
    @NotNull
    private BigDecimal latitude;
    @Column(name = "LONGITUDE", nullable = false, precision = 9, scale = 6)
    @NotNull
    private BigDecimal longitude;

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}