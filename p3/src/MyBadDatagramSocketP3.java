
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

/**
 * Mimic DatagramSocket, but inject single-bit errors.
 * Do not modify any of this code without talking to me first.
 * @author summerss
 */
public class MyBadDatagramSocketP3 extends DatagramSocket {
    private final int DEFAULT_ONE_OUT_OF = 5;
    private final int MINIMUM_ONE_OUT_OF = 2;
    private int corruptOneOutOf = DEFAULT_ONE_OUT_OF;
    private Random rand = new Random();

    public MyBadDatagramSocketP3() throws SocketException {
        super();
    }

    public MyBadDatagramSocketP3(int inPort) throws SocketException {
        super(inPort);
    }

    public void setDropOneOutOf(int inDropOneOutOf) {
        if (inDropOneOutOf < MINIMUM_ONE_OUT_OF)
            throw new IllegalArgumentException();
    }

    public void setCorruptOneOutOf(int inCorruptOneOutOf) {
        if (inCorruptOneOutOf < MINIMUM_ONE_OUT_OF)
            throw new IllegalArgumentException();
    }

    @Override
    public void send(DatagramPacket inPkt) throws IOException {
        DatagramPacket sendPkt = inPkt;
        if (corruptOneOutOf > 0 &&
            inPkt.getLength() > 0 &&
            rand.nextInt(corruptOneOutOf) == 0) {
            int len = inPkt.getLength();
            // Copy the data so the client doesn't see the corruption
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

