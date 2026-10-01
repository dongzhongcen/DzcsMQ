package org.example.dzcsmq.mqclient;

import org.example.dzcsmq.common.FrameIO;
import org.example.dzcsmq.common.Request;
import org.example.dzcsmq.common.Response;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public class Connection implements Closeable {
    private final Socket socket;
    private final FrameIO frameIO;

    public Connection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.frameIO = new FrameIO(socket.getInputStream(), socket.getOutputStream());
    }

    public Channel createChannel() throws IOException, ClassNotFoundException {
        return new Channel(this);
    }

    Response sendRequest(Request request) throws IOException, ClassNotFoundException {
        frameIO.writeObject(request);
        return (Response) frameIO.readObject();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
