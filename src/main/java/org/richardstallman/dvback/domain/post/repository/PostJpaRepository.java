package org.richardstallman.dvback.domain.post.repository;

import java.util.List;
import org.richardstallman.dvback.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {

  List<PostEntity> findByAuthorUserIdOrderByPostIdDesc(Long authorId);
}
