package org.richardstallman.dvback.domain.coupon.repository;

import org.richardstallman.dvback.domain.coupon.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponJpaRepository extends JpaRepository<CouponEntity, Long> {}
