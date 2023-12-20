package revi1337.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.api.domain.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
