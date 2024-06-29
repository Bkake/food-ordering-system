package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;

    public CancelOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                            OrderServiceConfigData orderServiceConfigData,
                                            KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
    }


    @Override
    public void publish(OrderCancelledEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCancelledEvent for order id: {}", orderId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper
                    .orderCancelledEventToPaymentRequestAvroModel(domainEvent);

            var paymentRequestTopicName = orderServiceConfigData.getPaymentResponseTopicName();

            kafkaProducer.send(paymentRequestTopicName, orderId, paymentRequestAvroModel)
                    .whenComplete((result, throwable) -> {
                        if (throwable == null) {
                            handleSuccess(result.getRecordMetadata(), orderId);
                        }
                        else {
                            handleFailure(paymentRequestAvroModel,
                                    paymentRequestTopicName, throwable);
                        }
                    });

            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", paymentRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message" +
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

    private void handleFailure(PaymentRequestAvroModel paymentRequestAvroModel,
                               String paymentRequestTopicName,
                               Throwable throwable) {
        log.error("Error while sending paymentRequestAvroModel  message {} to topic {} ",
                paymentRequestAvroModel.toString(), paymentRequestTopicName, throwable);
    }
}
