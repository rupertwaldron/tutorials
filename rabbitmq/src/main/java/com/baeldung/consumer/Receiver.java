package com.baeldung.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receiver {

    private static Channel channel;

    private static Connection connection;

    private static final String QUEUE_NAME1 = "products_queue1";
    private static final String QUEUE_NAME2 = "products_queue2";

    public static void main (String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
        channel.queueDeclare(QUEUE_NAME2, false, false, false, null);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                try {
                    respond(message);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        channel.basicConsume(QUEUE_NAME1, true, consumer);
    }

    private static void respond(String message) throws IOException, TimeoutException {
        Integer sum = Integer.valueOf(message);
        sum *= 6;
        channel.basicPublish("", QUEUE_NAME2, null, sum.toString().getBytes());
    }
}
