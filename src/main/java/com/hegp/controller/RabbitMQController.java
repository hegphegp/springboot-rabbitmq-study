package com.hegp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hegp.po.Mail;
import com.hegp.po.TopicMail;
import com.hegp.service.impl.ProducerImpl;
import com.hegp.service.impl.PublisherImpl;

@Controller
public class RabbitMQController {
	
	@Autowired
	private ProducerImpl producer;
	
	@Autowired
	private PublisherImpl publisher;
	
	@RequestMapping(value="/produce",produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public void produce(@ModelAttribute("mail")Mail mail) throws InterruptedException {
		producer.sendMail("myqueue",mail);
	}
	
	@RequestMapping(value="/topic",produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public void topic(@ModelAttribute("mail")Mail mail) {
		publisher.publishMail(mail);
	}
	
	@RequestMapping(value="/direct",produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public void direct(@ModelAttribute("mail")TopicMail mail){
		Mail m=new Mail(mail.getMailId(),mail.getCountry(),mail.getWeight());
		publisher.senddirectMail(m, mail.getRoutingkey());
	}
	
	@RequestMapping(value="/mytopic",produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public void topic(@ModelAttribute("mail")TopicMail mail){
		Mail m=new Mail(mail.getMailId(),mail.getCountry(),mail.getWeight());
		publisher.sendtopicMail(m, mail.getRoutingkey());
	}

	@RequestMapping("/demo")
	public String demo(){
		return "demo";
	}

}
