import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class rabbitMQ {
    private static final String rapid = "rapid";
    private static Map<String, DeliverCallback> events = new HashMap<>();
    private static Connection connection;

    public static void setupRabbit() throws IOException, TimeoutException {
        String uri =  "amqp://"+System.getenv("RABBITMQ_USER") + ":"  + System.getenv("RABBITMQ_PASS") + "@" + System.getenv("RABBITMQ_HOST");
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(uri);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            System.out.println("rabbitMQ connection failed! Cause: " + e.getCause().getMessage() + "\nHas it finished starting?");
            e.printStackTrace(System.err);
        }
        connection = factory.newConnection();
        System.out.println("RabbitMQ connection is now ready!");
    }

    private static Channel setupChannel() throws IOException {
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(rabbitMQ.rapid,"direct");
        return channel;
    }

    public static void send(String event, String body, BasicProperties props) {
        try {
            Channel channel = setupChannel();
            channel.basicPublish(rabbitMQ.rapid, event,props, body.getBytes("UTF-8"));
            System.out.println(" [x] Sending " + event);
            channel.close();
        } catch (IOException | TimeoutException e) {
            System.out.println("An error occured while trying to send the event " + event + " on the " + rabbitMQ.rapid + " exchange.");
        }
    }

    //Bind a single event to one queue
    private static void bindQueue(String event, String queue) throws IOException {
        Channel channel = setupChannel();
        channel.queueDeclare(queue,false,false,false,null);
        channel.queueBind(queue,rabbitMQ.rapid,event);
    }

    public static void setupReceiver(String queueName) throws IOException {
        Channel channel = setupChannel();
        DeliverCallback callback = ((consumerTag, delivery) -> {
            DeliverCallback func = events.get(delivery.getEnvelope().getRoutingKey());
            if(func == null) {
                System.out.println("Service received an event, which does not have a associated function! Caused by: " + delivery.getEnvelope().getRoutingKey());
                return;
            } else {
                System.out.println(" [x] Received " + delivery.getEnvelope().getRoutingKey());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                func.handle(consumerTag, delivery);
            }
        });
        channel.basicConsume(queueName,callback, consumerTag -> { });
    }

    public static void addSubscription(String event,String queue, DeliverCallback func) throws IOException, TimeoutException {
        rabbitMQ.bindQueue(event,queue);
        events.put(event,func);
    }

    public static BasicProperties setupProperties(String correlationId, String contentType, int status_code, String status_message){
        Map<String, Object> header = new HashMap<>();
        header.put("status_code", status_code);
        header.put("status_message", status_message);
        return new BasicProperties().builder().contentType(contentType).correlationId(correlationId).headers(header).build();
    }
}
