package com.pyeon.domain.post.service;

import com.pyeon.domain.image.service.ImageService;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.member.facade.MemberFacade;
import com.pyeon.domain.post.dao.PostRepository;
import com.pyeon.domain.post.domain.enums.MainCategory;
import com.pyeon.domain.post.domain.enums.SubCategory;
import com.pyeon.domain.post.domain.Post;
import com.pyeon.domain.post.dto.request.PostCreateRequest;
import com.pyeon.domain.post.dto.request.PostUpdateRequest;
import com.pyeon.domain.post.dto.response.PostResponse;
import com.pyeon.domain.post.dto.response.PostSummaryResponse;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static com.pyeon.domain.post.dao.PostSpecification.isPopular;
import static com.pyeon.domain.post.dao.PostSpecification.withMainCategory;
import static com.pyeon.domain.post.dao.PostSpecification.withSubCategory;
import static com.pyeon.domain.post.dao.PostSpecification.containsText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final MemberFacade memberFacade;
    private final StringRedisTemplate redisTemplate;
    private final ImageService imageService;

    private static final String LIKE_KEY_PREFIX = "post:like:";

    @Override
    @Transactional
    public Long createPost(PostCreateRequest request, Long memberId) {
        Member member = memberFacade.getMemberById(memberId);
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .mainCategory(request.getMainCategory())
                .subCategory(request.getSubCategory())
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
            MainCategory mainCategory, 
            SubCategory subCategory,
            String searchText,
            boolean onlyPopular, 
            Pageable pageable
    ) {
        Specification<Post> spec = Specification.where(null);
        
        if (mainCategory != null) {
            spec = spec.and(withMainCategory(mainCategory));
        }
        
        if (subCategory != null) {
            spec = spec.and(withSubCategory(subCategory));
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
        post.update(
                request.getTitle(),
                request.getContent(),
                request.getMainCategory(),
                request.getSubCategory()
        );
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = findPostById(postId);

        try {
            String key = LIKE_KEY_PREFIX + postId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED, "게시글 좋아요 정보 삭제 실패: " + e.getMessage());
        }
        
        deleteImagesInContent(post.getContent());
        
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
        Member member = memberFacade.getMemberById(memberId);
        Specification<Post> spec = Specification.where((root, query, builder) ->
            builder.equal(root.get("member"), member)
        );
        
        return postRepository.findAll(spec, pageable)
                .map(PostResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPostsSummary(
            MainCategory mainCategory, 
            SubCategory subCategory,
            String searchText,
            boolean onlyPopular, 
            Pageable pageable
    ) {
        Specification<Post> spec = Specification.where(null);
        
        if (mainCategory != null) {
            spec = spec.and(withMainCategory(mainCategory));
        }
        
        if (subCategory != null) {
            spec = spec.and(withSubCategory(subCategory));
        }
        
        if (StringUtils.hasText(searchText)) {
            spec = spec.and(containsText(searchText));
        }
        
        if (onlyPopular) {
            spec = spec.and(isPopular());
        }
                
        return postRepository.findAll(spec, pageable)
                .map(PostSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPostsSummaryByMemberId(Long memberId, Pageable pageable) {
        Member member = memberFacade.getMemberById(memberId);
        Specification<Post> spec = Specification.where((root, query, builder) ->
            builder.equal(root.get("member"), member)
        );
        
        return postRepository.findAll(spec, pageable)
                .map(PostSummaryResponse::from);
    }

    @Transactional
    public void incrementViewCount(Long postId) {
        Post post = findPostById(postId);
        post.incrementViewCount();
        postRepository.save(post);
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

    private void deleteImagesInContent(String content) {
        Pattern pattern = Pattern.compile("https://[^\\s\"']+\\.amazonaws\\.com/[^\\s\"']+");
        Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            String imageUrl = matcher.group();
            String fileName = extractFileNameFromUrl(imageUrl);
            if (fileName != null) {
                try {
                    imageService.deleteImage(fileName);
                } catch (Exception e) {
                    log.warn("이미지 삭제 실패: {}", fileName, e);
                }
            }
        }
    }

    private String extractFileNameFromUrl(String url) {
        try {
            String[] parts = url.split("/");
            return parts[parts.length - 1];
        } catch (Exception e) {
            log.warn("이미지 URL 파싱 실패: {}", url, e);
            return null;
        }
    }
}
