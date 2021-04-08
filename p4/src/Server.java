import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

public class Server {
    public static final int MSS = Long.BYTES + 2; // Do not modify
    public static final int ACK_SIZE = 3; // Do not modify
    public static final int LISTEN_PORT = 9998; // Do not modify
    public static final byte LOW = 0x01; // Do not modify
    public static final byte HIGH = 0x02; // Do not modify
    public static final byte WINNER = 0x03; // Do not modify
    private static DatagramSocket dgSocket = null; // Do not modify
    public static void main(String args[]) {
        // Do not modify any of the code in this method
        try {
            Random rand = new Random();
            System.out.print("SERVER Creating socket...");
            dgSocket = new MyBadDatagramSocketP4(LISTEN_PORT);
            System.out.println("good");
            long secret = rand.nextLong();
            System.out.println("SERVER Secret=" + secret);
            DatagramPacket ackPkt = null;
            byte[] ackData = new byte[ACK_SIZE];
            while (true) {
                DatagramPacket rcvPkt = new DatagramPacket(new byte[MSS], MSS);
                System.out.print("SERVER Waiting for guess...");
                dgSocket.receive(rcvPkt);
                ackPkt = new DatagramPacket(ackData,
                                            ackData.length,
                                            rcvPkt.getAddress(),
                                            rcvPkt.getPort());
                if (packetHasWrongSeqNum(rcvPkt)) {
                    System.out.println("wrong sequence number");
                    makeAckPktNotOK(ackPkt);
                }
                else if (packetCorrupt(rcvPkt)) {
                    System.out.println("corrupt");
                    makeAckPktNotOK(ackPkt);
                }
                else  {
                    long guess = extractGuess(rcvPkt.getData());
                    System.out.print("got it, guess=" + guess + ", ");
                    byte response = WINNER;
                    if (guess < secret) {
                        response = LOW;
                        System.out.println("too low");
                    }
                    else if (guess > secret) {
                        response = HIGH;
                        System.out.println("too high");
                    }
                    else {
                        System.out.println("winner!");
                    }
                    ackData[1] = response;
                    makeAckPktOK(ackPkt);
                    updateSeqNum();
                }
                System.out.print("SERVER Sending response...");
                dgSocket.send(ackPkt);
                System.out.println("good");
            }
        }
        catch (IOException ex) {
            System.out.println("error: " + ex);
        }
    }

    //private static int expSeqNum = ;

    /*
     * Give comment blocks for ALL of the following methods...
     */


    private static void updateSeqNum() {
        // You can modify only the code inside this method
    }

    private static void makeAckPktOK(DatagramPacket inAckPkt) {
        // You can modify only the code inside this method
    }

    private static void makeAckPktNotOK(DatagramPacket inAckPkt) {
        // You can modify only the code inside this method
    }

    private static long extractGuess(byte[] data) throws IOException {
        // You can modify only the code inside this method
        return 0;
    }

    private static boolean packetHasWrongSeqNum(DatagramPacket inPkt) {
        // You can modify only the code inside this method
        return false;
    }

    private static boolean packetCorrupt(DatagramPacket inPkt) {
        // You can modify only the code inside this method
        return false;
    }

    // You may add extra methods...
}
