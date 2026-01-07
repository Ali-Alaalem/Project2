package com.project.hospital.controllers;

import com.project.hospital.models.Permission;
import com.project.hospital.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Permission createPermission(@RequestBody Permission permission) {
        return permissionService.createPermission(permission);
    }

    @GetMapping("/")
    public List<Permission> getAllPermissions() {
        return permissionService.findAllPermissions();
    }

    @GetMapping("/{permissionId}")
    public Permission getPermissionById(@PathVariable("permissionId") Long permissionId) {
        return permissionService.findPermissionById(permissionId);
    }

    @DeleteMapping("/{permissionId}")
    public Permission deletePermissionById(@PathVariable("permissionId") Long permissionId) {
        return permissionService.deletePermission(permissionId);
    }

    @PostMapping("/{permissionId}")
    public Permission updatePermission(@PathVariable("permissionId") Long permissionId, @RequestBody Permission permission) {
        return permissionService.updatePermission(permissionId, permission);
    }

}
