package com.zdk.mai.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/17 17:37
 */
@RefreshScope
@Service
public class MailService {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async("mailTaskExecutor")
    public void sendEmail(String to,String subject,String text){
        SimpleMailMessage smm = new SimpleMailMessage();
        //发送者
        smm.setFrom(senderEmail);
        //收件人
        smm.setTo(to);
        //抄送人
        smm.setCc(senderEmail);
        //邮件主题
        smm.setSubject(subject);
        //邮件内容
        smm.setText(text);
        //发送邮件
        mailSender.send(smm);
    }

}
