package com.mooc.controller;

import com.mooc.dto.CertificateView;
import com.mooc.security.CustomUserDetailsService.AppUserPrincipal;
import com.mooc.service.CertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {
    private final CertificateService certificateService;
    public CertificateController(CertificateService certificateService) { this.certificateService = certificateService; }

    @GetMapping("/me")
    public ResponseEntity<List<CertificateView>> mine(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(certificateService.listForUser(principal.getUser()));
    }

    @GetMapping("/me/courses/{courseId}")
    public ResponseEntity<CertificateView> byCourse(@PathVariable Long courseId,
                                                     @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(certificateService.findForUser(principal.getUser(), courseId));
    }
}
