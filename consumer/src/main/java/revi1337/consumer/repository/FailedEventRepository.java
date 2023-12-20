package revi1337.consumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.consumer.domain.FailedEvent;

public interface FailedEventRepository extends JpaRepository<FailedEvent, Long> {
}
