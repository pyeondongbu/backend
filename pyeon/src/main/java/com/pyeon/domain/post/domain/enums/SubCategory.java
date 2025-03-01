package com.pyeon.domain.post.domain.enums;

import com.pyeon.domain.post.domain.enums.MainCategory;
import lombok.Getter;

@Getter
public enum SubCategory {
    // 구인 ＆ 구직 서브 카테고리
    EDITOR("편집자", MainCategory.RECRUITMENT, MainCategory.JOB_SEEKING),
    THUMBNAILER("썸네일러", MainCategory.RECRUITMENT, MainCategory.JOB_SEEKING),
    OTHER("기타", MainCategory.RECRUITMENT, MainCategory.JOB_SEEKING),

    // 커뮤니티 서브 카테고리
    ALL("전체", MainCategory.COMMUNITY),
    FREE("자유", MainCategory.COMMUNITY),
    QUESTION("질문", MainCategory.COMMUNITY),
    INFORMATION("정보", MainCategory.COMMUNITY);

    private final String value;
    private final MainCategory[] mainCategories;

    SubCategory(String value, MainCategory... mainCategories) {
        this.value = value;
        this.mainCategories = mainCategories;
    }

    public boolean belongsTo(MainCategory mainCategory) {
        for (MainCategory category : mainCategories) {
            if (category == mainCategory) {
                return true;
            }
        }
        return false;
    }
} 