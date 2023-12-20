package revi1337.api.service;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import revi1337.api.repository.CouponCountRepository;
import revi1337.api.repository.CouponRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplyServiceTest {

    @Autowired ApplyService applyService;
    @Autowired CouponRepository couponRepository;
    @Autowired CouponCountRepository couponCountRepository;

    @BeforeEach
    void beforeEach() {
        couponCountRepository.clear();
    }
    
    @Test
    public void 한번만응모() {
        applyService.apply(1L);

        long count = couponRepository.count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 여러명응모() throws InterruptedException {
        int threadCount = 500;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        IntStream.range(0, threadCount).forEach(num -> {
            long userId = num;
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        countDownLatch.await();

        Thread.sleep(10000);

        long count = couponRepository.count();
        assertThat(count).isEqualTo(100);
    }

}