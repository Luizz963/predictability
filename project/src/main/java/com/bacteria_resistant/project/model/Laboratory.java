package com.bacteria_resistant.project.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_laboratories")
public class Laboratory implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String taxId;

    @Column(nullable = false)
    private String password;


    @Column(nullable = false)
    private String name;

    public Laboratory() {}

    // Construtor atualizado para incluir o nome
    public Laboratory(String taxId, String password, String name) {
        this.taxId = taxId;
        this.password = password;
        this.name = name;
    }

    // Métodos do UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.taxId;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // Getters
    public Long getId() { return id; }
    public String getTaxId() { return taxId; }
    public String getName() { return name; }
}