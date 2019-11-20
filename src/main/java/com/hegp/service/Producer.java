package com.hegp.service;

import com.hegp.po.Mail;

public interface Producer {
	void sendMail(String queue, Mail mail) throws InterruptedException;//向队列queue发送消息
}
