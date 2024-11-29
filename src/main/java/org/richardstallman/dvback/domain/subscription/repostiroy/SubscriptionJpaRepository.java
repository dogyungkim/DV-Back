package org.richardstallman.dvback.domain.subscription.repostiroy;

import java.util.List;
import java.util.Optional;
import org.richardstallman.dvback.domain.subscription.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, Long> {
  List<SubscriptionEntity> findByUserIdOrderBySubscriptionIdDesc(Long userId);

  Optional<SubscriptionEntity> findBySubscriptionIdAndUserId(Long subscriptionId, Long userId);

  boolean existsByUserIdAndJobJobId(Long userId, Long jobId);
}
