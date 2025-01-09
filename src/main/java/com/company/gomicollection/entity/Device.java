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
    @JoinTable(name = "DEVICE_EMPLOYEE_LINK",
            joinColumns = @JoinColumn(name = "DEVICE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID"))
    @ManyToMany
    private List<Employee> employee;
    @Column(name = "ACTIVE", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active = false;
    @Column(name = "VEHICLE_PLATE_NUMBER", nullable = false)
    @NotNull
    private String vehicle_plate_number;
    @Column(name = "DEVICE_NAME", nullable = false)
    @NotNull
    private String device_name;

    public void setEmployee(List<Employee> employee) {
        this.employee = employee;
    }

    public List<Employee> getEmployee() {
        return employee;
    }

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}