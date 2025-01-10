package com.company.gomicollection.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@JmixEntity
@Table(name = "DEVICE_LOCATION_LOGS", indexes = {
        @Index(name = "IDX_DEVICE_LOCATION_LOGS_DEVICE", columnList = "DEVICE_ID")
})
@Entity
public class DeviceLocationLogs {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "DEVICE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Device device;
    @Column(name = "LATITUDE", nullable = false, precision = 8, scale = 6)
    @NotNull
    @DecimalMin(value = "-90.000000")
    @DecimalMax(value = "90.000000")
    private BigDecimal latitude;
    @Column(name = "LONGITUDE", nullable = false, precision = 9, scale = 6)
    @NotNull
    @DecimalMin(value = "-180.000000")
    @DecimalMax(value = "180.000000")
    private BigDecimal longitude;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_AND_TIME", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date date_and_time;

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Date getDate_and_time() {
        return date_and_time;
    }

    public void setDate_and_time(Date date_and_time) {
        this.date_and_time = date_and_time;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }



}