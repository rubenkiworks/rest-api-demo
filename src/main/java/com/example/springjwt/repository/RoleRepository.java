package com.example.springjwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springjwt.models.ERol;
import com.example.springjwt.models.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByNombre(ERol nombre);
}
