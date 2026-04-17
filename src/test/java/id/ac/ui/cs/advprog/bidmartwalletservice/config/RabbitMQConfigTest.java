package id.ac.ui.cs.advprog.bidmartwalletservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

class RabbitMQConfigTest {

    @Test
    void testRabbitMqBeansCreation() {
        RabbitMQConfig config = new RabbitMQConfig();
        ReflectionTestUtils.setField(config, "queueName", "wallet.q");
        ReflectionTestUtils.setField(config, "exchangeName", "wallet.ex");
        ReflectionTestUtils.setField(config, "routingKey", "wallet.key");
        ReflectionTestUtils.setField(config, "deadLetterExchangeName", "wallet.dlx");
        ReflectionTestUtils.setField(config, "deadLetterQueueName", "wallet.dlq");
        ReflectionTestUtils.setField(config, "deadLetterRoutingKey", "wallet.dead.key");
        ReflectionTestUtils.setField(config, "retryAttempts", 0);

        TopicExchange exchange = config.walletProvisioningExchange();
        Queue queue = config.walletProvisioningQueue();
        Binding binding = config.walletProvisioningBinding(queue, exchange);

        TopicExchange deadLetterExchange = config.walletProvisioningDeadLetterExchange();
        Queue deadLetterQueue = config.walletProvisioningDeadLetterQueue();
        Binding deadLetterBinding = config.walletProvisioningDeadLetterBinding(deadLetterQueue, deadLetterExchange);

        MessageConverter messageConverter = config.jsonMessageConverter();
        RetryOperationsInterceptor retryInterceptor = config.walletProvisioningRetryInterceptor();

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        SimpleRabbitListenerContainerFactoryConfigurer configurer =
                mock(SimpleRabbitListenerContainerFactoryConfigurer.class);
        doNothing().when(configurer).configure(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(connectionFactory));
        SimpleRabbitListenerContainerFactory listenerFactory = config.walletProvisioningListenerContainerFactory(
                connectionFactory,
                configurer,
                messageConverter,
                retryInterceptor
        );

        assertEquals("wallet.ex", exchange.getName());
        assertEquals("wallet.q", queue.getName());
        assertNotNull(binding);
        assertEquals("wallet.dlx", deadLetterExchange.getName());
        assertEquals("wallet.dlq", deadLetterQueue.getName());
        assertNotNull(deadLetterBinding);
        assertNotNull(messageConverter);
        assertNotNull(retryInterceptor);
        assertNotNull(listenerFactory);
    }
}
