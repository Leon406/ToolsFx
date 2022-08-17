package me.leon;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class GreetingClient {

    private String serverName;
    private int port;

    private Socket socket;

    public GreetingClient(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public void connect() {
        try {
            socket = new Socket(serverName, port);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void sendMsgData(String msg) throws Exception {
        System.out.println("Just connected to " + socket.getRemoteSocketAddress());
        OutputStream outToServer = socket.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);

        out.writeUTF(msg + " from " + socket.getLocalSocketAddress());
        InputStream inFromServer = socket.getInputStream();
        DataInputStream in = new DataInputStream(inFromServer);

        System.out.println("Server says " + in.readUTF());
        socket.close();
        System.out.println();
    }

    public void sendRawMsg(String msg) throws Exception {
        System.out.println("Just connected to " + socket.getRemoteSocketAddress());
        OutputStream outToServer = socket.getOutputStream();
        outToServer.write(msg.getBytes(StandardCharsets.UTF_8));

        InputStream inFromServer = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inFromServer));
        System.out.println("++++++++Server says ");
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
        }
        socket.close();
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        String serverName = "localhost";
        int port = 8888;

        for (int i = 0; i < 10; i++) {
            GreetingClient client = new GreetingClient(serverName, port);
            client.connect();
            client.sendMsgData("haha " + i);
        }

        GreetingClient client = new GreetingClient("www.baidu.com", 80);
        client.connect();
        client.sendRawMsg("HEAD / HTTP/1.1\r\nHost: www.baidu.com\r\nConnection: close\r\n\r\n");
    }
}
