package org.mail.javaMail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

public class JavaMailDemo {
    public static String senderAddress = "893862613@qq.com";
    public static String receiveAddress = "qitingma@163.com";
    public static String senderPwd = "lvfuqcjmftubbdcj";
    public static String mySMTPHost = "smtp.qq.com";
    public static String charset = "utf-8";

    public static MimeMessage getMimeMessage(Session session) throws Exception {
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress("893862613@qq.com","PORNHUB","UTF-8"));
        mimeMessage.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress("17629072675@163.com","六六","UTF-8"));
        mimeMessage.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(receiveAddress,"铁牛哞哞","UTF-8"));
        mimeMessage.setSubject("PORNHUB欢迎您回来，送您一个月会员免费试用",charset);
        MimeBodyPart img = new MimeBodyPart();
        DataHandler handler = new DataHandler(new FileDataSource("f:\\pornhub\\tomls.jpeg"));
        img.setDataHandler(handler);
        img.setContentID("mailTestPic");
        MimeBodyPart text = new MimeBodyPart();
        text.setContent("点击领取试用会员<br><a href='http://www.baidu.com'><img src='cid:mailTestPic'></a>","text/html;charset=UTF-8");
        MimeMultipart mm_text_image = new MimeMultipart();
        mm_text_image.addBodyPart(text);
        mm_text_image.addBodyPart(img);
        mm_text_image.setSubType("related");
        MimeBodyPart text_img = new MimeBodyPart();
        text_img.setContent(mm_text_image);
        MimeBodyPart attachment = new MimeBodyPart();
        DataHandler dh2 = new DataHandler(new FileDataSource("f:\\pornhub\\PORNHUB-checkUser.pdf"));
        attachment.setDataHandler(dh2);
        attachment.setFileName(MimeUtility.encodeText(dh2.getName()));
        MimeMultipart mm = new MimeMultipart();
        mm.addBodyPart(text_img);
        mm.addBodyPart(attachment);
        mm.setSubType("mixed");
        mimeMessage.setContent(mm);
        mimeMessage.setSentDate(new Date());
        return mimeMessage;

    }

    public static void main(String[] args) {
        try{
            Properties prop = new Properties();
            prop.setProperty("mail.transport.protocol","smtp");
            prop.setProperty("mail.smtp.host",mySMTPHost);
            prop.setProperty("mail.smtp.auth","true");
            Session session = javax.mail.Session.getDefaultInstance(prop);
            session.setDebug(true);
            MimeMessage mimeMessage = JavaMailDemo.getMimeMessage(session);
            Transport transport = session.getTransport();
            transport.connect(senderAddress,senderPwd);
            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
            System.out.println("发送成功");
            transport.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
