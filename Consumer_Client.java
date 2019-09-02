package Consumers;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer_Client implements ExceptionListener {

    void processConsumer() {
        try {
            // URL of the JMS server
            //String url = ActiveMQConnection.DEFAULT_BROKER_URL;
            String url = "tcp://localhost:61616";
            String queue = "ROUTE_QUEUE";

            // Getting JMS connection from the server
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            //create connection
            Connection connection = connectionFactory.createConnection();

            //create Listener for reception messages in connection
            connection.setExceptionListener(this);

            // Creating session for seding messages
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Getting the queue 'TEST_QUEUE'
            Destination destination = session.createQueue(queue);

            // MessageConsumer is used for receiving (consuming) messages
            MessageConsumer consumer = session.createConsumer(destination);

            consumer.setMessageListener(listener);

            connection.start();

        } catch (Exception e) {
            System.out.println("Exception " + e);
            e.printStackTrace();
        }

    }
    // Event Handler
    MessageListener listener = new MessageListener() {
        @Override
        public void onMessage(Message msg) {

            // We will be using TestMessage in our example. MessageProducer sent us a TextMessage
            // so we must cast to it to get access to its .getText() method.
            if (msg instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) msg;
                String text = null;
                try {
                    text = textMessage.getText();
                } catch (JMSException jms) {
                    System.out.println("Excepcion JMS " + jms);
                    jms.printStackTrace();
                }
                Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSSSSS");
				System.out.println(" Notificado a las " + formatter.format(date) + "Event Received from Engine = " + text);
            } else {
                System.out.println("Recibido " + msg);
            }
//            connection.close();   
        }
    };
    
    public synchronized void OnException(JMSException ex) {

        System.out.println("JMS Exception not Caugth. Shutting Down Client Consumer");
    }

    public static void main(String[] args) throws Exception{
    	Consumer_Client c = new Consumer_Client();
        System.out.println("Running Consumer for Detours Alerts");
        c.processConsumer();
    }

    @Override
    public void onException(JMSException jmse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
