package com.company.gomicollection.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "DEVICE")
@Entity
public class Device {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;
    @OneToMany(mappedBy = "device")
    private List<Employee> employee_id;
    @Column(name = "ACTIVE", nullable = false)
    @NotNull
    private Boolean active = false;
    @Column(name = "VEHICLE_PLATE_NUMBER", nullable = false)
    @NotNull
    private String vehicle_plate_number;
    @Column(name = "DEVICE_NAME", nullable = false)
    @NotNull
    private String device_name;

    public String getVehicle_plate_number() {
        return vehicle_plate_number;
    }

    public void setVehicle_plate_number(String vehicle_plate_number) {
        this.vehicle_plate_number = vehicle_plate_number;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public List<Employee> getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(List<Employee> employee_id) {
        this.employee_id = employee_id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}