import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Client extends JFrame {

    private DatagramSocket clientSocket;
    private ServerTask serverTask;

    public Client() {
        initComponents();









    }

    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lstPeers = new javax.swing.JList<Peer>();
        jLabel1 = new javax.swing.JLabel();
        txtMessage = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnSend = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtRcvMsg = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(Message.USER_NAME);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closingDown(evt);
            }

            public void windowClosed(java.awt.event.WindowEvent evt) {
                closingDown(evt);
            }
        });

        lstPeers.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        jScrollPane1.setViewportView(lstPeers);

        jLabel1.setText("Current Peers");

        jLabel2.setText("Message To Send");

        btnSend.setText("Send Message To Selected  Peer");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        txtRcvMsg.setColumns(20);
        txtRcvMsg.setRows(5);
        jScrollPane2.setViewportView(txtRcvMsg);

        jLabel3.setText("Received Messages");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING,
                                                        0, 0, Short.MAX_VALUE)
                                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addGap(38, 38, 38))
                                .addComponent(txtMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                .addComponent(btnSend, javax.swing.GroupLayout.Alignment.TRAILING,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)))
                .addGap(21, 21, 21)
                .addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 476,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                .addGap(40, 40, 40)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1)
                        .addComponent(jLabel3))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                        .createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 195,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18).addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addComponent(btnSend).addGap(113, 113, 113))
                        .addGroup(layout.createSequentialGroup().addComponent(jScrollPane2,
                                javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()))));

        pack();
    }

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {
        try {












        } catch (Exception ex) {
            System.out.println("Client Failed to Send Message.");
            System.out.println("Client Exception: " + ex.toString());
        }
    }

    private void closingDown(java.awt.event.WindowEvent evt) {
















    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    private javax.swing.JButton btnSend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<Peer> lstPeers;
    private javax.swing.JTextField txtMessage;
    private javax.swing.JTextArea txtRcvMsg;
}
