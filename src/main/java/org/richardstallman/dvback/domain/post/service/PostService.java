package org.richardstallman.dvback.domain.post.service;

import org.richardstallman.dvback.domain.post.domain.request.PostCreateRequestDto;
import org.richardstallman.dvback.domain.post.domain.response.PostCreateResponseDto;

public interface PostService {

  PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId);
}
