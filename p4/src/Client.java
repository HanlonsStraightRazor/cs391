import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    // You do not have to give a comment block for this method
    private static String getInput(Scanner inFromUser) {
        // Do not modify
        System.out.print("CLIENT Guess a number: ");
        return inFromUser.next().toLowerCase();
    }

    // You do not have to give a comment block for this method
    public static void main(String[] args) {
        // Do not modify
        DatagramSocket dgSocket = null;
        InetAddress serverAddr = null;
        try {
            System.out.print("CLIENT Looking up server...");
            if (args.length < 1) {
                serverAddr = InetAddress.getLoopbackAddress();
            }
            else {
                serverAddr = InetAddress.getByName(args[0]);
            }
            System.out.println("good");
            dgSocket = new MyBadDatagramSocketP4();
            Scanner inFromUser = new Scanner(System.in);
            boolean done = false;
            while (!done) {
                String strFromUser = getInput(inFromUser);
                if (!strFromUser.equals("quit")) {
                    long guess = Long.parseLong(strFromUser);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);
                    // Guess message: [ header | byte[8] | checksum ]
                    dos.write(0); // header
                    dos.writeLong(guess); // data
                    dos.write(0); // checksum
                    DatagramPacket sndPkt = new DatagramPacket(baos.toByteArray(),
                                                               baos.size(),
                                                               serverAddr,
                                                               Server.LISTEN_PORT);
                    byte response = sendGuessAndGetResponse(sndPkt, dgSocket);
                    if (response == Server.LOW) {
                        System.out.println("too low");
                    }
                    else if (response == Server.HIGH) {
                        System.out.println("too high");
                    }
                    else if (response == Server.WINNER) {
                        System.out.println("winner!");
                        done = true;
                    }
                }
                else {
                    done = true;
                }
            }
            System.out.println("CLIENT Done, normal termination");
            inFromUser.close();
            dgSocket.close();
        }
        catch (IOException ex) {
            System.out.println("error: " + ex);
        }
    }

    private static int expSeqNum = 0;

    /*
     * Sends a guess to the Server and returns its response.
     * If the received response is wrong,
     * it resends the packet and tries again.
     * @param sndPkt The packet to send to the Server
     * @param dgSocket The socket over which to send the packet
     * @exception IOException If there is an error with the socket
     * @return The response from the Server if the packet is good
     */
    private static byte sendGuessAndGetResponse(DatagramPacket sndPkt,
                                                DatagramSocket dgSocket) throws IOException {
        // Add code to implement the alternating-bit protocol -- only add code
        DatagramPacket rcvPkt = new DatagramPacket(new byte[Server.ACK_SIZE], Server.ACK_SIZE);
        sndPkt.getData()[0] = (byte) expSeqNum;
        sndPkt.getData()[9] = checkSum(sndPkt.getData(), 9);
        boolean sent = false;
        while (!sent) {
            System.out.print("CLIENT Sending guess...");
            dgSocket.send(sndPkt);
            System.out.println("good");
            System.out.print("CLIENT Waiting for response...");
            dgSocket.receive(rcvPkt);
            System.out.print("got it, ");
            if (rcvPkt.getData()[2] != checkSum(rcvPkt.getData(), 2)) {
                System.err.println("corrupt");
                continue;
            }
            if (rcvPkt.getData()[0] != expSeqNum) {
                System.err.println("wrong sequence number");
                continue;
            }
            sent = true;
        }
        expSeqNum ^= 1;
        return rcvPkt.getData()[1];
    }

    /*
     * Compute the XOR checksum of a given
     * byte array over a specific length.
     * @param data The data for which to generate the checksum
     * @param len The length of the data field
     * @return The XOR checksum
     */
    // You fill in the missing details -- compute the XOR checksum
    public static byte checkSum(byte[] data, int len) {
        byte cs = 0;
        for (int i = 0; i < len; i++) {
            cs ^= data[i];
        }
        return cs;
    }
}
