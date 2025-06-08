package com.agh.zlotowka.dto;

import lombok.Builder;

@Builder
public record EmailDTO(String to, String subject, String body) {}
