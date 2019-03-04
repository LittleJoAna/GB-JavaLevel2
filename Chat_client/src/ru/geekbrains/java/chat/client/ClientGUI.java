package ru.geekbrains.java.chat.client;

import ru.geekbrains.java.network.SocketThread;
import ru.geekbrains.java.network.SocketThreadListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

/*
Отправлять сообщения в лог по нажатию кнопки или по нажатию клавиши Enter.
Создать лог в файле (записи должны делаться при отправке сообщений).
 */

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, SocketThreadListener {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private final JTextArea log = new JTextArea();
    private final JPanel panelTop = new JPanel(new GridLayout(2, 3));
    private final JTextField tfIPAddress = new JTextField("127.0.0.1");
    private final JTextField tfPort = new JTextField("8189");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always On Top");
    private final JTextField tfLogin = new JTextField("Anastasiya");
    private final JPasswordField tfPassword = new JPasswordField("123456");
    private final JButton btnLogin = new JButton("Login");

    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton btnDisconnect = new JButton("Disconnect");
    private final JTextField tfMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");

    private final JList<String> userList = new JList<>();

    private boolean shownIoErrors = false;
    private SocketThread socketThread;


    private ClientGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        setTitle("Chat Client");

        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(cbAlwaysOnTop);
        panelTop.add(tfLogin);
        panelTop.add(tfPassword);
        panelTop.add(btnLogin);

        panelBottom.add(btnDisconnect, BorderLayout.WEST);
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);

        log.setEnabled(false);
        JScrollPane scrollLog = new JScrollPane(log);

        JScrollPane scrollUsers = new JScrollPane(userList);
        scrollUsers.setPreferredSize(new Dimension(100, 0));

        cbAlwaysOnTop.addActionListener(this);
        btnSend.addActionListener(this);
        tfMessage.addActionListener(this);
        btnLogin.addActionListener(this);

        add(scrollUsers, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);
        add(panelBottom, BorderLayout.SOUTH);
        add(scrollLog, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] ste = e.getStackTrace();
        String msg;
        if (ste.length == 0) {
            msg = "Пустой стектрейс";
        } else {
            msg = e.getClass().getCanonicalName() + " " + e.getMessage() + "\n" +
                    "\t\t at " + ste[0];
        }

        JOptionPane.showMessageDialog(this, msg, "Exception!", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
        } else if (src == btnSend || src == tfMessage) {
            sendMessage();
        } else if (src == btnLogin) {
            connect();
        } else {
            throw new RuntimeException("Неизвестный исходник: " + src);
        }
    }

    void sendMessage() {
        String msg = tfMessage.getText();
        String userName = tfLogin.getText();
        if ("".equals(msg)) return;
        log.append(userName + ": " + msg + "\n");
        tfMessage.setText(null);
        tfMessage.requestFocusInWindow();
        socketThread.sendMessage(msg);
    }

    void putLog (String msg, String userName) {
        if ("".equals(msg)) return;
        log.append(userName + " : " + msg + "\n");
    }

    void connect() {
        Socket socket = null;
        try {
            socket = new Socket(tfIPAddress.getText(), Integer.parseInt(tfPort.getText()));
        } catch (IOException e) {
            log.append("Exception: " + e.getMessage());
        }
        socketThread = new SocketThread(this, "Client thread", socket);
    }

    /**
     * Socket Thread Listener methods
     */

    @Override
    public void onSocketThreadStart(SocketThread socketThread, Socket socket) {
        putLog("Socket start", "system");
    }

    @Override
    public void onSocketThreadStop(SocketThread socketThread) {
        putLog("Socket start", "system");
    }

    @Override
    public void onSocketIsReady(SocketThread thread, Socket socket) {
        putLog("Socket start", "system");
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        SwingUtilities.invokeLater(() -> putLog(msg, socket.getInetAddress().toString()));
    }

    @Override
    public void onSocketThreadException(SocketThread thread, Exception e) {
        putLog("Socket start", "system");
    }
}
