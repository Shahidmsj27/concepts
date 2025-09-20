import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpServer {

    DatagramSocket datagramSocket;
    byte[] buffer;

    public UdpServer() throws SocketException {
        this.datagramSocket = new DatagramSocket(8085);
        this.buffer = new byte[1024];
    }

    public static void main(String[] args) throws SocketException {
        UdpServer udpServer = new UdpServer();
        try {
            udpServer.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() throws IOException {
        try {
            System.out.println("Server started");
            DatagramPacket datagramPacket = new DatagramPacket(this.buffer, this.buffer.length);
            while (true) {
                System.out.println("Listening to port 8085...");
                this.datagramSocket.receive(datagramPacket);
                System.out.println(new String(datagramPacket.getData(), 0, datagramPacket.getLength()));
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
