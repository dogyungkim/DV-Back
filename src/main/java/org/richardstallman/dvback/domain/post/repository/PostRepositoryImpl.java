package org.richardstallman.dvback.domain.post.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.post.converter.PostConverter;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

  private final PostConverter postConverter;
  private final PostJpaRepository postJpaRepository;

  @Override
  public PostDomain save(PostDomain postDomain) {
    return postConverter.fromEntityToDomain(
        postJpaRepository.save(postConverter.fromDomainToEntity(postDomain)));
  }

  @Override
  public List<PostDomain> findByAuthorId(Long authorId) {
    return postJpaRepository.findByAuthorIdOrderByPostIdDesc(authorId).stream()
        .map(postConverter::fromEntityToDomain)
        .toList();
  }
}
