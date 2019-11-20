package com.hegp.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hegp.po.Mail;
import com.hegp.service.Producer;

@Service
public class ProducerImpl implements Producer {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void sendMail(String queue, Mail mail) throws InterruptedException {
		rabbitTemplate.convertAndSend(queue, mail);
		Thread.sleep(1);
	}

}
