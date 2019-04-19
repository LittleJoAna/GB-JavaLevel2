package ru.geekbrains.java.chat.server.core;

import com.sun.xml.internal.ws.server.ServerRtException;
import ru.geekbrains.java.chat.library.Messages;
import ru.geekbrains.java.network.ServerSocketThread;
import ru.geekbrains.java.network.ServerSocketThreadListener;
import ru.geekbrains.java.network.SocketThread;
import ru.geekbrains.java.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    private ServerSocketThread serverSocketThread;
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");
    private final ChatServerListener listener;

    private Vector<SocketThread> clients = new Vector<>();

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
    }

    /**
     * Chat Server actions
     */
    public void start(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            putLog("Server is already running!");
            return;
        }
        serverSocketThread = new ServerSocketThread(this, "Server", port, 2000);
        SqlClient.connect();
        //putLog("Nick = " + SqlClient.getNick("Anastasiya", "123456"));
    }

    public void stop() {
        if (serverSocketThread == null || !serverSocketThread.isAlive()) {
            putLog("Server is not running!");
            return;
        }
        serverSocketThread.interrupt();
        SqlClient.disconnect();
    }

    private void putLog(String msg) {
        msg = dateFormat.format(System.currentTimeMillis()) + Thread.currentThread().getName() + ": " + msg;
        listener.onChatServerLog(this, msg);
    }

    private synchronized String getUsers () {
         StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAutorized()) continue;
            stringBuilder.append(client.getNickname()).append(Messages.DELIMITER);
        }
        return stringBuilder.toString();
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
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).close();
        }
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
        new ClientThread(this, threadName, socket);
    }

    @Override
    public void onAcceptTimeout(ServerSocketThread thread, ServerSocket serverSocket) {
        //putLog("Server is alive");
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
        ClientThread client = (ClientThread) socketThread;
        clients.remove(socketThread);
        if (client.isAutorized() && !client.isReconnecting()) {
            sendToAuthorizedCliens(Messages.getTypeBroadcast("Server", client.getNickname() + " вышел"));
        }
        sendToAuthorizedCliens(Messages.getUserList(getUsers()));
    }

    @Override
    public synchronized void onSocketIsReady(SocketThread thread, Socket socket) {
        clients.add(thread);
    }

    private void sendToAuthorizedCliens(String msg) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAutorized()) continue;
            client.sendMessage(msg);
        }
    }

    @Override
    public synchronized void onReceiveString(SocketThread thread, Socket socket, String msg) {
        ClientThread client = (ClientThread) thread;
        if (client.isAutorized()) {
            handleAuthorizedCliens(client, msg);
        } else {
            handleNonAuthorizedClients(client, msg);
        }
    }

    private void handleAuthorizedCliens (ClientThread client, String message) {
        String[] arr = message.split(Messages.DELIMITER);
        String msgType = arr[0];
        switch (msgType) {
            case Messages.TYPE_BROADCAST_SHORT:
                sendToAuthorizedCliens(Messages.getTypeBroadcast(client.getNickname(), arr[1]));
                break;
            default:
                client.msgFormatError(message);

        }
    }

    private void handleNonAuthorizedClients (ClientThread newClient, String message) {
        String[] arr = message.split(Messages.DELIMITER);
        if (arr.length != 3 || !arr[0].equals(Messages.AUTH_REQUEST)) {
            newClient.msgFormatError(message);
            return;
        }

        String login = arr[1];
        String password = arr[2];
        String nickname = SqlClient.getNick(login, password);

        if (nickname == null) {
            putLog("Invalid login/password: login ='" + login + "' password = '" + password + "'");
            newClient.authError();
            return;
        }

        ClientThread client = findClientByNickname(nickname);
        newClient.authAccept(nickname);
        if (client == null) {
            sendToAuthorizedCliens(Messages.getTypeBroadcast("Server", nickname + " вошел"));

        } else {
            client.reconnect();
            clients.remove(client);
        }
         sendToAuthorizedCliens(Messages.getUserList(getUsers()));
    }

    private synchronized ClientThread findClientByNickname (String nickname) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAutorized()) continue;
            if (client.getNickname().equals(nickname)) return client;
        }
        return null;
    }

    @Override
    public synchronized void onSocketThreadException(SocketThread thread, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }
}
