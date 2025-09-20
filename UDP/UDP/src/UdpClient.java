import java.io.IOException;
import java.net.*;

public class UdpClient {

    public UdpClient() {
    }

    public static void main(String[] args) throws IOException {
        UdpClient udpClient = new UdpClient();
        udpClient.init();
    }

    public void init() throws IOException {
        String newMessage = "Hello UDP!";
        DatagramPacket datagramPacket = new DatagramPacket(
                newMessage.getBytes(),
                newMessage.length(),
                InetAddress.getByName("127.0.0.1"),
                8085);
        DatagramSocket datagramSocket = new DatagramSocket();
        System.out.println("Sending message: " + newMessage);
        datagramSocket.send(datagramPacket);
    }

}
