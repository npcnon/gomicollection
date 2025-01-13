package com.company.gomicollection.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum CoordinatePosition implements EnumClass<String> {

    START("A"),
    NORMAL("B"),
    END("C");

    private final String id;

    CoordinatePosition(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CoordinatePosition fromId(String id) {
        for (CoordinatePosition at : CoordinatePosition.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}