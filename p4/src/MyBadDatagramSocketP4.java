
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

/**
 * Mimic DatagramSocket, but inject errors to test robustness of rdt protocol: 
 * 1. Drop a packet, or
 * 2. Corrupt one bit.
 * Do not modify any of this code without talking to me first.
 * @author summerss
 */
public class MyBadDatagramSocketP4 extends DatagramSocket {
    private final int DEFAULT_ONE_OUT_OF = 3;
    private int dropOneOutOf = DEFAULT_ONE_OUT_OF, 
                corruptOneOutOf = DEFAULT_ONE_OUT_OF;
    private Random rand = new Random();
    
    public MyBadDatagramSocketP4() throws SocketException {
        super();
    }
    
    public MyBadDatagramSocketP4(int inPort) throws SocketException {
        super(inPort);
    }
    
    public void setDropOneOutOf(int inDropOneOutOf) {
        if (inDropOneOutOf < 0)
            throw new IllegalArgumentException();
        dropOneOutOf = inDropOneOutOf;
    }
    
    public void setCorruptOneOutOf(int inCorruptOneOutOf) {
        if (inCorruptOneOutOf < 0)
            throw new IllegalArgumentException();
        dropOneOutOf = inCorruptOneOutOf;
    }
    
    /*
    private String firstFewBytesStr(byte[] inArr) {
        String firstBytes = "";
        int numBytes = Math.min(inArr.length, 3);
        for (int i = 0; i < numBytes - 1; i++)
            firstBytes += String.format("0x%02x ", inArr[i]);
        firstBytes += String.format("0x%02x", inArr[numBytes - 1]);
        return firstBytes;
    }
    */
   
    @Override
    public void send(DatagramPacket inPkt) throws IOException {
        if (dropOneOutOf > 0 && rand.nextInt(dropOneOutOf) == 0) {
            if (inPkt.getLength() > 0) {
                //String firstBytes = firstFewBytesStr(inPkt.getData());
                //System.out.println("!!! DROPPED first bytes=" + firstBytes); 
            }     
            else
                //System.out.println("!!! DROPPED");
            return;
        }
        DatagramPacket sendPkt = inPkt;
        if (corruptOneOutOf > 0 && 
            inPkt.getLength() > 0 && 
            rand.nextInt(corruptOneOutOf) == 0) {
            int len = inPkt.getLength();
            byte[] corruptBytes = new byte[len],
                   inPktBytes = inPkt.getData();
            System.arraycopy(inPktBytes, 
                             0, 
                             corruptBytes, 
                             0, 
                             len);
            /*
            Flip exactly one random bit...
            */
            int rbyte = rand.nextInt(len), // a random byte
                rbit = 1 << rand.nextInt(Byte.SIZE); // a random bit 
            // Flip the random bit within the random byte:
            corruptBytes[rbyte] = (byte)(corruptBytes[rbyte] ^ rbit);
            //String firstBytes = firstFewBytesStr(corruptBytes);
            //System.out.println("!!! CORRUPTED, first bytes=" + firstBytes);
            sendPkt = new DatagramPacket(corruptBytes,
                                         0,
                                         corruptBytes.length,
                                         inPkt.getAddress(),
                                         inPkt.getPort());
        }
        // Sleep to give the receiver time to catch up
        try { Thread.sleep(100); } catch (Exception ex) { }
        super.send(sendPkt);
    }
}

