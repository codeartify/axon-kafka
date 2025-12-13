package com.codeartify.customerservice

import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EventProcessorConfig {

    @Autowired
    fun configure(
        application: CustomerServiceApplication,
        eventProcessingConfigurer: EventProcessingConfigurer,
        subscribableKafkaMessageSource: SubscribableKafkaMessageSource<String, ByteArray>
    ) {
        eventProcessingConfigurer
            .registerSubscribingEventProcessor("orders-from-kafka") { config ->
                subscribableKafkaMessageSource
            }
    }
}
