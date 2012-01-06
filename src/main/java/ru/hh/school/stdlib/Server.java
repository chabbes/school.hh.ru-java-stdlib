package ru.hh.school.stdlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private Substitutor3000 substitutor;
    private ServerSocket serverSocket;
    private long sleep;

    public Server(InetSocketAddress addr) {
        try {
            serverSocket = new ServerSocket(addr.getPort(), 0, addr.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
        substitutor = new Substitutor3000();
        sleep = 0;
    }

    public void run() throws IOException {
        Executor e = Executors.newCachedThreadPool();
        while(true) {
            final Socket socket = serverSocket.accept();
            System.out.println("Client connected");
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    BufferedReader in = null;
                    PrintWriter out = null;

                    String input[];

                    try {
                        Thread.sleep(sleep);

                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(socket.getOutputStream(), true);
                        input = in.readLine().split(" ");

                        String info = "Command with error! ";

                        if (input[0].equals("GET")) {
                            if (input.length == 2) {
                                info = "VALUE\n" + substitutor.get(input[1]) + "\n";
                            } else {
                                info += "USAGE: GET key\n";
                            }
                        } else if (input[0].equals("PUT")) {
                            if (input.length == 3) {
                                // Не используем пробел в качестве разделителя
                                substitutor.put(input[1], input[2]);
                                info = "OK\n";
                            } else {
                                info += "USAGE: PUT key value\n";
                            }
                        } else if (input[0].equals("SET") && input[1].equals("SLEEP")) {
                            if (input.length == 3) {
                                sleep = Integer.parseInt(input[2]);
                                info = "OK\n";
                            } else {
                                info += "USAGE: SET SLEEP number\n";
                            }
                        }

                        out.write(info);
                        out.flush();

                        socket.close();
                        System.out.println("Connection closed");
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            e.execute(r);
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }
}
