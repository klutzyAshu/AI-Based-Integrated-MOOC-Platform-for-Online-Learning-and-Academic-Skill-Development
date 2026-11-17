package com.mooc.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "certificate_code", nullable = false, unique = true, length = 40)
    private String certificateCode;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "issued_at", updatable = false)
    private LocalDateTime issuedAt;

    public Certificate() {
    }

    public Certificate(Long id, User user, Course course, String certificateCode, String fileUrl, LocalDateTime issuedAt) {
        this.id = id;
        this.user = user;
        this.course = course;
        this.certificateCode = certificateCode;
        this.fileUrl = fileUrl;
        this.issuedAt = issuedAt;
    }

    @PrePersist
    protected void onCreate() {
        issuedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getCertificateCode() {
        return certificateCode;
    }

    public void setCertificateCode(String certificateCode) {
        this.certificateCode = certificateCode;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public static CertificateBuilder builder() {
        return new CertificateBuilder();
    }

    public static class CertificateBuilder {
        private Long id;
        private User user;
        private Course course;
        private String certificateCode;
        private String fileUrl;
        private LocalDateTime issuedAt;

        CertificateBuilder() {
        }

        public CertificateBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CertificateBuilder user(User user) {
            this.user = user;
            return this;
        }

        public CertificateBuilder course(Course course) {
            this.course = course;
            return this;
        }

        public CertificateBuilder certificateCode(String certificateCode) {
            this.certificateCode = certificateCode;
            return this;
        }

        public CertificateBuilder fileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        public CertificateBuilder issuedAt(LocalDateTime issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public Certificate build() {
            return new Certificate(id, user, course, certificateCode, fileUrl, issuedAt);
        }
    }
}
