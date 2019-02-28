package lesson_4.server.core;

import lesson_4.network.ServerSocketThread;

public class ChatServer {

    private ServerSocketThread serverSocketThread;

    public void start(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            System.out.println("Server is already running!");
            return;
        }
        serverSocketThread = new ServerSocketThread("Server", port);
    }

    public void stop() {
        if (serverSocketThread == null && !serverSocketThread.isAlive()) {
            System.out.println("Server is not running!");
            return;
        }
        serverSocketThread.interrupt();
    }
}
