import java.io.IOException;
import java.net.DatagramSocket;
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
     *
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
            // Fill in missing details
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
            // Fill in missing details
            System.out.println("Server: Sent going down message.");
            // Fill in missing details
        } catch (Exception ex) {
            System.out.println("Server Failed to send I'm down");
            System.out.println("Server Exception: " + ex);
        }
    }

    @Override
    public void run() {
        // Fill in missing details
    }
}
