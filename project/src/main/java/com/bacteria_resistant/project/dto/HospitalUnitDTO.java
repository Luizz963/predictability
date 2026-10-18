package com.bacteria_resistant.project.dto;

public record HospitalUnitDTO(
        Long id,
        String name,
        Integer bedCount,
        Boolean active) {}