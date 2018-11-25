package com.tan.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

public class ItemDeleteListener extends AbstractAdaptableMessageListener {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;


    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage)message;
       Long[] ids =  (Long[])objectMessage.getObject();
        for (Long id : ids) {
            String file = ITEM_HTML_PATH+id+".html";
            File file1 = new File(file);
            if(file1.exists()){
                file1.delete();
            }
        }
    }
}
