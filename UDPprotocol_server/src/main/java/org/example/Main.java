package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class Main {
    /* Порт сервера, к которому собирается
  подключиться клиентский сокет */
    public final static int SERVICE_PORT = 50001;
    public final static String exitWord = "exit";


    public static void main(String[] args) throws IOException {
        try {

            System.out.println("Enter your ip address: ");
            InetAddress IPAddressSender;
            Scanner scanner = new Scanner(System.in);
            IPAddressSender = InetAddress.getByName(scanner.nextLine());

            System.out.println("Enter remote ip address: ");
            InetAddress IPAddressReciever;
            IPAddressReciever = InetAddress.getByName(scanner.nextLine());

            DatagramSocket serverSocket = new DatagramSocket(SERVICE_PORT, IPAddressSender);

            Thread receiverThread = new Thread(new ReceiverUDP(serverSocket));
            receiverThread.start();

            System.out.println("Now talk!");

            while (true)
            {
                byte[] sendingDataBuffer = new byte[1024];

                String message = scanner.nextLine();
                sendingDataBuffer = message.getBytes();

                if (message.equals(exitWord)) {
                        System.out.println("Вы завершили работу при помощи ключевого слова. ");
                        DatagramPacket senderPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddressReciever, SERVICE_PORT);
                        serverSocket.send(senderPacket);
                        break;
                }

                DatagramPacket senderPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddressReciever, SERVICE_PORT);
                serverSocket.send(senderPacket);
            }
           System.exit(0);


        } catch (SocketException e) {
            System.out.println("Пользователь с введенным вами IP-адресом уже общается в системе. Попробуйте использовать другой адрес");
        }


    }
    public static class ReceiverUDP implements Runnable{

        private DatagramSocket serverSocket;
        public ReceiverUDP(DatagramSocket serverSocket){

            this.serverSocket = serverSocket;
        }
        public void run(){
            try {
                while (true) {

                    byte[] receivingDataBuffer = new byte[1024];
                    byte[] savePlace = new byte[1024];

                    DatagramPacket receivePacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
                    // System.out.println("Waiting for a client to connect...");
                    // Получите данные от клиента и сохраните их в inputPacket
                    serverSocket.receive(receivePacket);

                     for (int i = 0; i < receivingDataBuffer.length; i++) {
                            savePlace[i] = receivingDataBuffer[i];
                         if (receivingDataBuffer[i] == 0) {
                             receivingDataBuffer = new byte[i];
                             break;
                         }
                     }
                    for (int i = 0; i < receivingDataBuffer.length; i++){
                       receivingDataBuffer[i] =  savePlace[i];
                    }


                    String receivedData = new String(receivingDataBuffer, StandardCharsets.UTF_8);

                    if (receivedData.equals(exitWord)) {
                        System.out.println("Ваш собеседник завершил работу при помощи ключевого слова. ");
                        System.exit(0);
                    }

                    System.out.println("Sent from the client: " + receivedData);


                }

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}