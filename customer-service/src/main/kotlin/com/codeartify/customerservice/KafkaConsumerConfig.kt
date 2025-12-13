package com.codeartify.customerservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.GenericEventMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.stereotype.Component

@Configuration
@EnableKafka
class KafkaConsumerConfig {

    @Autowired
    fun configureEventProcessing(eventProcessingConfigurer: EventProcessingConfigurer) {
        eventProcessingConfigurer.registerSubscribingEventProcessor("customer-order-processing")
    }

    @Bean
    fun consumerFactory(
        @Value("\${axon.kafka.bootstrap-servers}") bootstrapServers: String
    ): ConsumerFactory<String, ByteArray> {
        val props = mapOf<String, Any>(
            "bootstrap.servers" to bootstrapServers,
            "group.id" to "customer-service-group-v2",  // New group to start fresh
            "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer" to "org.apache.kafka.common.serialization.ByteArrayDeserializer",
            "auto.offset.reset" to "latest",
            "enable.auto.commit" to "true",
            "auto.commit.interval.ms" to "1000"
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, ByteArray>
    ): ConcurrentKafkaListenerContainerFactory<String, ByteArray> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, ByteArray>()
        factory.consumerFactory = consumerFactory
        factory.containerProperties.ackMode = org.springframework.kafka.listener.ContainerProperties.AckMode.RECORD
        return factory
    }
}

@Component
class KafkaEventListener(
    private val eventBus: EventBus,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["axon.events"], groupId = "customer-service-group-v2")
    fun listen(message: ByteArray) {
        try {
            log.info("Received raw Kafka message, size: {} bytes", message.size)

            // Parse directly as OrderPlacedEvent
            val event = objectMapper.readValue(message, OrderPlacedEvent::class.java)

            log.info("========================================")
            log.info("Customer service received OrderPlacedEvent from Kafka: {}", event)
            log.info("Order ID: {}, Customer ID: {}, Amount: {}", event.orderId, event.customerId, event.amount)
            log.info("========================================")

            // Publish to Axon event bus so handlers can process it
            eventBus.publish(GenericEventMessage.asEventMessage<OrderPlacedEvent>(event))
        } catch (e: Exception) {
            log.error("Failed to process Kafka message", e)
        }
    }
}
