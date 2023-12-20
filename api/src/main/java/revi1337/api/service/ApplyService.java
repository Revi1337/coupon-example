package revi1337.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.api.domain.Coupon;
import revi1337.api.repository.CouponCountRepository;
import revi1337.api.repository.CouponRepository;

@Service
@RequiredArgsConstructor
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;

    public void apply(Long userId) {
        Long count = couponCountRepository.increment();
        if (count > 100) {
            return;
        }
        couponRepository.save(new Coupon(userId));
    }
}
