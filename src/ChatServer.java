import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private List<Socket> clients = new ArrayList<>();
    private List<String> usernames = new ArrayList<>();

    public ChatServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("服务器已启动，等待客户端连接...");

            while (true) {
                Socket client = serverSocket.accept();
                clients.add(client);
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String username = reader.readLine();

                synchronized (usernames) {
                    // 检查用户名是否已被使用
                    if (usernames.contains(username)) {
                        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                        writer.println("[Error] 用户名已被使用，请选择其他用户名。");
                        client.close();
                        continue;  // 继续等待下一个客户端连接
                    }

                    usernames.add(username);
                }
                // 发送用户列表给所有客户端
                broadcastMessage("在线用户：" + String.join(",", usernames));

                System.out.println("用户 '" + username + "' 已加入聊天室");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 其他方法不变...

    private void broadcastMessage(String message) {
        for (Socket client : clients) {
            try {
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                writer.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
}