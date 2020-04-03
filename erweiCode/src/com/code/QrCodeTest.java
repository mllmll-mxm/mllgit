package com.code;

public class QrCodeTest {
    public static void main(String[] args) throws Exception {
        String text = "http://www.4399.com";
        String imgPath = "f:\\pornhub\\dog.jpg";
        String destPath = "f:\\pornhub\\dest.jpg";
        QRCodeUtil.encode(text,imgPath,destPath,true);
        String decode = QRCodeUtil.decode(destPath);
        System.out.println(decode);
    }
}
