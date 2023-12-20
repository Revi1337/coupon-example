package revi1337.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.api.domain.Coupon;
import revi1337.api.producer.CouponCreateProducer;
import revi1337.api.repository.CouponCountRepository;
import revi1337.api.repository.CouponRepository;

@Service
@RequiredArgsConstructor
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;

    public void apply(Long userId) {
        Long count = couponCountRepository.increment();
        if (count > 100) {
            return;
        }
        couponCreateProducer.create(userId);
    }
}
