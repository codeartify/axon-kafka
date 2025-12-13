package com.codeartify.customerservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.axonframework.eventhandling.EventMessage
import org.axonframework.eventhandling.GenericEventMessage
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter
import org.slf4j.LoggerFactory
import java.util.*

class RawJsonKafkaMessageConverter(
    private val objectMapper: ObjectMapper
) : KafkaMessageConverter<String, ByteArray> {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun readKafkaMessage(consumerRecord: ConsumerRecord<String, ByteArray>): Optional<EventMessage<*>> {
        return try {
            val json = String(consumerRecord.value())

            val event = objectMapper.readValue(json, OrderPlacedEvent::class.java)

            Optional.of(GenericEventMessage.asEventMessage<Any>(event))
        } catch (e: Exception) {
            Optional.empty()
        }
    }

    override fun createKafkaMessage(eventMessage: EventMessage<*>, topic: String): ProducerRecord<String, ByteArray> {
        return  ProducerRecord(topic, objectMapper.writeValueAsBytes(eventMessage.payload))
    }
}
