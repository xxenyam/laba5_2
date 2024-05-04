package updWork;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UPDServer {
    private ActiveUsers userList = null;
    private DatagramSocket socket = null;
    private DatagramPacket packet = null;
    private InetAddress address = null;
    private int port = -1;

    public UPDServer(int serverPort) {
        try {
            socket = new DatagramSocket(serverPort);
        } catch (SocketException e) {
            System.out.println("Error: " + e);
        }
        userList = new ActiveUsers();
    }

    public void work(int bufferSize) {
        try {
            System.out.println("Server start...");
            while (true) {
                byte[] buffer = new byte[bufferSize];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                address = packet.getAddress();
                port = packet.getPort();
                User usr = new User(address, port);
                if (userList.isEmpty()) {
                    userList.add(usr);
                } else if (!userList.contains(usr)) {
                    userList.add(usr);
                }
                clear(buffer);

                System.out.println("Request from: " + address.getHostAddress() + " port: " + port);

                byte[] sendBuffer;
                for (int i = 0; i < userList.size(); i++) {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bout);
                    out.writeObject(userList.get(i));
                    sendBuffer = bout.toByteArray();
                    packet = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
                    socket.send(packet);
                }
                sendBuffer = "end".getBytes();
                packet = new DatagramPacket(sendBuffer, 0, address, port);
                socket.send(packet);
            }
        } catch(IOException e) {
            System.out.println("Error: " + e);
        } finally {
            System.out.println("Server end...");
            socket.close();
        }
    }

    private void clear(byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = 0;
        }
    }

    public static void main(String[] args) {
        (new UPDServer(1501)).work(256);
    }
}
