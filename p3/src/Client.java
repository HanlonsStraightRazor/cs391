import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Client {

    // You do not have to give a comment block for this method
    private static String getInput(Scanner inFromUser) {
        // Do not modify
        System.out.print("CLIENT Guess a number: ");
        return inFromUser.next().toLowerCase();
    }

    // You do not have to give a comment block for this method
    public static void main(String[] args) {
        // Do not modify
        DatagramSocket dgSocket = null;
        InetAddress serverAddr = null;
        try {
            System.out.print("CLIENT Looking up server...");
            if (args.length < 1) {
                serverAddr = InetAddress.getLoopbackAddress();
            }
            else {
                serverAddr = InetAddress.getByName(args[0]);
            }
            System.out.println("good");
            dgSocket = new MyBadDatagramSocketP3();
            Scanner inFromUser = new Scanner(System.in);
            boolean done = false;
            while (!done) {
                String strFromUser = getInput(inFromUser);
                if (!strFromUser.equals("quit")) {
                    long guess = Long.parseLong(strFromUser);
                    System.out.print("CLIENT Sending guess...");
                    sendGuess(guess, dgSocket, serverAddr);
                    System.out.println("good");
                    System.out.print("CLIENT Waiting for response...");
                    try {
                        byte response = receiveResponse(dgSocket);
                        if (response == Server.LOW) {
                            System.out.println("good, too low");
                        }
                        else if (response == Server.HIGH ) {
                            System.out.println("good, too high");
                        }
                        else {
                            System.out.println("good, winner!");
                        }
                        done = response == Server.WINNER;
                    }
                    catch (SocketTimeoutException ex) {
                        // The Server could choose not to respond in the event of a corrupt packet
                        System.out.println("timeout, resending previous guess");
                    }
                }
                else {
                    done = true;
                }
            }
            System.out.println("CLIENT Done, normal termination");
            inFromUser.close();
            dgSocket.close();
        }
        catch (IOException ex) {
            System.out.println("error: " + ex);
        }
    }

    private static final int TIMEOUT = 1000;
    /**
     * Fill in the missing details. Give a justification for the error
     * recovery scheme being used here.
     *
     * Since I am not able to modify the main method, I just throw a timeout
     * exception if I get an invalid response from the server.
     * That will cause the packet to be resent.
     *
     * @param inSocket The DatagramSocket through which a response from the Server
     *                 is expected.
     * @return Server.LOW, Server.HIGH or Server.WINNER if the response indicates a
     *         guess that was too low, high or correct.
     * @throws IOException If receive fails or a response is not received from the
     *                     Server after one second.
     */
    private static byte receiveResponse(DatagramSocket inSocket) throws IOException {
        // You can modify only the code inside this method
        byte[] rcvData = new byte[1];
        DatagramPacket rcvPkt = new DatagramPacket(rcvData, 1);
        inSocket.setSoTimeout(TIMEOUT); // Wait 1 second for the Server to respond
        inSocket.receive(rcvPkt); // If a response isn't received  within one second, then a
                                  // SocketTimeoutException is thrown
        if ((rcvData[0] & Server.LOW) == Server.LOW) {
            return Server.LOW;
        } else if ((rcvData[0] & Server.HIGH) == Server.HIGH) {
            return Server.HIGH;
        } else if ((rcvData[0] & Server.WINNER) == Server.WINNER) {
            return Server.WINNER;
        } else {
            // Timeout if erroneous status code was sent
            throw new SocketTimeoutException();
        }
    }

    /**
     * Sends a given guess to a given InetAddress address through the given DatagramSocket.
     *
     * Two checksums are sent with each guess. If the bits representing the long
     * were to be written as an 8x8 table, one checksum would be from the top
     * down, and another would be from left to right. This allows the server to
     * use both checksums to pinpoint and correct a single bit flip error.
     *
     * @param guess The guess that the Client is sending to the Server.
     * @param inSocket The DatagramSocket through which the guess must be sent.
     * @param serverAddr The InetAddress to which the given guess should be sent.
     * @throws IOException is thrown if send fails.
     */
    private static void sendGuess(long guess,
                                  DatagramSocket inSocket,
                                  InetAddress serverAddr) throws IOException {
        // You can modify only the code inside this method
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        // Write guess
        dos.writeLong(guess);
        // Write error detection and recovery bits
        int parity = 0;
        for (int i = 0; i < Long.SIZE / Byte.SIZE; i++) {
            int b = ((int) (guess >> (i * Byte.SIZE))) & 255;
            parity ^= b << Byte.SIZE; // detection
            for (int j = 0; j < Byte.SIZE; j++) {
                parity ^= ((b >> j) & 1) << i; // recovery
            }
        }
        dos.writeShort((short) parity);
        byte[] sndData = baos.toByteArray();
        DatagramPacket sndPkt = new DatagramPacket(sndData,
                                                   sndData.length,
                                                   serverAddr,
                                                   Server.LISTEN_PORT);
        inSocket.send(sndPkt);
    }

    // You may add extra methods...
}
