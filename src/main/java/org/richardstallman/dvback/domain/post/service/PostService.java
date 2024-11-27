package org.richardstallman.dvback.domain.post.service;

import java.util.List;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.richardstallman.dvback.domain.post.domain.request.PostCreateRequestDto;
import org.richardstallman.dvback.domain.post.domain.response.PostCreateResponseDto;

public interface PostService {

  PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId);

  List<PostCreateResponseDto> getPostsByUserId(Long userId);

  PostDomain getPost(Long postId);
}
