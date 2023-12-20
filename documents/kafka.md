# Kafka

카프카란? `분산 이벤트 스트리밍 플랫폼` 이다. 여기서 이벤트 스트리밍이란 소스(`producer`)에서 목적지(`consumer`)까지 이벤트를 실시간으로 스트리밍하는 것을 의미한다.

카프카의 기본적인 구조는 Producer, Topic, Consumer 로 이루어져있는데, Queue 라고 생각하면 편하다.

![img.png](img.png)

Topic 에 데이터를 삽입할 수 있는 기능을 가진것이 `Producer` 이며, 반대로 Topic 에 삽입된 데이터를
가져갈 수 있는 것이 `Producer` 이다. 그래서 Kafka 는 소스(Producer) 에서 목적지(Consumer) 까지 데이터를 
실시간으로 스트리밍할 수 있도록 도와주는 플랫폼이라고 불리우는 것이다.

# Docker 로 kafka 테스트

## Docker 로 kafaka 셋팅

토픽 생성

```bash
$ docker exec -it kakfa kafka-topics.sh --bootstrap-server localhost:9092 --create --topic testTopic
```

프로듀서 실행 (> 가 뜨면 정상)

```bash
$ docker exec -it kakfa kafka-console-producer.sh --topic testTopic --broker-list 0.0.0.0:9092
```

컨슈머 실행 (아무것도 안뜨고 대기하면 정상)

```bash
$ docker exec -it kakfa kafka-console-consumer.sh --topic testTopic --bootstrap-server localhost:9092
```

producer 프롬프트에서 값을 보내면, consumer 프롬프트에서 값을 받을 수 있다. 





# Spring + Docker 로 kafka 테스트

## Spring 에서 kafaka 셋팅 (Producer 셋팅)

```java
@Configuration
public class KafkaProducerConfig {

    /**
     * Producer 인스턴스를 생성하는데 필요한 설정을 설정.
     * @return
     */
    @Bean
    public ProducerFactory<String, Long> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * Topic 에 데이터를 전송하기 위해 사용할 KafkaTemplate 를 생성
     */
    @Bean
    public KafkaTemplate<String, Long> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
```

### Topic 생성

Topic 생성

```bash
$ docker exec -it kakfa kafka-topics.sh --bootstrap-server localhost:9092 --create --topic coupon_create
``` 

### Consumer 실행

Topic 의 들어오는 데이터를 받아볼 수 있는 Consumer 를 실행

```bash
$ docker exec -it kakfa kafka-console-consumer.sh --topic coupon_create --bootstrap-server localhost:9092 --key-deserializer "org.apache.kafka.common.serialization.StringDeserializer" --value-deserializer "org.apache.kafka.common.serialization.LongDeserializer"
```

### 애플리케이션을 실행해서 Topic 에 데이터 전송

애플리케이션을 실행하면 Spring 에서 데이터가 Topic 으로 전송되고, Consumer 프롬프트에서
데이터가 들어오는 것을 확인할 수 있음.


## Spring 에서 kafaka 셋팅 (Consumer 셋팅)

### Consumer 모듈 생성

여기서는 멀티모듈로 진행되기 때문에 spring-kafka, jpa, mysql 의존성을 추가하고 모듈 생성해준다.

### yml 복사

이전 Procuder 모듈에서 사용한 yml 을 그대로 복붙해준다.

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:9004/coupon_example
    username: root
    password: 1234

  data:
    redis:
      host: host.docker.internal
      port: 6379

  jpa:
    hibernate:
      ddl-auto: create

logging:
  level:
    sql: debug
```

### Consumer 설정파일 작성

```java
@Configuration
public class KafkaConsumerConfig {

    /**
     * Consumer 인스턴스를 생성하는데 필요한 설정을 설정
     */
    @Bean
    public ConsumerFactory<String, Long> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    /**
     * Topic 으로부터 메시지를 전달받기 위한 KafkaListenerContainerFactory 를 생성
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Long> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
```

### Topic 에 전송된 데이터를 가져오기위한 Consumer 클래스 작성

```java
/**
 * Topic 에 전송된 데이터를 가져오기위한 클래스 작성
 */
@Component
public class CouponCreatedConsumer {

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        System.out.println(userId);
    }
}
```

### Consumer 모듈 실행

Consumer 모듈을 실행하면, Consumer 설정파일에 작성한내용을 토대로 
Topic 에 들어오는 데이터를 들어오길 Listen 한다.

### Producer 모듈 실행

이제 Producer 모듈을 실행해주면 해당 모듈에서 Topic 으로 데이터를 전송하고
이를 Listen 하고 있던 Consumer 모듈이 동작하게 된다.

### Consumer 로 받은 데이터를 변경

Consumer 로 받은 Topic 의 데이터를 변경하려면 Consumer 클래스에서 레포지토리를 주입받아
그냥 사용해주면된다.

```java
@Component
@RequiredArgsConstructor
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        couponRepository.save(new Coupon(userId));
    }
}
```
