package org.example.dzcsmq.mqserver;

import org.example.dzcsmq.common.FrameIO;
import org.example.dzcsmq.common.Request;
import org.example.dzcsmq.common.RequestType;
import org.example.dzcsmq.common.Response;
import org.example.dzcsmq.mqserver.core.VirtualHost;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrokerServer {
    public static final int DEFAULT_PORT = 9090;

    private final int port;
    private final Map<String, VirtualHost> virtualHostMap = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile boolean running = false;
    private ServerSocket serverSocket;

    public BrokerServer() {
        this(DEFAULT_PORT);
    }

    public BrokerServer(int port) {
        this.port = port;
        virtualHostMap.put("/", new VirtualHost("/"));
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        while (running) {
            Socket socket = serverSocket.accept();
            executorService.submit(() -> processConnection(socket));
        }
    }

    public void stop() throws IOException {
        running = false;
        executorService.shutdownNow();
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    private void processConnection(Socket socket) {
        Map<String, ServerChannel> channelMap = new ConcurrentHashMap<>();
        try (socket) {
            FrameIO frameIO = new FrameIO(socket.getInputStream(), socket.getOutputStream());
            while (running && !socket.isClosed()) {
                Request request = (Request) frameIO.readObject();
                Response response = processRequest(request, channelMap);
                frameIO.writeObject(response);
            }
        } catch (EOFException ignored) {
        } catch (Exception ignored) {
        }
    }

    private Response processRequest(Request request, Map<String, ServerChannel> channelMap) {
        if (RequestType.CREATE_CHANNEL.equals(request.getType())) {
            String channelId = "CH-" + UUID.randomUUID();
            ServerChannel channel = new ServerChannel(channelId, getVirtualHost("/"));
            channelMap.put(channelId, channel);
            return Response.ok(channelId);
        }

        ServerChannel channel = channelMap.get(request.getChannelId());
        if (channel == null) {
            return Response.error("channel not found: " + request.getChannelId());
        }

        Response response = channel.process(request);
        if (RequestType.CLOSE_CHANNEL.equals(request.getType()) && response.isOk()) {
            channelMap.remove(request.getChannelId());
        }
        return response;
    }

    public VirtualHost getVirtualHost(String virtualHostName) {
        return virtualHostMap.get(virtualHostName);
    }

    public void addVirtualHost(VirtualHost virtualHost) {
        virtualHostMap.put(virtualHost.getVirtualHostName(), virtualHost);
    }
}
