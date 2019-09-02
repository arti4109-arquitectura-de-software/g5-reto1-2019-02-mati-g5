package Gestor_eventos;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class Gestor_eventos {
	
    public static void main(String[] args) throws Exception{
    	System.out.println("Running Engine");
    	while (true) {
            thread(new gestor_eventos(), false);
            thread(new gestor_eventos(), false);
            thread(new gestor_eventos(), false);
            thread(new gestor_eventos(), false);
            thread(new gestor_eventos(), false);
            Thread.sleep(1000);
		}
        
    }
    
    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    public static class gestor_eventos implements Runnable, ExceptionListener {
        public void run() {
        try {
            // URL of the JMS server
            //String url = ActiveMQConnection.DEFAULT_BROKER_URL;
            String url = "tcp://localhost:61616";
            String queue = "RECEPTION_QUEUE";

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
                Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSSSSS");
				switch (Integer.valueOf(text.substring(6, 7))) {
				case 1: // 
					
					String Message_critical = ("Critical Alert. Event received = " + text + " recepcionada en el sistema a las = " + formatter.format(date));
					SendQueueMsg("tcp://localhost:61616", "CRITICAL_QUEUE", Message_critical);
					System.out.println(Message_critical);
					break;
				case 2: // 
					String Message_critical2 = ("Critical Alert. Event received = " + text + " recepcionada en el sistema a las = " + formatter.format(date));
					SendQueueMsg("tcp://localhost:61616", "CRITICAL_QUEUE", Message_critical2);
					System.out.println(Message_critical2);
					break;
				case 3: // 
					String Message_critical3 = ("Critical Alert. Event received = " + text + " recepcionada en el sistema a las = " + formatter.format(date));
					SendQueueMsg("tcp://localhost:61616", "CRITICAL_QUEUE", Message_critical3);
					System.out.println(Message_critical3);
					break;
				case 4: // 
					String Message_Medium = ("Critical Alert. Event received = " + text + " recepcionada en el sistema a las = " + formatter.format(date));
					SendQueueMsg("tcp://localhost:61616", "MEDIUM_QUEUE", Message_Medium);
					System.out.println(Message_Medium);
					break;
				case 5: // Emergency Button
					Random rand = new Random();
					int n = rand.nextInt(3);
					switch (n){
					case 1: // fire
						String Message_critical_button = ("Critical Alert from button Emergency. Event received = " + text + " recepcionada en el sistema a las = " + formatter.format(date));
						SendQueueMsg("tcp://localhost:61616", "CRITICAL_QUEUE", Message_critical_button);
						System.out.println("Alert for Emergency button. Checking sensor measurements" + n);
						System.out.println(Message_critical_button);
						break;
					case 2: // steal
						String Message_critical_button2 = ("Critical Alert from button Emergency. Event received = " + text + " recepcionada en el sistema a las = " + formatter.format(date));
						SendQueueMsg("tcp://localhost:61616", "CRITICAL_QUEUE", Message_critical_button2);
						System.out.println("Alert for Emergency button. Checking sensor measurements" + n);
						System.out.println(Message_critical_button2);
						break;
					case 3: // steal
						String Message_critical_button3 = ("Critical Alert from button Emergency. Event received = " + text + " recepcionada en el sistema a las = " + formatter.format(date));
						SendQueueMsg("tcp://localhost:61616", "CRITICAL_QUEUE", Message_critical_button3);
						System.out.println("Alert for Emergency button. Checking sensor measurements" + n);
						System.out.println(Message_critical_button3);
						break;
					default: // Info about Car State
						String  Message_info = ("Critical Alert from button Emergency. Event received= "+ text + " recepcionada en el sistema a las = " + formatter.format(date));
						SendQueueMsg("tcp://localhost:61616", "INFO_QUEUE", Message_info);
						System.out.println("Alert for Emergency button. Checking sensor measurements" + n);
						System.out.println(Message_info);
						break;
					}
					break;
				default: // Info about Car State
					String  Message_info = ("Informative Alert. Event received = "+ text + " recepcionada en el sistema a las = " + formatter.format(date));
					SendQueueMsg("tcp://localhost:61616", "INFO_QUEUE", Message_info);
					System.out.println(Message_info);
					break;
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
