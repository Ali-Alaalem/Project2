package com.project.hospital.controllers;

import com.project.hospital.models.Permission;
import com.project.hospital.models.Role;
import com.project.hospital.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/auth/roles")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('role:create')")
    public Role createRole(@RequestBody Role role) {
        return roleService.createRole(role);
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('role:view')")
    public List<Role> getAllRoles() {
        return roleService.findAllRoles();
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("hasAuthority('role:view')")
    public Role getRoleById(@PathVariable("roleId") Long roleId) {
        return roleService.findRoleById(roleId);
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('role:delete')")
    public Role deleteRoleById(@PathVariable("roleId") Long roleId) {
        return roleService.deleteRoleById(roleId);
    }

    @PostMapping("/{roleId}")
    @PreAuthorize("hasAuthority('role:update')")
    public Role updateRole(@PathVariable("roleId") Long roleId, @RequestBody Role role) {
        return roleService.updateRole(roleId, role);
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    public Role updateRolePermissions(@PathVariable("roleId") Long roleId, @RequestBody Set<Permission> permissions) {
        return roleService.updateRolePermissions(roleId, permissions);
    }

    @DeleteMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    public Role removeRolePermission(@PathVariable("roleId") Long roleId, @RequestBody Permission permission) {
        return roleService.removeRolePermission(roleId, permission);
    }
}