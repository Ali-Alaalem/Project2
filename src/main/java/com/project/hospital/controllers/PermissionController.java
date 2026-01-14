package com.project.hospital.controllers;

import com.project.hospital.models.Permission;
import com.project.hospital.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('permission:create')")
    public Permission createPermission(@RequestBody Permission permission) {
        return permissionService.createPermission(permission);
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('permission:view')")
    public List<Permission> getAllPermissions() {
        return permissionService.findAllPermissions();
    }

    @GetMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('permission:view')")
    public Permission getPermissionById(@PathVariable("permissionId") Long permissionId) {
        return permissionService.findPermissionById(permissionId);
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public Permission deletePermissionById(@PathVariable("permissionId") Long permissionId) {
        return permissionService.deletePermission(permissionId);
    }

    @PutMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('permission:update')")
    public Permission updatePermission(@PathVariable("permissionId") Long permissionId, @RequestBody Permission permission) {
        return permissionService.updatePermission(permissionId, permission);
    }

}
