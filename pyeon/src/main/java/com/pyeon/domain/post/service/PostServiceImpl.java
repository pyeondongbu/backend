package com.pyeon.domain.post.service;

import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.post.dao.PostRepository;
import com.pyeon.domain.post.domain.Category;
import com.pyeon.domain.post.domain.Post;
import com.pyeon.domain.post.dto.request.PostCreateRequest;
import com.pyeon.domain.post.dto.request.PostUpdateRequest;
import com.pyeon.domain.post.dto.response.PostResponse;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static com.pyeon.domain.post.dao.PostSpecification.isPopular;
import static com.pyeon.domain.post.dao.PostSpecification.withCategory;
import static com.pyeon.domain.post.dao.PostSpecification.containsText;

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
    public Long createPost(PostCreateRequest request, Long memberId) {
        Member member = findMemberById(memberId);
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .member(member)
                .build();
        return postRepository.save(post).getId();
    }

    @Override
    @Transactional
    public PostResponse getPost(Long id, Long memberId) {
        Post post = findPostById(id);
        post.incrementViewCount();
        
        boolean hasLiked = false;
        
        if (memberId != null) {
            hasLiked = hasLiked(post.getId(), memberId);
        }
        
        return PostResponse.from(post, hasLiked);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(
            Category category, 
            String searchText,
            boolean onlyPopular, 
            Pageable pageable
    ) {
        Specification<Post> spec = Specification.where(null);
        
        if (category != null) {
            spec = spec.and(withCategory(category));
        }
        
        if (StringUtils.hasText(searchText)) {
            spec = spec.and(containsText(searchText));
        }
        
        if (onlyPopular) {
            spec = spec.and(isPopular());
        }
                
        return postRepository.findAll(spec, pageable)
                .map(PostResponse::from);
    }

    @Override
    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, Long memberId) {
        Post post = findPostById(postId);
        Member member = findMemberById(memberId);
        
        if (!post.isWriter(member)) {
            throw new CustomException(ErrorCode.NOT_POST_AUTHOR);
        }
        
        post.update(
                request.getTitle(),
                request.getContent(),
                request.getCategory()
        );
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = findPostById(postId);
        Member member = findMemberById(memberId);
        
        if (!post.isWriter(member) && !member.isAdmin()) {
            throw new CustomException(ErrorCode.NOT_POST_AUTHOR);
        }
        
        try {
            String key = LIKE_KEY_PREFIX + postId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED);
        }
        
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void likePost(Long postId, Long memberId) {
        Post post = findPostById(postId);
        String key = LIKE_KEY_PREFIX + postId;
        
        try {
            Boolean hasLiked = redisTemplate.opsForSet().isMember(key, String.valueOf(memberId));
            
            if (Boolean.TRUE.equals(hasLiked)) {
                redisTemplate.opsForSet().remove(key, String.valueOf(memberId));
                post.unlike();
            } else {
                redisTemplate.opsForSet().add(key, String.valueOf(memberId));
                post.like();
            }
            
            redisTemplate.persist(key);
            postRepository.save(post);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByMemberId(Long memberId, Pageable pageable) {
        Member member = findMemberById(memberId);
        Specification<Post> spec = Specification.where((root, query, builder) ->
            builder.equal(root.get("member"), member)
        );
        
        return postRepository.findAll(spec, pageable)
                .map(PostResponse::from);
    }

    /**
     * Private 함수들
     */

    private boolean hasLiked(Long postId, Long memberId) {
        String key = LIKE_KEY_PREFIX + postId;
        try {
            return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(key, String.valueOf(memberId))
            );
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED);
        }
    }

    private Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
