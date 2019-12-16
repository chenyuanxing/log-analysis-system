package com.cad.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class SendMail {

    @Autowired
    private JavaMailSender mailSender;


    /**
     * 简单邮件.
     * @param to
     * @throws Exception
     */
    public void sendSimpleMail(String to) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("chenyuanxing_bupt@foxmail.com");
        message.setTo(to);
        message.setSubject("主题：Dashboard 报警");
        message.setText("你好，这是一封报警邮件");

        mailSender.send(message);
    }
    /**
     * 发送告警邮件.
     * @param to
     * @throws Exception
     */
    public void sendAlarmMail(String to,String emailMessage) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("chenyuanxing_bupt@foxmail.com");
        message.setTo(to);
        message.setSubject("主题：Dashboard 报警");
        message.setText(emailMessage);

        mailSender.send(message);
    }
    /**
     * 带附件的邮件.
     * @throws Exception
     */
    public void sendAttachmentsMail() throws Exception {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("dyc87112@qq.com");
        helper.setTo("dyc87112@qq.com");
        helper.setSubject("主题：有附件");
        helper.setText("有附件的邮件");

        FileSystemResource file = new FileSystemResource(new File("weixin.jpg"));
        helper.addAttachment("附件-1.jpg", file);
        helper.addAttachment("附件-2.jpg", file);

        mailSender.send(mimeMessage);

    }

}
