package com.consiliuminc.sras.service.sqlserver;


import com.consiliuminc.sras.entities.sqlserver.Role;
import com.consiliuminc.sras.repository.sqlserver.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {


    private RoleRepository roleRepository;


    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }


    public Role save(Role role) {
        return this.roleRepository.save(role);
    }

    public void delete(Role role) {
        this.roleRepository.delete(role);
    }
}
