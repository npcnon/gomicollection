package com.company.gomicollection.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "ROUTE")
@Entity
public class Route {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;
    @Column(name = "ROUTE_NAME", nullable = false)
    @NotNull
    private String route_name;
    @JoinTable(name = "DEVICE_ROUTE_LINK",
            joinColumns = @JoinColumn(name = "ROUTE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "DEVICE_ID", referencedColumnName = "ID"))
    @ManyToMany
    private List<Device> devices;

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}