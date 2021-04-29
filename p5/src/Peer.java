import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Peer {
    private final int INET_ADDR_LEN = 4;

    private String userName;
    private InetAddress address;
    private int port;

    /*
     * Construct a Peer from an array of bytes in the format:
     * First byte is a type (ignore it),
     * next INET_ADDR_LEN bytes is the address,
     * next 4 bytes is the port on which the Peer
     * being constructed is listening,
     * and the remaining bytes is the username.
     *
     * @param msgBytes Raw data used to create Peer
     * @param length The length of msgBytes parameter
     */
    public Peer(byte[] msgBytes, int length)
            throws UnknownHostException, IOException {
        // Use a ByteArrayOutputStream
        // Use a DataInputStream
        // Use static InetAddress method getByAddress.
    }

    /*
     * Get the InetAddress of the Peer.
     *
     * @return The InetAddress of the Peer
     */
    public InetAddress getInetAddress() {
        return address;
    }

    /*
     * Get the port of the Peer.
     *
     * @return The port of the Peer
     */
    public int getPort() {
        return port;
    }

    /*
     * Two peers are equal if their IP addresses
     * and port numbers are both equal.
     *
     * @param peer The Peer with which to compare this one
     * @return Whether this Peer is equal to another
     */
    public boolean equals(Peer peer) {
        return address == peer.getInetAddress() && port == peer.getPort();
    }

    @Override
    public String toString() {
        return userName + ":" + address.getHostAddress() + "/" + port;
    }
}
