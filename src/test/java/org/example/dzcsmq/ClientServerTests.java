package org.example.dzcsmq;

import org.example.dzcsmq.mqclient.Channel;
import org.example.dzcsmq.mqclient.Connection;
import org.example.dzcsmq.mqserver.BrokerServer;
import org.example.dzcsmq.mqserver.core.ExchangeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientServerTests {
    @Test
    void clientCanCreateChannelAndCallCoreApi() throws Exception {
        int port = 19090;
        BrokerServer brokerServer = new BrokerServer(port);
        Thread serverThread = new Thread(() -> {
            try {
                brokerServer.start();
            } catch (Exception ignored) {
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(500);

        try (Connection connection = new Connection("127.0.0.1", port);
             Channel channel = connection.createChannel()) {
            assertNotNull(channel.getChannelId());
            assertTrue(channel.exchangeDeclare("client.test.exchange", ExchangeType.DIRECT, false, false));
            assertTrue(channel.queueDeclare("client.test.queue", false, false, false));
            assertTrue(channel.queueBind("client.test.queue", "client.test.exchange", "client.test.key"));
            assertTrue(channel.basicPublish("client.test.exchange", "client.test.key", null, "hello".getBytes()));
            assertTrue(channel.queueUnbind("client.test.queue", "client.test.exchange", "client.test.key"));
            assertTrue(channel.queueDelete("client.test.queue"));
            assertTrue(channel.exchangeDelete("client.test.exchange"));
        } finally {
            brokerServer.stop();
        }
    }
}
