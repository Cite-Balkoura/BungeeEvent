package fr.milekat.grimtown.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.milekat.grimtown.MainBungee;
import net.md_5.bungee.api.ProxyServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class RabbitMQ {
    public RabbitMQ() {
        try {
            Channel channel = getConnection().createChannel();
            channel.exchangeDeclare(MainBungee.getConfig().getString("data.rabbitMQ.exchange"), "direct");
            channel.queueDeclare("consumer_" + MainBungee.getEvent().getDatabase(),
                    false, false, false, null);
            channel.queueBind("consumer_" + MainBungee.getEvent().getDatabase(),
                    MainBungee.getConfig().getString("data.rabbitMQ.exchange"),
                    "prod.bungee.all");
            channel.queueBind("consumer_" + MainBungee.getEvent().getDatabase(),
                    MainBungee.getConfig().getString("data.rabbitMQ.exchange"),
                    "prod.bungee." + MainBungee.getEvent().getDatabase());
            channel.queueDeclare(MainBungee.getConfig().getString("data.rabbitMQ.publisher.queue"),
                    false, false, false, null);
            channel.queueBind(MainBungee.getConfig().getString("data.rabbitMQ.publisher.queue"),
                    MainBungee.getConfig().getString("data.rabbitMQ.exchange"),
                    MainBungee.getConfig().getString("data.rabbitMQ.publisher.routingKey"));
            channel.close();
            getConnection().close();
        } catch (IOException | TimeoutException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Init/Get RabbitMQ Connection
     */
    private static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(MainBungee.getConfig().getString("data.rabbitMQ.host"));
        connectionFactory.setPort(MainBungee.getConfig().getInt("data.rabbitMQ.port"));
        connectionFactory.setUsername(MainBungee.getConfig().getString("data.rabbitMQ.user"));
        connectionFactory.setPassword(MainBungee.getConfig().getString("data.rabbitMQ.password"));
        return connectionFactory.newConnection();
    }

    /**
     * Load prod.bungee.%eventName% Consumer
     */
    public Thread getRabbitConsumer() throws IOException {
        return new Thread(() -> {
            try {
                Channel channel = getConnection().createChannel();
                DeliverCallback deliverCallback = (consumerTag, message) -> {
                    if (MainBungee.DEBUG_RABBIT) MainBungee.log(new String(message.getBody(), StandardCharsets.UTF_8));
                    try {
                        JSONObject json = (JSONObject) new JSONParser().parse(new String(message.getBody(), StandardCharsets.UTF_8));
                        RabbitMQReceive.MessageType messageType = RabbitMQReceive.MessageType.other;
                        String type = (String) Optional.ofNullable(json.get("type")).orElse("other");
                        try {
                            messageType = RabbitMQReceive.MessageType.valueOf(type);
                        } catch (IllegalArgumentException ignore) {}
                        ProxyServer.getInstance().getPluginManager().callEvent(new RabbitMQReceive(messageType, json));
                        if (MainBungee.DEBUG_RABBIT && messageType.equals(RabbitMQReceive.MessageType.other)) {
                            MainBungee.warning("RabbitMQ Unknown type: " + type);
                        }
                    } catch (ParseException exception) {
                        exception.printStackTrace();
                    }
                };
                channel.basicConsume("consumer_" + MainBungee.getEvent().getDatabase(), true, deliverCallback,
                        MainBungee::log);
            } catch (IOException | TimeoutException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Send message through RABBIT_CONFIG.get("routingKey") queue
     */
    public static void rabbitSend(String message) throws IOException, TimeoutException {
        Channel channel = getConnection().createChannel();
        channel.basicPublish(MainBungee.getConfig().getString("data.rabbitMQ.exchange"),
                MainBungee.getConfig().getString("data.rabbitMQ.publisher.routingKey"),
                null, message.getBytes(StandardCharsets.UTF_8));
        channel.close();
        getConnection().close();
    }
}
