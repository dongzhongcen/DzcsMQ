package org.example.dzcsmq.common;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class FrameIO {
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;

    public FrameIO(InputStream inputStream, OutputStream outputStream) throws IOException {
        this.objectOutputStream = new ObjectOutputStream(outputStream);
        this.objectOutputStream.flush();
        this.objectInputStream = new ObjectInputStream(inputStream);
    }

    public synchronized void writeObject(Object object) throws IOException {
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    public synchronized Object readObject() throws IOException, ClassNotFoundException {
        try {
            return objectInputStream.readObject();
        } catch (EOFException e) {
            throw e;
        }
    }
}
