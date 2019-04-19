package ru.geekbrains.java.chat.client;

import ru.geekbrains.java.chat.library.Messages;
import ru.geekbrains.java.network.SocketThread;
import ru.geekbrains.java.network.SocketThreadListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Vector;

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, SocketThreadListener {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    private static final String WIN_TITLE = "Chat Client";
    private static final String[] EMPTY_LIST = new String[1];

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
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");

    private boolean shownIoErrors = false;
    private SocketThread socketThread;


    private ClientGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        setTitle(WIN_TITLE);

        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(cbAlwaysOnTop);
        panelTop.add(tfLogin);
        panelTop.add(tfPassword);
        panelTop.add(btnLogin);

        panelBottom.setVisible(false);
        panelBottom.add(btnDisconnect, BorderLayout.WEST);
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);

        log.setEditable(false);
        log.setLineWrap(true);

        JScrollPane scrollLog = new JScrollPane(log);

        JScrollPane scrollUsers = new JScrollPane(userList);
        scrollUsers.setPreferredSize(new Dimension(100, 0));

        cbAlwaysOnTop.addActionListener(this);
        btnSend.addActionListener(this);
        tfMessage.addActionListener(this);
        btnLogin.addActionListener(this);
        btnDisconnect.addActionListener(this);

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
        } else if (src == btnDisconnect) {
            disconnect();
        } else {
            throw new RuntimeException("Неизвестный исходник: " + src);
        }
    }

    private void disconnect() {
        socketThread.close();
    }

    void sendMessage() {
        String msg = tfMessage.getText();
        String userName = tfLogin.getText();
        if ("".equals(msg)) return;
//        log.append(userName + ": " + msg + "\n");
        tfMessage.setText(null);
        tfMessage.requestFocusInWindow();
        socketThread.sendMessage(Messages.getTypeBroadcastShort(msg));
    }

    void putLog (String msg) {
        if ("".equals(msg)) return;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });

    }

    private void handleMessage (String value) {
        String[] arr = value.split(Messages.DELIMITER);
        String msgType = arr[0];
        switch (msgType) {
            case Messages.AUTH_ACCEPT:
setTitle(WIN_TITLE + " nickname: " + arr[1]);
                break;
            case Messages.AUTH_DENIED:
putLog(value);
                break;
            case Messages.MSG_FORMAT_ERROR:
                socketThread.close();
                break;
            case Messages.USER_LIST:
                String users = value.substring(Messages.USER_LIST.length() + Messages.DELIMITER.length());
                String[] usersArr = users.split(Messages.DELIMITER);
                Arrays.sort(usersArr);
                userList.setListData(usersArr);
                break;
            case Messages.TYPE_BROADCAST:
                putLog(dateFormat.format(Long.parseLong(arr[1])) + "" + arr[2] + ": " + arr[3]);
                break;
            default:
                throw new RuntimeException("Неизвестное сообщение: " + value);
        }
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
        putLog("Соединение установлено");
    }

    @Override
    public void onSocketThreadStop(SocketThread socketThread) {
        setTitle(WIN_TITLE);
        userList.setListData(EMPTY_LIST);
        panelBottom.setVisible(false);
        panelTop.setVisible(true);
    }

    @Override
    public void onSocketIsReady(SocketThread thread, Socket socket) {
        String login = tfLogin.getText();
        String password = new String(tfPassword.getPassword());
        thread.sendMessage(Messages.getAuthRequest(login, password));
        panelTop.setVisible(false);
        panelBottom.setVisible(true);
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        handleMessage(msg);
    }

    @Override
    public void onSocketThreadException(SocketThread thread, Exception e) {
        putLog("Socket exception");
    }
}
