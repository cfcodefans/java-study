package cf.study.java.javax.mail;

import org.junit.Test;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

public class MailTests {
    private static final String MAIL_SERVER_HOST = "ned.thenetcircle.lab";
    private static final String USER = "mjtest@fetisch.de.lab";
    private static final String PASSWORD = "123456";

    @Test
    public void receive() throws Exception {

        // 创建一个有具体连接信息的Properties对象
        Properties prop = new Properties();
        prop.setProperty("mail.debug", "true");
        prop.setProperty("mail.store.protocol", "pop3");
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.host", MAIL_SERVER_HOST);
        prop.setProperty("mail.pop3.host", MAIL_SERVER_HOST);

        // 1、创建session
        Session session = Session.getInstance(prop);

        // 2、通过session得到Store对象
        Store store = session.getStore();

        // 3、连上邮件服务器
        store.connect(MAIL_SERVER_HOST, USER, PASSWORD);

        // 4、获得邮箱内的邮件夹
        try (Folder folder = store.getFolder("inbox")) {
            folder.open(Folder.READ_ONLY);

            // 获得邮件夹Folder内的所有邮件Message对象
            Message[] messages = folder.getMessages();
            for (int i = 0; i < messages.length; i++) {
                String subject = messages[i].getSubject();
                String from = (messages[i].getFrom()[0]).toString();
                System.out.println("第 " + (i + 1) + "封邮件的主题：" + subject);
                System.out.println("第 " + (i + 1) + "封邮件的发件人地址：" + from);
            }

//            int messageCount = folder.getMessageCount();
//            int newMessageCount = folder.getNewMessageCount();
//            System.out.println(String.format("new messages: \t%d", newMessageCount));
//            for (int i = 1; i <= newMessageCount && i >= 1; i++) {
//                System.out.println(i);
//                Message msg = folder.getMessage(i);
//                System.out.println(msg.getSubject());
//                System.out.println(msg.getContent());
//            }
        }
        // 5、关闭
//        folder.close(false);
        store.close();
    }
}
