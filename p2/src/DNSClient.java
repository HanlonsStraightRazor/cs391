package p2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/* DNS header format:

*/

public class DNSClient {
    private static final String DNS_SERVER_ADDRESS = "8.8.8.8";
    private static final int DNS_SERVER_PORT = 53;
    // MSS = 576, so maximum data is 576 - 20 - 20 = 536
    private static final int MSS = 576 - 20 - 20;

    public static void main(String args[]) {
        InetAddress serverAddr = null;
        try {
            System.out.print("Verifying name server address...");
            serverAddr = InetAddress.getByName(DNS_SERVER_ADDRESS);
            System.out.println("Verified");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            // ID = 0x1234
            dos.writeShort(0x1234);
            // QR = 0
            // Opcode = 0
            // AA = 0
            // TC = 0
            // RD = 
            dos.writeShort(0x0100);
            // QDCOUNT
            dos.writeShort(0x0001);
            dos.writeShort(0x0);
            dos.writeShort(0x0);
            dos.writeShort(0x0);

            String hostParts[] = { "www", "google", "com" };
            for (int i = 0; i < hostParts.length; i++) {
                System.out.println("Writing: " + hostParts[i]);
                byte arr[] = hostParts[i].getBytes();
                dos.writeByte(arr.length);
                dos.write(arr);
            }
            // Null terminator
            dos.writeByte(0x00);
            // Type: 1 => A-Record Request
            dos.writeShort(0x0001);
            // Class: 1 => Internet Address
            dos.writeShort(0x0001);

            byte request[] = baos.toByteArray();
            System.out.println("Sending: " + request.length + " bytes");
            for (int i = 0; i < request.length; i++) {
                System.out.printf("%02x ", request[i]);
            }
            System.out.println();
            for (int i = 0; i < request.length; i++) {
                System.out.printf("%02d ", i);
            }
            System.out.println();

            System.out.println("Sending request...");
            DatagramSocket sock = new DatagramSocket();
            DatagramPacket pack = new DatagramPacket(
                request,
                request.length,
                serverAddr,
                DNS_SERVER_PORT
            );
            sock.send(pack);
            System.out.println("Sent");

            System.out.print("Waiting for response...");
            byte response[] = new byte[MSS];
            DatagramPacket recPack = new DatagramPacket(
                response,
                response.length
            );
            sock.receive(recPack);
            System.out.println("Received");
            sock.close();

            System.out.println("Received " + recPack.getLength() + " bytes");
            for (int i = 0; i < recPack.getLength(); i++) {
                System.out.printf("%02d %02x\n", i, response[i]);
            }
            System.out.println();

            DataInputStream dis = new DataInputStream(
                new ByteArrayInputStream(response)
            );
            System.out.printf("ID: 0x%x\n", dis.readShort());
            short flags = dis.readShort();
            System.out.printf("Flags: 0x%x\n", flags);
            System.out.printf("Questions: %d\n", dis.readShort());
            System.out.printf("Answers: %d\n", dis.readShort());
            System.out.printf("Additional Name Servers: %d\n", dis.readShort());
            System.out.printf("Additional Records: %d\n", dis.readShort());
        } catch(IOException ioex) {
            System.out.println(ioex);
        }
    }
}
