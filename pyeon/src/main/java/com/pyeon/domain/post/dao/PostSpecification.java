package com.pyeon.domain.post.dao;

import com.pyeon.domain.post.domain.enums.MainCategory;
import com.pyeon.domain.post.domain.enums.SubCategory;
import com.pyeon.domain.post.domain.Post;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PostSpecification {
    
    public static Specification<Post> withMainCategory(MainCategory mainCategory) {
        return (root, query, cb) -> {
            if (mainCategory == null) return null;
            return cb.equal(root.get("mainCategory"), mainCategory);
        };
    }
    
    public static Specification<Post> withSubCategory(SubCategory subCategory) {
        return (root, query, cb) -> {
            if (subCategory == null) return null;
            return cb.equal(root.get("subCategory"), subCategory);
        };
    }
    
    public static Specification<Post> isPopular() {
        return (root, query, cb) -> 
            cb.greaterThanOrEqualTo(root.get("likeCount"), 10);
    }
    
    public static Specification<Post> containsText(String searchText) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(searchText)) return null;
            String contains = "%" + searchText + "%";
            return cb.or(
                cb.like(root.get("title"), contains),
                cb.like(root.get("content"), contains)
            );
        };
    }
}