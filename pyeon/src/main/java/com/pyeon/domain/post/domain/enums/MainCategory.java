package com.pyeon.domain.post.domain.enums;

import lombok.Getter;

@Getter
public enum MainCategory {
    RECRUITMENT("구인"),
    JOB_SEEKING("구직"),
    COMMUNITY("커뮤니티");

    private final String value;

    MainCategory(String value) {
        this.value = value;
    }
} 