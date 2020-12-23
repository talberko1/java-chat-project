package com.github.server.base;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class BaseServer implements Runnable {
    public static final int MINIMUM_PORT_NUMBER = 0;
    public static final int MAXIMUM_PORT_NUMBER = 65536;
    public static final int DEFAULT_SERVER_PORT = 6969;
    protected ServerSocket serverSocket;
    protected int port;
    protected boolean running;

    public BaseServer(String ipAddress, int portNumber) throws IOException {
        port = (portNumber > MINIMUM_PORT_NUMBER && portNumber < MAXIMUM_PORT_NUMBER) ? portNumber : DEFAULT_SERVER_PORT;
        serverSocket = new ServerSocket(port, 0, InetAddress.getByName(ipAddress));
        running = false;
    }

    public void run() {
        running = true;
        while (running) {
            try {
                Socket client = serverSocket.accept();
                handleClient(client);
            } catch (IOException e) {
                onServerError(e);
            }
        }
    }

    protected abstract void handleClient(Socket client) throws IOException;

    protected abstract void onServerError(IOException e);

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
