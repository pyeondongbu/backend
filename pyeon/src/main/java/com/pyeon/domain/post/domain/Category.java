package com.pyeon.domain.post.domain;

import lombok.Getter;

@Getter
public enum Category {
    RECRUITMENT("구인"),
    JOB_SEEKING("구직"),
    COMMUNITY("커뮤니티");

    private final String value;

    Category(String value) {
        this.value = value;
    }
}
