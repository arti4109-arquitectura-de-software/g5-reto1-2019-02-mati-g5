package engine;

import java.util.Random;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Engine_critical_alerts {
	
    public static void main(String[] args) throws Exception{
    	System.out.println("Running Engine");
    	while (true) {
            thread(new Engine_critical(), false);
            thread(new Engine_critical(), false);
            thread(new Engine_critical(), false);
            thread(new Engine_critical(), false);
            thread(new Engine_critical(), false);
            Thread.sleep(1000);
		}
        
    }
    
    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    public static class Engine_critical implements Runnable, ExceptionListener {
        public void run() {
        try {
            // URL of the JMS server
            //String url = ActiveMQConnection.DEFAULT_BROKER_URL;
            String url = "tcp://localhost:61616";
            String queue = "CRITICAL_QUEUE";

            // Getting JMS connection from the server
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            //create connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            //create Listener for reception messages in connection
            connection.setExceptionListener(this);

            // Creating session for seding messages
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Getting the queue 'TEST_QUEUE'
            Destination destination = session.createQueue(queue);

            // MessageConsumer is used for receiving (consuming) messages
            MessageConsumer consumer = session.createConsumer(destination);

            Message msg = consumer.receive(100);
            if (msg instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) msg;
                String text = null;
                try {
                    text = textMessage.getText();
                } catch (JMSException jms) {
                    System.out.println("Excepcion JMS " + jms);
                    jms.printStackTrace();
                }
                //Date date = new Date();
				//SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSSSSS");
				switch (Integer.valueOf(text.substring(6, 7))) {
				case 1: // fire
					String Message_fire = ("Alert for Fire, Notifying FireFighters. Event received = " + text);
					SendQueueMsg("tcp://localhost:61616", "FIRE_QUEUE", Message_fire);
					System.out.println(Message_fire);
					break;
				case 2: // steal
					String Message_steal = ("Alert for Vehicle Robbery, Notifying Police. Event received =" + text);
					SendQueueMsg("tcp://localhost:61616", "STEAL_QUEUE", Message_steal);
					System.out.println(Message_steal);
					break;
				case 3: // accident
					String Message_acc = ("Alert for Car Accident. Event received = "  + text);
					SendQueueMsg("tcp://localhost:61616", "ACC_QUEUE", Message_acc);
					System.out.println(Message_acc);
					break;
				case 5: // Emergency Button
					System.out.println("Alert for Emergency button. Checking sensor measurements");
					Random rand = new Random();
					int n = rand.nextInt(3);
					switch (n){
					case 1:
						String Message_fire_emergency = ("Alert for Fire, Notifying FireFighters. Event received =" + text);
						SendQueueMsg("tcp://localhost:61616", "FIRE_QUEUE", Message_fire_emergency);
						System.out.println(Message_fire_emergency);
						break;
					case 2:
						String Message_steal_emergency = ("Alert for Vehicle Robbery, Notifying Police. Event received =" + text);
						SendQueueMsg("tcp://localhost:61616", "STEAL_QUEUE", Message_steal_emergency);
						System.out.println(Message_steal_emergency);
						break;
					default:
						String Message_acc_emergency = ("Alert for Car Accident. Event received = " + text);
						SendQueueMsg("tcp://localhost:61616", "ACC_QUEUE", Message_acc_emergency);
						System.out.println(Message_acc_emergency);
						break;
					}
				}    
            } else {
                //System.out.println("Recibido " + msg);
            }
            consumer.close();
            session.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("Exception " + e);
            e.printStackTrace();
        }

    }
    
    public synchronized void OnException(JMSException ex) {
        System.out.println("JMS Exception not Caugth. Shutting Down Client Consumer");
    }
  
   
    @Override
    public void onException(JMSException jmse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static String SendQueueMsg(String url, String Queue, String Message) {
		try {

			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			Connection connection = connectionFactory.createConnection();
			connection.start();

			// Creating a non transactional session to send/receive JMS message.
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Destination destination = session.createQueue(Queue);

			MessageProducer producer = session.createProducer(destination); // MessageProducer // is used for sending Message to Queue
			TextMessage message_queue = session.createTextMessage(Message); // Create Messages for Queue
			producer.send(message_queue); // Send Messages Queue
			String Message_send = message_queue.getText(); // get Messages sent
			session.close();
			connection.close(); // We Close the session and connection
			return Message_send; // return Message sent
		} catch (Exception e) {
			String Error = ("Exception " + e);
			e.printStackTrace();
			return Error;
		}
	}
    
}
}
