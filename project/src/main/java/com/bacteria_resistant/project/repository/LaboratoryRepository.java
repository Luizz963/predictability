package com.bacteria_resistant.project.repository;

import com.bacteria_resistant.project.model.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
    UserDetails findByTaxId(String taxId);
}