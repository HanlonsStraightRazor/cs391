import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

public class Server {
	public static final int MSS = Long.BYTES + 2; // Maximum segment size
	public static final int LISTEN_PORT = 9999;
	public static final byte LOW = 0x03; // bit pattern 0000 0011
	public static final byte HIGH = 0x0c; // bit pattern 0000 1100
	public static final byte WINNER = 0x30; // bit pattern 0011 0000 
	public static void main(String args[]) {
		// Do not modify any of the code in this method
		DatagramSocket dgSocket = null;
		try {
			Random rand = new Random();
			System.out.print("SERVER Creating socket...");
			dgSocket = new MyBadDatagramSocketP3(LISTEN_PORT);
			System.out.println("good");
			long secret = rand.nextLong();
			System.out.println("SERVER Secret=" + secret);
			System.out.print("SERVER Waiting for guess...");
			byte[] rcvData = new byte[MSS];
			DatagramPacket rcvPkt = new DatagramPacket(rcvData, rcvData.length);
			while (!receivePacket(rcvPkt, dgSocket));
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rcvData));
			long guess = dis.readLong();
			byte[] sndData = new byte[1];
			DatagramPacket sndPkt = new DatagramPacket(sndData, 
					                                   1, 
					                                   rcvPkt.getAddress(), 
					                                   rcvPkt.getPort());
			while (guess != secret) {
				System.out.print("got it, guess=" + guess + ", ");
				byte response = HIGH;
				if (guess < secret) {
					response = LOW;
					System.out.println("too low");
				}
				else {
					System.out.println("too high");
				}
				sndData[0] = response;
				System.out.print("SERVER Sending response...");
				dgSocket.send(sndPkt);
				System.out.println("good");
				System.out.print("SERVER Waiting for guess...");
				while (!receivePacket(rcvPkt, dgSocket));
				dis = new DataInputStream(new ByteArrayInputStream(rcvData));
				guess = dis.readLong();
			    sndPkt = new DatagramPacket(sndData, 
                                            1, 
                                            rcvPkt.getAddress(), 
                                            rcvPkt.getPort());
			}
			System.out.println("got it, guess=" + guess + ", winner!");
			sndData[0] = WINNER;
			System.out.print("SERVER Sending response...");
			dgSocket.send(sndPkt);
			System.out.println("good");
			System.out.print("SERVER Shutting down...");
			dgSocket.close();
			System.out.println("done, normal termination");
		}
		catch (IOException ex) {
			System.out.println("error: " + ex);
		}
	}
	
	/**
	 * Receives an incoming DatagramPacket through a given DatagramSocket. This method should
	 * block until a DatagramPacket is received.
	 * @param inPkt The DatagramPacket into which the incoming DatagramPacket is received.
	 * @param inSocket The DatagramSocket through which a DatagramPacket is received.
	 * @throws IOException is thrown if receive fails. 
	 */
	private static boolean receivePacket(DatagramPacket inPkt, DatagramSocket inSocket) throws IOException {
		// You can modify only the code inside this method
		inSocket.receive(inPkt);
		return true;
	}
	
	// You may add extra methods...
}
