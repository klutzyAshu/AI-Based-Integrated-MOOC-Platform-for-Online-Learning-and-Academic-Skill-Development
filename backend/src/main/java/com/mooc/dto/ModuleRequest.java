package com.mooc.dto;

import jakarta.validation.constraints.NotBlank;

public record ModuleRequest(@NotBlank String title, Integer order) {}
