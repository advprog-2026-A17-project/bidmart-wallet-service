package id.ac.ui.cs.advprog.bidmartwalletservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${app.wallet.provisioning.queue:wallet.provision.queue}")
    private String queueName;

    @Value("${app.wallet.provisioning.exchange:bidmart.wallet.provisioning}")
    private String exchangeName;

    @Value("${app.wallet.provisioning.routing-key:wallet.provision.requested.v1}")
    private String routingKey;

    @Value("${app.wallet.provisioning.dlx:wallet.provisioning.dlx}")
    private String deadLetterExchangeName;

    @Value("${app.wallet.provisioning.dlq:wallet.provisioning.dlq}")
    private String deadLetterQueueName;

    @Value("${app.wallet.provisioning.dlq-routing-key:wallet.provisioning.failed.v1}")
    private String deadLetterRoutingKey;

    @Value("${app.wallet.provisioning.retry-attempts:3}")
    private int retryAttempts;

    @Bean
    public TopicExchange walletProvisioningExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue walletProvisioningQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", deadLetterExchangeName);
        arguments.put("x-dead-letter-routing-key", deadLetterRoutingKey);
        return new Queue(queueName, true, false, false, arguments);
    }

    @Bean
    public Binding walletProvisioningBinding(Queue walletProvisioningQueue, TopicExchange walletProvisioningExchange) {
        return BindingBuilder.bind(walletProvisioningQueue).to(walletProvisioningExchange).with(routingKey);
    }

    @Bean
    public TopicExchange walletProvisioningDeadLetterExchange() {
        return new TopicExchange(deadLetterExchangeName);
    }

    @Bean
    public Queue walletProvisioningDeadLetterQueue() {
        return new Queue(deadLetterQueueName, true);
    }

    @Bean
    public Binding walletProvisioningDeadLetterBinding(
            Queue walletProvisioningDeadLetterQueue,
            TopicExchange walletProvisioningDeadLetterExchange
    ) {
        return BindingBuilder.bind(walletProvisioningDeadLetterQueue)
                .to(walletProvisioningDeadLetterExchange)
                .with(deadLetterRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RetryOperationsInterceptor walletProvisioningRetryInterceptor() {
        int effectiveRetryAttempts = Math.max(1, retryAttempts);
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(effectiveRetryAttempts)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory walletProvisioningListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            MessageConverter jsonMessageConverter,
            RetryOperationsInterceptor walletProvisioningRetryInterceptor
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(walletProvisioningRetryInterceptor);
        return factory;
    }
}
