package me.leon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class GreetingServer extends Thread {
    private ServerSocket serverSocket;

    public GreetingServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        while (true) {
            try {
                System.out.println(
                        "Waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket client = serverSocket.accept();

                System.out.println("Just connected to " + client.getRemoteSocketAddress());
                InputStream inputStream = client.getInputStream();
                String msg =
                        "Thank you for connecting to "
                                + client.getLocalSocketAddress()
                                + "\tGoodbye!";

                DataInputStream in = new DataInputStream(inputStream);

                try {
                    System.out.println(in.readUTF());
                    DataOutputStream out = new DataOutputStream(client.getOutputStream());
                    out.writeUTF(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                client.close();
                System.out.println();

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        if (args.length != 0) port = Integer.parseInt(args[0]);
        try {
            Thread t = new GreetingServer(port);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {}
    }
}
