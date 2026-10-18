package com.bacteria_resistant.project.service.auth;

import com.bacteria_resistant.project.repository.LaboratoryRepository;
import com.bacteria_resistant.project.util.CnpjUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private LaboratoryRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = repository.findByTaxId(CnpjUtils.normalize(username));
        if (user == null) {
            throw new UsernameNotFoundException("Laboratório não encontrado para o CNPJ informado.");
        }
        return user;
    }
}