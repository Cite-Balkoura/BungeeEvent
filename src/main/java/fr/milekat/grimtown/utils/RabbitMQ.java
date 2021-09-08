package fr.milekat.grimtown.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.milekat.grimtown.MainBungee;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMQ {
    private static Connection CONNECTION = null;

    /**
     * Init/Get RabbitMQ Connection
     */
    private static Connection getConnection() {
        if (CONNECTION == null) {
            try {
                ConnectionFactory connectionFactory = new ConnectionFactory();
                connectionFactory.setHost(MainBungee.getConfig().getString("data.rabbitMQ.host"));
                connectionFactory.setPort(MainBungee.getConfig().getInt("data.rabbitMQ.port"));
                connectionFactory.setUsername(MainBungee.getConfig().getString("data.rabbitMQ.user"));
                connectionFactory.setPassword(MainBungee.getConfig().getString("data.rabbitMQ.password"));
                CONNECTION = connectionFactory.newConnection();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        return CONNECTION;
    }

    /**
     * Load RABBIT_CONFIG.get("queue") Consumer
     */
    public Thread getRabbitConsumer() throws IOException {
        Channel channel = getConnection().createChannel();
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            if (MainBungee.DEBUG_RABBIT) MainBungee.log(new String(message.getBody(), StandardCharsets.UTF_8));
        };
        return new Thread(() -> {
                    try {
                        channel.basicConsume(
                                MainBungee.getConfig().getString("data.rabbitMQ.queue"),
                                true,
                                deliverCallback,
                                MainBungee::log
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * Send message through RABBIT_CONFIG.get("routingKey") queue
     */
    public static void rabbitSend(String message) throws IOException, TimeoutException {
        Channel channel = getConnection().createChannel();
        channel.basicPublish(MainBungee.getConfig().getString("data.rabbitMQ.exchange"),
                MainBungee.getConfig().getString("data.rabbitMQ.routingKey"),
                null, message.getBytes(StandardCharsets.UTF_8));
        channel.close();
    }
}
