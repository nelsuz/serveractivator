package com.develoqu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3000);
        while(true){
            Socket socket = serverSocket.accept();

            new Thread (() -> {
                try (Socket sock = socket){
                    serve(sock);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void serve (Socket socket) throws IOException, ClassNotFoundException {
        Base keyBase = new Base("base");
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        while(true) {
            Object readObject = objectInputStream.readObject();
            System.out.println(readObject.toString());
            String[] line = readObject.toString().split(" ");
            if(line[0].equals("ckey") && line[1] != null && line[2] != null){
                objectOutputStream.writeObject(keyBase.checkKey(line[1], line[2]));
            }

            if(line[0].equals("akey") && line[1].equals("c8837b23ff8aaa8a2dde915473ce0991")
                    && Integer.parseInt(line[2]) > 0){
                for(int i = 0; i < Integer.parseInt(line[2]); i++){
                    objectOutputStream.writeObject(keyBase.addNewKey());
                }
            }

            if(line[0].equals("stop") && line[1].equals("c8837b23ff8aaa8a2dde915473ce0991")){
                System.exit(0);
                objectOutputStream.writeObject("Server is stopped");
            }

        }
    }
}
