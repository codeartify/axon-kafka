package com.codeartify.customerservice

import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.extensions.kafka.eventhandling.consumer.AsyncFetcher
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory
import org.axonframework.extensions.kafka.eventhandling.consumer.DefaultConsumerFactory
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource
import org.axonframework.serialization.Serializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component


@Configuration
class KafkaEventConsumptionConfig {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, ByteArray> {
        val consumerConfiguration = mapOf<String, Any>(
            "bootstrap.servers" to "localhost:9092",
            "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer" to "org.apache.kafka.common.serialization.ByteArrayDeserializer",
            "enable.auto.commit" to "true",
            "auto.commit.interval.ms" to "3000"
        )
        return DefaultConsumerFactory(consumerConfiguration)
    }

    @Bean
    fun fetcher(): Fetcher<*, *, *> {
        return AsyncFetcher.builder<String, ByteArray, org.axonframework.eventhandling.EventMessage<*>>()
            .pollTimeout(5000)
            .build()
    }

    @Bean
    fun subscribableKafkaMessageSource(
        consumerFactory: ConsumerFactory<String, ByteArray>,
        fetcher: Fetcher<String, ByteArray, org.axonframework.eventhandling.EventMessage<*>>,
        serializer: Serializer
    ): SubscribableKafkaMessageSource<String, ByteArray> {
        return SubscribableKafkaMessageSource
            .builder<String, ByteArray>()
            .topics(listOf("axon.events"))
            .groupId("customer-service-group")
            .consumerFactory(consumerFactory)
            .fetcher(fetcher)
            .serializer(serializer)
            .build()
    }
}

@Component
class EventProcessorConfigurer(
    private val eventProcessingConfigurer: EventProcessingConfigurer,
    private val subscribableKafkaMessageSource: SubscribableKafkaMessageSource<String, ByteArray>
) {
    @Autowired
    fun configure() {
        eventProcessingConfigurer.registerSubscribingEventProcessor("orders-from-kafka") { config ->
            subscribableKafkaMessageSource
        }
    }
}
