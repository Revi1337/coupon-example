package revi1337.consumer.consumer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import revi1337.consumer.domain.Coupon;
import revi1337.consumer.domain.FailedEvent;
import revi1337.consumer.repository.CouponRepository;
import revi1337.consumer.repository.FailedEventRepository;

/**
 * Topic 에 전송된 데이터를 가져오기위한 클래스 작성
 */
@Component
@RequiredArgsConstructor
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;
    private final FailedEventRepository failedEventRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CouponCreatedConsumer.class);

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        try {
            couponRepository.save(new Coupon(userId));
        } catch (Exception e) {
            LOGGER.error("failed to created coupon :: " + userId);
            failedEventRepository.save(new FailedEvent(userId));
        }
    }
}
