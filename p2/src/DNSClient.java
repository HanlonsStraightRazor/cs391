package p2;

import java.io.*;
import java.net.*;
import java.nio.channels.IllegalBlockingModeException;
import java.util.Scanner;

/*
 * Implementation of a simple DNS client in Java.
 */
public class DNSClient {
    // Address of DNS server to use
    private static final String DNS_SERVER_ADDRESS = "8.8.8.8";
    // Port over which DNS requests are sent
    private static final int DNS_SERVER_PORT = 53;
    // ID in the DNS packet headers
    private static final short ID = 1337;
    // MSS = 576, so maximum data is 576 - 20 - 20 = 536
    private static final int MSS = 576 - 20 - 20;
    /*
     * Main method; kicks off DNS client.
     * Runs in a loop until "quit!" is read when prompted.
     * @param args Command line arguments; ingored here
     */
    public static void main(String[] args) {
        DatagramSocket sock = createSocket();
        InetAddress address = verifyNameServer(sock);
        for (;;) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            if (!(writeHeader(dos)
                    && writeQuestion(sock, dos)
                    && sendRequest(sock, address, baos))) {
                continue;
            }
            byte[] response = getResponse(sock);
            if (response == null) {
                continue;
            }
            parseResponse(response);
        }
    }
    /*
     * Creates a socket used for communication
     * with the DNS server.
     * @return A socket for communication with the DNS server
     */
    public static DatagramSocket createSocket() {
        DatagramSocket sock = null;
        try {
            System.out.print("Creating socket...");
            sock = new DatagramSocket();
            System.out.println("Created");
        } catch (SocketException sockex) {
            System.err.println("Failed to open socket");
            System.exit(1);
        } catch (SecurityException secex) {
            System.err.println("Permission denied");
            System.exit(1);
        }
        return sock;
    }
    /*
     * Verifies a name server is valid by wrapping
     * it in an InetAddress object.
     * @return The address of a predefined name server
     */
    public static InetAddress verifyNameServer(DatagramSocket sock) {
        InetAddress address = null;
        try {
            System.out.print("Verifying name server address...");
            address = InetAddress.getByName(DNS_SERVER_ADDRESS);
            System.out.println("Verified");
        } catch (UnknownHostException uhex) {
            System.err.println("Failed to find host");
            closeSocket(sock);
            System.exit(1);
        } catch (SecurityException secex) {
            System.err.println("Permission denied");
            closeSocket(sock);
            System.exit(1);
        }
        return address;
    }
    /*
     * Writes a DNS header as specified in the project guidelines.
     * @param dos The DataOutputStream into which to write the header
     * @return Whether the header was successfully written
     */
    public static boolean writeHeader(DataOutputStream dos) {
        try {
            System.out.print("Writing header...");
            // ID      = (see above)   <-- Unique ID; Must be this for project
            dos.writeShort(ID);
            // QR      = 0             <-- Query or Response
            // OPCODE  = 0000          <-- Query Type; 0 => standard query
            // AA      = 0             <-- Authoritative Answer
            // TC      = 0             <-- TrunCation
            // RD      = 1             <-- Recursion Desired
            // RA      = 0             <-- Recursion Available
            // Z       = 000           <-- (reserved for future use)
            // RCODE   = 0000          <-- Response Code
            dos.writeShort(0x0100);
            // QDCOUNT = 1             <-- Question Count
            dos.writeShort(0x0001);
            // ANCOUNT = 0             <-- Answer Count
            dos.writeShort(0x0000);
            // NSCOUNT = 0             <-- Name Server records Count
            dos.writeShort(0x0000);
            // ARCOUNT = 0             <-- Additional Records Count
            dos.writeShort(0x0000);
            return true;
        } catch (IOException ioex) {
            System.err.println("Failed to write DNS packet header");
        }
        return false;
    }
    /*
     * Writes a DNS query into a given DataOutputStream.
     * The domain to be queried is taken from stdin.
     * @param sock The socket to close if the user wants to quit
     * @param dos The DataOutputStream into which to write the query
     * @return Whether the query was successfully written
     */
    public static boolean writeQuestion(DatagramSocket sock,
            DataOutputStream dos) {
        try {
            Scanner s = new Scanner(System.in);
            System.out.print("Enter host: ");
            String input = s.nextLine();
            if (input.equals("quit!")) {
                closeSocket(sock);
                System.exit(0);
            }
            String[] hosts = input.split("\\.");
            for (String host : hosts) {
                byte bytes[] = host.getBytes();
                dos.writeByte((byte) bytes.length);
                dos.write(bytes);
            }
            // Null terminator
            dos.writeByte(0x00);
            // QTYPE  = 1             <-- Query Type; 1 => A-Record Request
            dos.writeShort(0x0001);
            // QCLASS = 1             <-- Query Class; 1 => Internet Address
            dos.writeShort(0x0001);
            return true;
        } catch (IOException ioex) {
            System.err.println("Failed to write DNS packet body");
        }
        return false;
    }
    /*
     * Sends a pre-written query to a DNS server over a DatagramSocket.
     * @param baos The ByteArrayOutputStream containing the query information
     * @param sock The DatagramSocket over which to send the query
     * @param address The InetAddress of the DNS server to query
     * @return Whether the query was successfully sent
     */
    public static boolean sendRequest(DatagramSocket sock,
            InetAddress address,
            ByteArrayOutputStream baos) {
        try {
            System.out.print("Sending request...");
            byte request[] = baos.toByteArray();
            sock.send(
                new DatagramPacket(
                    request,
                    request.length,
                    address,
                    DNS_SERVER_PORT
                )
            );
            System.out.println("Sent");
            return true;
        } catch (PortUnreachableException puex) {
            System.err.println("Port unreachable");
        } catch (IllegalBlockingModeException ibmex) {
            System.err.println("Illegal blocking mode");
        } catch (IllegalArgumentException iaex) {
            System.err.println("Illegal argument(s)");
        } catch (IOException ioex) {
            System.err.println("Failed to send request");
        } catch (SecurityException secex) {
            System.err.println("Permission denied");
        }
        return false;
    }
    /*
     * Retrieves an DNS response over a given socket.
     * @param sock The socket on which to listen
     * @return The DNS response
     */
    public static byte[] getResponse(DatagramSocket sock) {
        try {
            System.out.print("Waiting for response...");
            byte response[] = new byte[MSS];
            DatagramPacket recPack = new DatagramPacket(
                response,
                response.length
            );
            sock.receive(recPack);
            System.out.println("Received");
            return response;
        } catch (SocketTimeoutException stoex) {
            System.err.println("Socket timed out");
        } catch (PortUnreachableException puex) {
            System.err.println("Port unreachable");
        } catch (IOException ioex) {
            System.err.println("Failed to retrieve response");
        } catch (IllegalBlockingModeException ibmex) {
            System.err.println("Illegal blocking mode");
        }
        return null;
    }
    /*
     * Outputs a DNS response packet in a human readable format.
     */
    public static void parseResponse(byte[] response) {
        try {
            DataInputStream dis = new DataInputStream(
                new ByteArrayInputStream(response)
            );
            System.out.printf("ID: 0x%x\n", dis.readShort());
            short flags = dis.readShort();
            System.out.printf("Flags: 0x%x\n", flags);
            System.out.printf("Questions: %d\n", dis.readShort());
            System.out.printf("Answers: %d\n", dis.readShort());
            System.out.printf("Additional Name Servers: %d\n", dis.readShort());
            System.out.printf("Additional Records: %d\n", dis.readShort());
        } catch (EOFException eofex) {
            System.err.println("Reached end of response before parsing finished");
        } catch (IOException ioex) {
            System.err.println("Input stream closed unexpectedly");
        }
    }
    /*
     * Closes a given DatagramSocket.
     * @param sock The DatagramSocket to close
     */
    public static void closeSocket(DatagramSocket sock) {
        try {
            System.out.print("Closing socket...");
            sock.close();
            System.out.println("Closed");
        } catch (Exception ex) {
            System.err.println("Failed to close socket");
            System.exit(1);
        }
    }
}
