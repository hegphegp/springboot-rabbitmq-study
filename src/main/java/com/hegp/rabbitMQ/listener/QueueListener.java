package com.hegp.rabbitMQ.listener;

import com.hegp.po.Mail;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class QueueListener {
	
	@RabbitListener(queues = "myqueue")
	public void displayMail1(Message message, @Headers Map<String, Object> headers, Channel channel, @Payload Mail mail) {
		System.out.println(headers);
		try {
			System.out.println(message);
			//告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			System.out.println("receiver success");
		} catch (IOException e) {
			e.printStackTrace();
			//丢弃这条消息
			//channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
			System.out.println("receiver fail");
		}
		System.out.println("队列监听器1号收到消息"+mail.toString());
	}

	@RabbitListener(queues = "myqueue")
	public void displayMail2(Message message, @Headers Map<String, Object> headers, Channel channel, @Payload Mail mail) {
		System.out.println(headers);
		try {
			System.out.println(message);
			//告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			System.out.println("receiver success");
		} catch (IOException e) {
			e.printStackTrace();
			//丢弃这条消息
			//channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
			System.out.println("receiver fail");
		}
		System.out.println("队列监听器2号收到消息"+mail.toString());
	}
}
