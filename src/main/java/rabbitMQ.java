import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class rabbitMQ {
    private static final String rapid = "rapid";
    private static Map<String, DeliverCallback> events = new HashMap<>();

    private static Channel setupChannel() throws IOException, TimeoutException {
        String uri = System.getenv("AMQP_URI");
        ConnectionFactory factory = new ConnectionFactory();
        System.out.println("rabbitmq URL: "+ uri);
        try {
            factory.setUri(uri);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            System.out.println("rabbitMQ connection failed! Cause: " + e.getCause().getMessage() + "\nHas it finished starting?");
            e.printStackTrace(System.err);
        }
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(rabbitMQ.rapid,"direct");
        return channel;
    }

    public static void send(String event, String body, String correlationId) {
        try {
            Channel channel = setupChannel();
            BasicProperties props = new BasicProperties().builder().correlationId(correlationId).build();
            channel.basicPublish(rabbitMQ.rapid, event,props, body.getBytes("UTF-8"));
            channel.close();
        } catch (IOException | TimeoutException e) {
            System.out.println("An error occured while trying to send the event " + event + " on the " + rabbitMQ.rapid + " exchange.");
        }
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
    private static void bindQueue(String event, String queue) throws IOException, TimeoutException {
        Channel channel = setupChannel();
        channel.queueDeclare(queue,false,false,false,null);
        channel.queueBind(queue,rabbitMQ.rapid,event);
    }

    public static void setupReceiver(String queueName) throws TimeoutException, IOException {
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
