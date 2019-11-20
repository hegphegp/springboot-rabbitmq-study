package com.hegp.rabbitMQ.listener;

import com.hegp.po.Mail;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class QueueListener {
	
	@RabbitListener(queues = "myqueue")
	public void displayMail1(@Headers Map<String, Object> headers, Mail mail) {
		System.out.println(headers);
		System.out.println("队列监听器1号收到消息"+mail.toString());
	}

	@RabbitListener(queues = "myqueue")
	public void displayMail2(@Headers Map<String, Object> headers, Mail mail) {
		System.out.println(headers);
		System.out.println("队列监听器2号收到消息"+mail.toString());
	}
}
