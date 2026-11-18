package com.mooc.service;

import com.mooc.dto.CertificateView;
import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.Certificate;
import com.mooc.model.Course;
import com.mooc.model.User;
import com.mooc.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CertificateService {
    private final CertificateRepository certificateRepository;
    public CertificateService(CertificateRepository certificateRepository) { this.certificateRepository = certificateRepository; }

    @Transactional
    public Certificate issue(User user, Course course) {
        return certificateRepository.findByUserIdAndCourseId(user.getId(), course.getId())
                .orElseGet(() -> certificateRepository.save(Certificate.builder().user(user).course(course)
                        .certificateCode(UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase())
                        .build()));
    }

    @Transactional(readOnly = true)
    public CertificateView findForUser(User user, Long courseId) {
        Certificate certificate = certificateRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        return view(certificate);
    }

    @Transactional(readOnly = true)
    public List<CertificateView> listForUser(User user) {
        return certificateRepository.findByUserIdOrderByIssuedAtDesc(user.getId()).stream().map(this::view).toList();
    }

    private CertificateView view(Certificate certificate) {
        return new CertificateView(certificate.getId(), certificate.getUser().getName(),
                certificate.getCourse().getTitle(), certificate.getCertificateCode(), certificate.getIssuedAt());
    }
}
