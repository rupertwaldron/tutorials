package com.baeldung.producer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Publisher {

    private static final String QUEUE_NAME1 = "products_queue1";
    private static final String QUEUE_NAME2 = "products_queue2";

    public static void main(String[]args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String message = "2";
        channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
        channel.queueDeclare(QUEUE_NAME2, false, false, false, null);

        channel.basicPublish("", QUEUE_NAME1, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(QUEUE_NAME2, true, consumer);

//        channel.close();
//        connection.close();
    }
}
