package org.richardstallman.dvback.domain.post.repository;

import java.util.List;
import java.util.Optional;
import org.richardstallman.dvback.domain.post.domain.PostDomain;

public interface PostRepository {

  PostDomain save(PostDomain postDomain);

  List<PostDomain> findByAuthorId(Long authorId);

  Optional<PostDomain> findByPostId(Long postId);
}
