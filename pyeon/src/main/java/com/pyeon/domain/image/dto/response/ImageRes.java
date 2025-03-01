package com.pyeon.domain.image.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageRes {

    private String imageUrl;

    public static ImageRes of(final String imageUrl) {
        return new ImageRes(imageUrl);
    }
} 