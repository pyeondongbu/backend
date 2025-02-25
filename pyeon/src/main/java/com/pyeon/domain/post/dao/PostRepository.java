package com.pyeon.domain.post.dao;

import com.pyeon.domain.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

}
