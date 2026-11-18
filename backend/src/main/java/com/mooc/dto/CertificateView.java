package com.mooc.dto;

import java.time.LocalDateTime;

public record CertificateView(Long id, String learnerName, String courseTitle,
                              String certificateCode, LocalDateTime issuedAt) {}
