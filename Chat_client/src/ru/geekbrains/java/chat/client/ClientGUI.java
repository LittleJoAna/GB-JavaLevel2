package ru.geekbrains.java.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/*
Отправлять сообщения в лог по нажатию кнопки или по нажатию клавиши Enter.
Создать лог в файле (записи должны делаться при отправке сообщений).
 */

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler {

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

        cbAlwaysOnTop.addActionListener(this);
        btnSend.addActionListener(this);
        tfMessage.addActionListener(this);

        log.setEnabled(false);
        JScrollPane scrollLog = new JScrollPane(log);

        JList<String> userList = new JList<>();
        JScrollPane scrollUsers = new JScrollPane(userList);

        String[] users = {"user1", "user2", "user3"};
        userList.setListData(users);
        scrollUsers.setPreferredSize(new Dimension(100, 0));

        add(panelTop, BorderLayout.NORTH);
        add(panelBottom, BorderLayout.SOUTH);
        add(scrollLog, BorderLayout.CENTER);
        add(scrollUsers, BorderLayout.EAST);

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
            tfMessage.requestFocus();
            String message = tfLogin.getText() + ": " + tfMessage.getText();
            if (message.length() != 0) {
                log.append(message + "\n");
                writeMessageInLog(message);
                tfMessage.setText("");
            } else {
                return;
            }
        } else {
            throw new RuntimeException("Неизвестный исходник: " + src);
        }
    }

    private void writeMessageInLog (String message) {
        try (PrintStream printStream = new PrintStream(new FileOutputStream("logChat.txt", true))){
            printStream.print(message + "\n");
            printStream.flush();
        } catch (FileNotFoundException e) {
            System.out.println("Запись в лог не удалась! Лог не найден!");
        }
    }
}
