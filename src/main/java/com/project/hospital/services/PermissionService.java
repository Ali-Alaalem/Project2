package com.project.hospital.services;


import com.project.hospital.exceptions.InformationExistException;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Permission;
import com.project.hospital.repositorys.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }


    @Transactional
    public Permission createPermission(Permission permission) {
        if (permission.getAction() != null && permissionRepository.existsByAction(permission.getAction()))
            throw new InformationExistException("Permission already exists");

        return permissionRepository.save(permission);

    }


    public List<Permission> findAllPermissions() {
        return permissionRepository.findAll();
    }

    public Permission findPermissionByAction(String action) {
        return permissionRepository.findByAction(action).orElseThrow(
                () -> new InformationNotFoundException("Permission does not exist")
        );
    }


    public Permission findPermissionById(Long permissionId) {
        return permissionRepository.findById(permissionId).orElseThrow(
                () -> new InformationNotFoundException("Permission does not exist")
        );
    }

    @Transactional
    public Permission deletePermission(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new InformationNotFoundException("Permission does not exist"));
        permissionRepository.delete(permission);
        return permission;
    }


    @Transactional
    public Permission updatePermission(Long permissionId, Permission permission) {
        Permission existingPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new InformationNotFoundException("Permission does not exist"));

        String newAction = permission.getAction();
        String currentAction = existingPermission.getAction();

        if (newAction != null && !newAction.equals(currentAction)) {
            if (permissionRepository.existsByAction(newAction)) {
                throw new InformationExistException("Permission with action '" + newAction + "' already exists");
            }
            existingPermission.setAction(newAction);
        }

       return  permissionRepository.save(existingPermission);
    }

}
