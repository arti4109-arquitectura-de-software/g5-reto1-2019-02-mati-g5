package Gestor_eventos;
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
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Gestor_eventos implements ExceptionListener {

	void processConsumer() {
		try {
			// URL of the JMS server
			// String url = ActiveMQConnection.DEFAULT_BROKER_URL;
			String url = "tcp://localhost:61616";
			// default broker URL is : tcp://localhost:61616"
			// Name of the queue we will receive messages from
			String queue = "RECEPTION_QUEUE";

			// Getting JMS connection from the server
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			// create connection
			Connection connection = connectionFactory.createConnection();

			// create Listener for reception messages in connection
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

			// We will be using TestMessage in our example. MessageProducer sent
			// us a TextMessage
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
				String  Message_info = (text + " recepcionada en el sistema a las = " + formatter.format(date));
				SendQueueMsg("tcp://localhost:61616", "ENGINE_QUEUE", Message_info);
				System.out.println(Message_info);
			}
			// only for messages without format
			else {
				System.out.println("Recibido " + msg);
			}
			// connection.close(); // Only for close connection with queue

		}
	};

	public synchronized void OnException(JMSException ex) {

		System.out.println("JMS Exception not Caugth. Shutting Down Client Consumer");
	}

	public static void main(String[] args) throws Exception {
		Gestor_eventos c = new Gestor_eventos();
		System.out.println("Running Event Handler");
		c.processConsumer();
	}

	@Override
	public void onException(JMSException jmse) {
		throw new UnsupportedOperationException("Not supported yet.");
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
			connection.close(); // We Close the session and connection
			return Message_send; // return Message sent
		} catch (Exception e) {
			String Error = ("Exception " + e);
			e.printStackTrace();
			return Error;
		}
	}
}
