package com.hegp.rabbitMQ.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import com.hegp.po.Mail;

import java.util.Map;

@Component
public class TopicListener {
	private ObjectMapper mapper = new ObjectMapper();

	@RabbitListener(queues = "topicqueue1")
	public void displayTopic1(Mail mail) {
		System.out.println("从topicqueue1取出消息"+mail.toString());
	}

	@RabbitListener(queues = "topicqueue2")
	public void displayTopic2(@Headers Map<String, Object> headers, Mail mail) throws JsonProcessingException {
		System.out.println(mapper.writeValueAsString(headers));
		System.out.println("从topicqueue2取出消息"+mail.toString());
	}

}
