package ru.geekbrains.java.chat.server.core;

import ru.geekbrains.java.network.ServerSocketThread;
import ru.geekbrains.java.network.ServerSocketThreadListener;
import ru.geekbrains.java.network.SocketThread;
import ru.geekbrains.java.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    private ServerSocketThread serverSocketThread;
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");

    /**
     * Chat Server actions
     */
    public void start(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            putLog("Server is already running!");
            return;
        }
        serverSocketThread = new ServerSocketThread(this, "Server", port, 2000);
    }

    public void stop() {
        if (serverSocketThread == null && !serverSocketThread.isAlive()) {
            putLog("Server is not running!");
            return;
        }
        serverSocketThread.interrupt();
    }

    private void putLog(String msg) {
        msg = dateFormat.format(System.currentTimeMillis()) + Thread.currentThread().getName() + ": " + msg;
        System.out.println(msg);
    }

    /**
     * Server Socket Thread Listener methods
     */
    @Override
    public void onServerThreadStart(ServerSocketThread thread) {
        putLog("Server started");
    }

    @Override
    public void onServerThreadStop(ServerSocketThread thread) {
        putLog("Server stopped");
    }

    @Override
    public void onServerSocketCreated(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("ServerSocket created");
    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, Socket socket) {
        putLog("Client connected: " + socket);
        String threadName = "SocketThread: " + socket.getInetAddress() + ": " + socket.getPort();
        new SocketThread(this, threadName, socket);
    }

    @Override
    public void onAcceptTimeout(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("Server is alive");
    }

    @Override
    public void onServerThreadException(ServerSocketThread thread, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }

    /**
     * Socket Thread Listener methods
     */
    @Override
    public synchronized void onSocketThreadStart(SocketThread socketThread, Socket socket) {
        putLog("Server started");
    }

    @Override
    public synchronized void onSocketThreadStop(SocketThread socketThread) {
        putLog("Server stopped");
    }

    @Override
    public synchronized void onSocketIsReady(SocketThread thread, Socket socket) {
        putLog("Server is ready");
    }

    @Override
    public synchronized void onReceiveString(SocketThread thread, Socket socket, String msg) {
       thread.sendMessage("echo: " + msg);
    }

    @Override
    public synchronized void onSocketThreadException(SocketThread thread, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }
}
