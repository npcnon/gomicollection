package com.company.gomicollection.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "EMPLOYEE")
@Entity
public class Employee {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;
    @NotNull
    @Column(name = "FIRST_NAME", nullable = false)
    private String first_name;
    @Column(name = "MIDDLE_NAME")
    private String middle_name;
    @Column(name = "LAST_NAME", nullable = false)
    @NotNull
    private String last_name;
    @Column(name = "AGE", nullable = false)
    @NotNull
    private Integer age;
    @Column(name = "ADDRESS", nullable = false)
    @NotNull
    private String address;
    @Column(name = "CONTACT_NUMBER", nullable = false)
    @NotNull
    private String contact_number;
    @Column(name = "EMAIL", nullable = false)
    @NotNull
    private String email;
    @JoinTable(name = "DEVICE_EMPLOYEE_LINK",
            joinColumns = @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "DEVICE_ID", referencedColumnName = "ID"))
    @ManyToMany
    private List<Device> devices;
    @Column(name = "ACTIVE", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean status) {
        this.active = status;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}