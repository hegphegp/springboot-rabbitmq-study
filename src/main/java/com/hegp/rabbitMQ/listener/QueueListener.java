package com.hegp.rabbitMQ.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hegp.config.RabbitMqConfig;
import com.hegp.po.Mail;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
public class QueueListener {
	private static final String X_RETRIES_HEADER = "retryCount";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@RabbitListener(queues = "myqueue")
	public void displayMail1(Message message, @Headers Map<String, Object> headers, Channel channel) {
//		System.out.println("myqueue队列监听器收到消息");
		System.out.println(sdf.format(new Date())+"  "+headers);
//		System.out.println(message);
		try {
			Mail mail = mapper.readValue(message.getBody(), Mail.class);
			Integer retriesHeader = (Integer) message.getMessageProperties().getHeaders().get(X_RETRIES_HEADER);
			retriesHeader = retriesHeader == null? 0:retriesHeader;
			if (retriesHeader < 3) {
				System.out.println("处理失败, 在头部添加重试次数, 重发");
				message.getMessageProperties().setHeader("x-delay", 3000);
				message.getMessageProperties().getHeaders().put(X_RETRIES_HEADER, retriesHeader + 1);
				rabbitTemplate.send("myqueue", message);
			} else { // 失败3次后,不再重试
				System.out.println("失败3次后,不再重试");
			}
		} catch (IOException e) {
			e.printStackTrace();
			// 丢弃这条消息
			// RabbitMqConfig.basicNack(channel, message, false, false);
			// System.out.println("丢弃这条消息");
			System.out.println("receiver fail");
		} finally {
			RabbitMqConfig.basicAck(channel, message, false);
		}
	}
}
