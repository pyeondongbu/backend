package com.pyeon.domain.post.service;

import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.post.dao.CommentRepository;
import com.pyeon.domain.post.dao.PostRepository;
import com.pyeon.domain.post.domain.Comment;
import com.pyeon.domain.post.domain.Post;
import com.pyeon.domain.post.dto.request.CommentCreateRequest;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long createComment(Long postId, CommentCreateRequest request, Long memberId) {
        Post post = findPostById(postId);
        Member member = findMemberById(memberId);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .member(member)
                .post(post)
                .build();

        return commentRepository.save(comment).getId();
    }

    @Override
    @Transactional
    public void updateComment(Long commentId, CommentCreateRequest request, Long memberId) {
        Comment comment = findCommentById(commentId);
        Member member = findMemberById(memberId);
        
        if (!comment.isWriter(member)) {
            throw new CustomException(ErrorCode.NOT_COMMENT_AUTHOR);
        }
        
        comment.update(request.getContent());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = findCommentById(commentId);
        Member member = findMemberById(memberId);
        
        if (!comment.isWriter(member)) {
            throw new CustomException(ErrorCode.NOT_COMMENT_AUTHOR);
        }
        
        commentRepository.delete(comment);
    }

    /**
     * Private 함수들
     */
    private Comment findCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
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