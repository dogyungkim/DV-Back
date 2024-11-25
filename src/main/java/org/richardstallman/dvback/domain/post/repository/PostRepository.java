package org.richardstallman.dvback.domain.post.repository;

import org.richardstallman.dvback.domain.post.domain.PostDomain;

public interface PostRepository {

  PostDomain save(PostDomain postDomain);
}
