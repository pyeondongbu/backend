package com.pyeon.domain.post.dao;

import com.pyeon.domain.post.domain.Category;
import com.pyeon.domain.post.domain.Post;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PostSpecification {
    
    public static Specification<Post> withCategory(Category category) {
        return (root, query, cb) -> {
            if (category == null) return null;
            return cb.equal(root.get("category"), category);
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