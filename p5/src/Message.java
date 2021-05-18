import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Message {

    public static final String TRACKER_ADDRESS = "localhost";
    public static final int TRACKER_SERVER_PORT_NUM = 5280;
    public static final String USER_NAME = "client1"; // short usernames

    // Message Types
    public static final byte MSG_PEER_COMING_UP   = 1;
    public static final byte MSG_PEER_GOING_DOWN  = 2;
    public static final byte MSG_MESSAGE_TO_PEER  = 3;
    public static final byte MSG_PEER_UP_DATA     = 4;
    public static final byte MSG_PEER_DOWN_DATA   = 5;
    public static final byte MSG_ARE_YOU_UP       = 6;
    public static final byte MSG_ACK              = 100;

    public static final int MAX_MSG_SIZE = 1500;

    /*
     * Create message that Peer is coming up.
     *
     * @return The packet containing the coming up message.
     */
    public static DatagramPacket peerComingUpMessage()
            throws UnknownHostException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(MSG_PEER_COMING_UP);
        dos.writeBytes(USER_NAME);
        byte[] data = baos.toByteArray();
        InetAddress addr = null;
        if (TRACKER_ADDRESS.toLowerCase().equals("localhost")) {
            addr = InetAddress.getLoopbackAddress();
        } else {
            addr = InetAddress.getByName(TRACKER_ADDRESS);
        }
        DatagramPacket dgp = new DatagramPacket(
            data,
            data.length,
            addr,TRACKER_SERVER_PORT_NUM
        );
        return dgp;
    }

    /*
     * Create message that Peer is going down.
     *
     * @return The packet containing the going down message.
     */
    public static DatagramPacket peerGoingDownMessage()
            throws UnknownHostException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(MSG_PEER_GOING_DOWN);
        byte[] data = baos.toByteArray();
        InetAddress addr = null;
        if (TRACKER_ADDRESS.toLowerCase().equals("localhost")) {
            addr = InetAddress.getLoopbackAddress();
        } else {
            addr = InetAddress.getByName(TRACKER_ADDRESS);
        }
        DatagramPacket dgp = new DatagramPacket(
            data,
            data.length,
            addr,TRACKER_SERVER_PORT_NUM
        );
        return dgp;
    }

    /*
     * Create an acknowledgment of reception response.
     *
     * @param receivePacket The packet in which to put the acknowledgment
     * @return The packet containing the acknowledgment
     */
    public static DatagramPacket ackResponse(DatagramPacket receivePacket)
            throws UnknownHostException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(MSG_ACK);
        byte[] data = baos.toByteArray();
        InetAddress addr = null;
        if (TRACKER_ADDRESS.toLowerCase().equals("localhost")) {
            addr = InetAddress.getLoopbackAddress();
        } else {
            addr = InetAddress.getByName(TRACKER_ADDRESS);
        }
        DatagramPacket dgp = new DatagramPacket(
            data,
            data.length,
            addr,TRACKER_SERVER_PORT_NUM
        );
        return dgp;
    }

    /*
     * Create a packet array containing a message.
     *
     * @param peer The Peer to which to send the message
     * @param msg The message to send
     * @return The packet(s) containing the message
     */
    public static DatagramPacket[] messageToPeer(Peer peer, String msg)
            throws UnknownHostException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(MSG_MESSAGE_TO_PEER);
        if (msg.length() > MAX_MSG_SIZE - USER_NAME.length() - 3) {
            dos.writeBytes(
                peer.toString()
                + msg.substring(0, MAX_MSG_SIZE - USER_NAME.length() - 2)
            );
        } else {
            dos.writeBytes(peer.toString() + msg);
        }
        byte[] data = baos.toByteArray();
        InetAddress addr = null;
        if (TRACKER_ADDRESS.toLowerCase().equals("localhost")) {
            addr = InetAddress.getLoopbackAddress();
        } else {
            addr = InetAddress.getByName(TRACKER_ADDRESS);
        }
        DatagramPacket dgp = new DatagramPacket(
            data,
            data.length,
            addr,TRACKER_SERVER_PORT_NUM
        );
        DatagramPacket[] packets = new DatagramPacket[1];
        packets[0] = dgp;
        return packets;
    }
}
