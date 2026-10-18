package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.dto.request.RegisterDTO;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.repository.LaboratoryRepository;
import com.bacteria_resistant.project.util.CnpjUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final LaboratoryRepository repository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(LaboratoryRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterDTO data) {
        String taxId = CnpjUtils.normalize(data.taxId());


        if (repository.findByTaxId(taxId) != null) {
            throw new IllegalArgumentException("CNPJ já cadastrado.");
        }

        if (repository.findByEmail(data.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        Laboratory newLaboratory = new Laboratory(
                taxId,
                data.email(),
                passwordEncoder.encode(data.password()),
                data.name()
        );

        repository.save(newLaboratory);
    }
}