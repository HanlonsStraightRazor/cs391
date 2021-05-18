import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.util.ArrayList;

public class ServerTask implements Runnable {

    private ArrayList<Peer> peerArrayList = new ArrayList<Peer>();
    private JList<Peer> lstPeers;
    private JTextArea txtRcvMsg;
    private DatagramSocket dgs;

    /*
     * Constructor for ServerTask.
     * TODO: Finish comment block
     * @param inLstPeers
     * @param inTxtRcvMsg
     */
    public ServerTask(
            JList<Peer> inLstPeers,
            JTextArea inTxtRcvMsg)
            throws IOException {
        lstPeers = inLstPeers;
        txtRcvMsg = inTxtRcvMsg;
        dgs = new DatagramSocket();
    }

    /*
     * Update the list keeping track of
     * the number of Peer connections.
     */
    private void updatePeerList() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Peer[] peer = new Peer[peerArrayList.size()];
                for (int i = 0; i < peerArrayList.size(); i++) {
                    peer[i] = peerArrayList.get(i);
                }
                lstPeers.setListData(peer);
            }
        });
    }

    /*
     * Announce to Peers that the server is up.
     */
    public void sendComingUpMessage() {
        try {
            System.out.println("Server: Going to try to send I'm up.");
            dgs.send(Message.peerComingUpMessage());
            System.out.println("Server: Sent coming up message.");
        } catch (Exception ex) {
            System.out.println("Server Failed to send I'm up.");
            System.out.println("Server Exception: " + ex);
        }
    }

    /*
     * Announce to Peers that server is going down,
     * then shut down.
     */
    public void closeDown() {
        try {
            System.out.println("Server: Going to try to send I'm down.");
            dgs.send(Message.peerGoingDownMessage());
            System.out.println("Server: Sent going down message.");
            dgs.close();
        } catch (Exception ex) {
            System.out.println("Server Failed to send I'm down");
            System.out.println("Server Exception: " + ex);
        }
    }

    @Override
    public void run() {
        sendComingUpMessage();
        try {
            while (true) {
                byte[] buffer = new byte[Message.MAX_MSG_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                dgs.receive(packet);
                byte[] bytes = new byte[packet.getLength()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = buffer[i];
                }
                switch (bytes[0]) {
                    case Message.MSG_MESSAGE_TO_PEER:
                        dgs.send(Message.ackResponse(packet));
                        byte[] msg = new byte[bytes.length - 1];;
                        for (int i = 0; i < msg.length; i++) {
                            msg[i] = bytes[i + 1];
                        }
                        txtRcvMsg.append(new String(msg));
                        break;
                    case Message.MSG_PEER_UP_DATA:
                        Peer p = new Peer(bytes, bytes.length);
                        if (!peerArrayList.contains(p)) {
                            peerArrayList.add(p);
                            updatePeerList();
                        }
                        break;
                    case Message.MSG_PEER_DOWN_DATA:
                        Peer q = new Peer(bytes, bytes.length);
                        if (peerArrayList.contains(q)) {
                            peerArrayList.remove(q);
                            updatePeerList();
                        }
                        break;
                    case Message.MSG_ARE_YOU_UP:
                        dgs.send(Message.ackResponse(packet));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
