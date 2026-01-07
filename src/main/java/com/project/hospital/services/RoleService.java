package com.project.hospital.services;


import com.project.hospital.exceptions.InformationExistException;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Permission;
import com.project.hospital.models.Role;
import com.project.hospital.repositorys.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RoleService {
    private final RoleRepository roleRepository;


    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public Role createRole(Role role){
        if(roleRepository.existsByName((role.getName()))){
            throw new InformationExistException("Role Already Exists");
        }

        return roleRepository.save(role);
    }


    public List<Role> findAllRoles(){
        return roleRepository.findAll();
    }


    public Role findRoleById(Long roleId){
        return roleRepository.findById(roleId).orElseThrow(
                ()->new InformationNotFoundException("No role exists with current id")
        );
    }


    public Role deleteRoleById(Long roleId){
        Role existingRole = roleRepository.findById(roleId).orElseThrow(
                ()->new InformationNotFoundException("Role with id does not exist")
        );

        roleRepository.delete(existingRole);
        return existingRole;
    }


    public Role updateRole(Long roleId,Role role){
        Role existingRole = roleRepository.findById(roleId).orElseThrow(
                ()->new InformationNotFoundException("Role with id does not exist")
        );

        if(role.getName()!=null){
            if(roleRepository.existsByName(role.getName())){
                throw new InformationExistException("Role already exists");
            }
            if(role.getName().equals(existingRole.getName())){
                throw new InformationExistException("Brother");
            }

            existingRole.setName(role.getName());
        }

        if(role.getPermissions()!= null){
            existingRole.setPermissions(role.getPermissions());
        }

        return roleRepository.save(existingRole);

    }

    public Role updateRolePermissions(Long roleId, Set<Permission> permissions){
        Role existingRole = roleRepository.findById(roleId).orElseThrow(
                ()->new InformationNotFoundException("Role with id does not exist")
        );

        existingRole.getPermissions().addAll(permissions);

        return roleRepository.save(existingRole);
    }
    public Role removeRolePermission(Long roleId, Permission permission){
        Role existingRole = roleRepository.findById(roleId).orElseThrow(
                ()->new InformationNotFoundException("Role with id does not exist")
        );

        existingRole.getPermissions().remove(permission);

        return roleRepository.save(existingRole);
    }



}
