package com.hegp.rabbitMQ.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.hegp.po.Mail;

@Component
public class DirectListener {
	@RabbitListener(queues = "directqueue1")
	public void displayMail1(Mail mail) {
		System.out.println("directqueue1队列监听器1号收到消息"+mail.toString());
	}

	@RabbitListener(queues = "directqueue2")
	public void displayMail2(Mail mail) {
		System.out.println("directqueue2队列监听器2号收到消息"+mail.toString());
	}
}
