package org.richardstallman.dvback.domain.post.repository;

import java.util.List;
import org.richardstallman.dvback.domain.post.domain.PostDomain;

public interface PostRepository {

  PostDomain save(PostDomain postDomain);

  List<PostDomain> findByAuthorId(Long authorId);
}
