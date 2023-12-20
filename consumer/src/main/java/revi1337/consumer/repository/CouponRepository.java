package revi1337.consumer.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.consumer.domain.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
