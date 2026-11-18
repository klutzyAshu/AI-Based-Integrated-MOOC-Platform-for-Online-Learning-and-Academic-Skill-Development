package com.mooc.controller;

import com.mooc.model.User;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> me(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(principal.getUser());
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMe(@AuthenticationPrincipal AppUserPrincipal principal,
                                          @RequestParam String name) {
        return ResponseEntity.ok(userService.updateProfile(principal.getUser().getId(), name));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> all() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> byId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> changeRole(@PathVariable Long id, @RequestParam User.Role role,
                                           @AuthenticationPrincipal AppUserPrincipal principal) {
        preventSelfLockout(id, principal);
        return ResponseEntity.ok(userService.changeRole(id, role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id,
                                            @AuthenticationPrincipal AppUserPrincipal principal) {
        preventSelfLockout(id, principal);
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    private void preventSelfLockout(Long targetId, AppUserPrincipal principal) {
        if (principal != null && principal.getUser().getId().equals(targetId)) {
            throw new org.springframework.security.access.AccessDeniedException("You cannot change or deactivate your own administrator account");
        }
    }
}
