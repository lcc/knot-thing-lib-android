package com.example.knot_thing

import com.rabbitmq.client.*
import java.io.IOException


class KNoTAMQP(username : String, password : String, hostname : String, port : Int) {

    val EXCHANGE_TYPE_TOPIC = "topic"

    val EXCHANGE_NAME_CLOUD = "cloud"
    val EXCHANGE_NAME_FOG = "fog"

    val BINDING_KEY_REGISTER = "device.register"
    val BINDING_KEY_UNREGISTER = "device.unregister"
    val BINDING_KEY_SCHEMA_UPDATE = "schema.update"
    val BINDING_KEY_AUTHENTICATE = "device.cmd.auth"
    val BINDING_KEY_DATA_PUBLISH = "data.publish"

    val QUEUE_NAME_FOG_IN = "fogin"
    val QUEUE_NAME_FOG_OUT = "fogout"

    val factory = ConnectionFactory()
    val queueNames = mutableSetOf<String>()
    val consumerTags = mutableSetOf<String>()
    lateinit var conn : Connection
    lateinit var channel : Channel

    init {
        factory.host = hostname
        factory.username = username
        factory.password = password
        factory.port = port
    }

    fun startConnection() {
        conn = factory.newConnection()
        channel = conn.createChannel()
    }

    fun createExchange(
        exchangeName : String,
        exchangeType : String ) {

        channel.exchangeDeclare(
            exchangeName,
            exchangeType,
            true
        )
    }

    fun createQueue(queueName: String) {
        channel.queueDeclare(
            queueName,
            true,
            false,
            false,
            null
        ).queue
        queueNames.add(queueName)
    }

    fun bindQueue( queueName : String, exchangeName : String, routingKey: String) {
        channel.queueBind(queueName, exchangeName, routingKey)
    }

    fun createConsumer(queueName : String, consumerTag : String) {
        val autoAck = false
        consumerTags.add(consumerTag)
        channel.basicConsume(queueName, autoAck, consumerTag,
            object : DefaultConsumer(channel) {
                @Throws(IOException::class)
                override fun handleDelivery(
                    consumerTag: String,
                    envelope: Envelope,
                    properties: AMQP.BasicProperties,
                    body: ByteArray
                ) {
                    val routingKey = envelope.getRoutingKey()
                    val contentType = properties.contentType
                    val deliveryTag = envelope.getDeliveryTag()
                    // (process the message components here ...)
                    channel.basicAck(deliveryTag, false)
                }
            })
    }

    fun publish(message : String, exchange : String, routingKey : String) {
        val messageBodyBytes = message.toByteArray()
        val messageProperties = AMQP.BasicProperties.Builder()
            .deliveryMode(2)
            .build()

        channel.basicPublish(
            exchange,
            routingKey,
            messageProperties,
            messageBodyBytes
        )
    }

    fun deleteAllQueues() {
        queueNames.forEach {
            channel.queueDelete(it)
        }
    }

    fun cancelAllConsumers() {
        queueNames.forEach {
            channel.basicCancel(it)
        }
    }

    fun purgeAllQueues() {
        queueNames.forEach {
            channel.queuePurge(it)
        }
    }

    fun disconnect() {
            deleteAllQueues()
            cancelAllConsumers()
            channel.close()
            conn.close()
    }

}
