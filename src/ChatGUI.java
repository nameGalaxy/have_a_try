import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintWriter writer;
    private String username;

    public ChatGUI() {
        setTitle("Chat GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        // 创建聊天区域
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // 创建消息输入框和发送按钮
        messageField = new JTextField();
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            System.out.println("成功连接到服务器：" + socket);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // 获取用户名
            username = JOptionPane.showInputDialog("请输入您的用户名：");

            // 创建一个新的线程来处理接收消息
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        if (message.startsWith("[Join]")) {
                            String joinUsername = message.substring(6);
                            chatArea.append("'" + joinUsername + "' 加入聊天室\n");
                        } else if (message.startsWith("[Quit]")) {
                            String quitUsername = message.substring(6);
                            chatArea.append("'" + quitUsername + "' 退出聊天室\n");
                        } else {
                            chatArea.append(message + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            // 发送用户名到服务器
            writer.println(username);
            chatArea.append("您已加入聊天室\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (message.equals(".quit")) {
            System.exit(0);
        }
        writer.println("[" + username + "]: " + message);
        messageField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatGUI();
            }
        });
    }
}
//你好