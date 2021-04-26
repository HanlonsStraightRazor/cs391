import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
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

    private static int expSeqNum = 0;

    /*
     * If the expected sequence number is 0, makes it 1.
     * If the expected sequence number is 1, makes it 0.
     */
    private static void updateSeqNum() {
        expSeqNum ^= 1;
    }

    /*
     * Attaches an ACK header and a checksum to a packet.
     * @param inPkt The packet to send to the Client
     */
    private static void makeAckPktOK(DatagramPacket inAckPkt) {
        byte[] array = inAckPkt.getData();
        array[0] = (byte) expSeqNum;
        array[2] = (byte) (array[0] ^ array[1]);
    }

    /*
     * Attaches a NACK header to a packet.
     * @param inPkt The packet to send to the Client
     */
    private static void makeAckPktNotOK(DatagramPacket inAckPkt) {
        inAckPkt.getData()[0] = (byte) expSeqNum;
    }

    /*
     * Extracts the numerical guess from a packet sent from the Client.
     * @param data The raw packet from the Client
     * @return The Client's guess
     * @exception IOException If the packet is the incorrect size
     */
    private static long extractGuess(byte[] data) throws IOException {
        DataInputStream stream = new DataInputStream(
            new ByteArrayInputStream(data, 1, Long.BYTES)
        );
        return stream.readLong();
    }

    /*
     * Determines if a response packet from
     * the Client has an unexpected sequence number.
     * @param inPkt The packet received from the Client
     * @return Whether the packet's sequence number is expected
     */
    private static boolean packetHasWrongSeqNum(DatagramPacket inPkt) {
        return inPkt.getData()[0] == ((byte) expSeqNum) ? true : false;
    }

    /*
     * Whether a packet from the Client has a
     * checksum matching the one computed on this end.
     * @param inPkt The packet received from the Client
     * @return Whether the received packet is corrupt
     */
    private static boolean packetCorrupt(DatagramPacket inPkt) {
        byte checksum = 0;
        for (int i = 0; i < MSS - 1; i++) {
            checksum = (byte) (checksum ^ inPkt.getData()[i]);
        }
        return inPkt.getData()[MSS - 1] == checksum ? true : false;
    }
}
