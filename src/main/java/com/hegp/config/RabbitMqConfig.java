package com.hegp.config;

import com.rabbitmq.client.Channel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.util.ErrorHandler;

import java.io.IOException;

@Configuration
public class RabbitMqConfig {
    @Bean
    public MessageConverter Jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConditionalRejectingErrorHandler conditionalRejectingErrorHandler() {
        return new ConditionalRejectingErrorHandler(new DefaultExceptionStrategy());
    }

//    @Bean
//    public SimpleMessageListenerContainer messageContainer(final ConnectionFactory connectionFactory) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//        container.setExposeListenerChannel(true);
//        container.setMaxConcurrentConsumers(1);
//        container.setConcurrentConsumers(1);
//        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式手工确认
//        container.setMessageListener(new ChannelAwareMessageListener() {
//
//            @Override
//            public void onMessage(org.springframework.amqp.core.Message message, Channel channel) throws Exception {
//                byte[] body = message.getBody();
//                System.out.println("receive msg : " + new String(body));
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息成功消费
//            }
//        });
//        return container;
//    }

    @Bean
    public RabbitListenerErrorHandler rabbitListenerErrorHandler() {
        return new RabbitListenerErrorHandler() {
            private Channel channel;


            @Override
            public Object handleError(Message amqpMessage, org.springframework.messaging.Message<?> message,
                                      ListenerExecutionFailedException exception) throws Exception {
                // 序列化不了的信息, 直接丢弃掉, 丢弃的话, 要用 channel 这个对象, 但是怎么获取
                if (exception.getCause() instanceof MessageConversionException) {

                }
                System.out.println("-------------------------------------"+message);
                throw exception;
//                return null;
            }
        };
    }


    /**
     * Default implementation of {@link FatalExceptionStrategy}.
     * @since 1.6.3
     */
    public static class DefaultExceptionStrategy implements FatalExceptionStrategy {

        protected final Log logger = LogFactory.getLog(getClass()); // NOSONAR

        @Override
        public boolean isFatal(Throwable t) {
            Throwable cause = t.getCause();
            while (cause instanceof MessagingException
                    && !(cause instanceof
                    org.springframework.messaging.converter.MessageConversionException)
                    && !(cause instanceof MethodArgumentResolutionException)) {
                cause = cause.getCause();
            }
            if (t instanceof ListenerExecutionFailedException && isCauseFatal(cause)) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn(
                            "Fatal message conversion error; message rejected; "
                                    + "it will be dropped or routed to a dead letter exchange, if so configured: "
                                    + ((ListenerExecutionFailedException) t).getFailedMessage());
                }
                return true;
            }
            return false;
        }

        private boolean isCauseFatal(Throwable cause) {
            return cause instanceof MessageConversionException // NOSONAR boolean complexity
                    || cause instanceof org.springframework.messaging.converter.MessageConversionException
                    || cause instanceof MethodArgumentResolutionException
                    || cause instanceof NoSuchMethodException
                    || cause instanceof ClassCastException
                    || isUserCauseFatal(cause);
        }

        /**
         * Subclasses can override this to add custom exceptions.
         * @param cause the cause
         * @return true if the cause is fatal.
         */
        protected boolean isUserCauseFatal(Throwable cause) {
            return false;
        }

    }

//    public static class BusinessExceptionStrategy implements FatalExceptionStrategy {
//        public static final Logger logger = LoggerFactory.getLogger(BusinessExceptionStrategy.class);
//
//        @Override
//        public boolean isFatal(Throwable t) {
//            if (t instanceof ListenerExecutionFailedException
//                    && causeIsFatal(t.getCause())) {
//                if (logger.isWarnEnabled()) {
//                    logger.warn(
//                            "Fatal message conversion error; message rejected; "
//                                    + "it will be dropped or routed to a dead letter exchange, if so configured: "
//                                    + ((ListenerExecutionFailedException) t).getFailedMessage());
//                }
//                return true;
//            }
//            return false;
//        }
//
//        protected boolean causeIsFatal(Throwable cause) {
//            return cause instanceof MessageConversionException
//                    || cause instanceof org.springframework.messaging.converter.MessageConversionException
//                    || cause instanceof MethodArgumentNotValidException
//                    || cause instanceof MethodArgumentTypeMismatchException
//                    || userFatalException(cause);
//        }
//
//        protected boolean userFatalException(Throwable cause) {
//            //如果我们抛出自定义的BusinessException异常，则不再重新入队，表示忽略异常
//            if (cause instanceof BusinessException) {
//                return true;
//            }
//            return true;
//        }
//    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    // 手动确认信息
    public static void basicAck(Channel channel, Message message, boolean multiple) {
        //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), multiple);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 手动确认信息
    public static void basicAck(Channel channel, long deliveryTag, boolean multiple) {
        //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
        try {
            channel.basicAck(deliveryTag, multiple);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 手动丢弃信息
    public static void basicNack(Channel channel, Message message, boolean multiple, boolean requeue) {
        try {
            //手动丢弃信息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), multiple, requeue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 手动丢弃信息
    public static void basicNack(Channel channel, long deliveryTag, boolean multiple, boolean requeue) {
        try {
            //手动丢弃信息
            channel.basicNack(deliveryTag, multiple, requeue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
