package revi1337.consumer.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import revi1337.consumer.domain.Coupon;
import revi1337.consumer.repository.CouponRepository;

/**
 * Topic 에 전송된 데이터를 가져오기위한 클래스 작성
 */
@Component
@RequiredArgsConstructor
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        couponRepository.save(new Coupon(userId));
    }
}
