import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class rabbitMQ {
    private static final String rapid = "rapid";
    private static Map<String, DeliverCallback> events = new HashMap<>();

    private static Channel setupChannel() throws TimeoutException, IOException {
        // TODO: Figure out how AMQP link is given securely
        String uri = null;
        if (uri == null) {
            uri = "localhost";
        }
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(uri);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(rabbitMQ.rapid,"direct");
        return channel;
    }

    public static void send(String event, String body) throws IOException, TimeoutException {
        Channel channel = setupChannel();
        channel.basicPublish(rabbitMQ.rapid, event,null, body.getBytes("UTF-8"));
        channel.close();
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
                //TODO some error logging that method received event that does not exist?
                return;
            } else {
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                func.handle(consumerTag, delivery);
            }
            System.out.println(" [x] Received event " + delivery.getEnvelope().getRoutingKey());
        });
        channel.basicConsume(queueName,callback, consumerTag -> { });
    }

    public static void addSubscription(String event,String queue, DeliverCallback func) throws IOException, TimeoutException {
        rabbitMQ.bindQueue(event,queue);
        events.put(event,func);
    }
}
