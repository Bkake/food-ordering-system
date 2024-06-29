package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;

    public PayOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                         OrderServiceConfigData orderServiceConfigData,
                                         KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
    }


    @Override
    public void publish(OrderPaidEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();

        try {
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
                    orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(domainEvent);

            var topicName = orderServiceConfigData.getRestaurantApprovalRequestTopicName();

            kafkaProducer.send(topicName, orderId, restaurantApprovalRequestAvroModel) .whenComplete((result, throwable) -> {
                if (throwable == null) {
                    handleSuccess(result.getRecordMetadata(), orderId);
                }
                else {
                    handleFailure(restaurantApprovalRequestAvroModel,
                            topicName, throwable);
                }
            });

            log.info("RestaurantApprovalRequestAvroModel sent to kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalRequestAvroModel message" +
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }

    private void handleSuccess(RecordMetadata recordMetadata, String orderId) {
        log.info("Received successful response from kafka for Topic:{}, Key:{}, Partition:{}, Offset:{}",
                recordMetadata.topic(),
                orderId,
                recordMetadata.partition(),
                recordMetadata.offset());
    }

    private void handleFailure(RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel,
                               String paymentRequestTopicName,
                               Throwable throwable) {
        log.error("Error while sending restaurantApprovalRequestAvroModel  message {} to topic {} ",
                restaurantApprovalRequestAvroModel.toString(), paymentRequestTopicName, throwable);
    }
}
