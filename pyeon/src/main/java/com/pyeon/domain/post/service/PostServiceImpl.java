package com.pyeon.domain.post.service;

import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.post.dao.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String LIKE_KEY_PREFIX = "post:like:";

    @Override
    @Transactional
    public Long createPost(PostCreateRequest request, String email) {
        Member member = findMemberByEmail(email);
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build();
        return postRepository.save(post).getId();
    }

    @Override
    public PostResponse getPost(Long id) {
        Post post = findPostById(id);
        post.incrementViewCount();
        return PostResponse.from(post);
    }

    @Override
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(PostResponse::from);
    }

    @Override
    @Transactional
    public void updatePost(Long id, PostUpdateRequest request, String email) {
        Post post = findPostById(id);
        validateAuthor(post, email);
        post.update(request.getTitle(), request.getContent());
    }

    @Override
    @Transactional
    public void deletePost(Long id, String email) {
        Post post = findPostById(id);
        validateAuthor(post, email);
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void likePost(Long postId, String email) {
        String key = LIKE_KEY_PREFIX + postId;
        Boolean isFirstLike = redisTemplate.opsForSet().add(key, email);
        
        if (Boolean.TRUE.equals(isFirstLike)) {
            Post post = findPostById(postId);
            post.like();
        } else {
            throw new CustomException(ErrorCode.ALREADY_LIKED_POST);
        }
    }

    @Override
    public boolean hasLiked(Long postId, String email) {
        String key = LIKE_KEY_PREFIX + postId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, email));
    }

    private Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateAuthor(Post post, String email) {
        if (!post.isAuthor(email)) {
            throw new CustomException(ErrorCode.NOT_POST_AUTHOR);
        }
    }
}
