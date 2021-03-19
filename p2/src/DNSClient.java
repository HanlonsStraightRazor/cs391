package p2;

import java.io.*;
import java.net.*;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
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
            if (writeHeader(dos)
                    && writeQuestion(sock, dos)
                    && sendRequest(sock, address, baos)) {
                byte[] response = getResponse(sock);
                if (response != null) {
                    parseResponse(response);
                }
            }
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
        System.out.print("Enter host: ");
        try {
            Scanner s = new Scanner(System.in);
            String input = s.nextLine();
            if (input.equals("quit!")) {
                closeSocket(sock);
                System.out.println("Done. Normal Termination.");
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
     * @param response The DNS response in the form of a byte array
     */
    public static void parseResponse(byte[] response) {
        try {
            DataInputStream dis = new DataInputStream(
                new ByteArrayInputStream(response)
            );
            // Keep log of bytes read
            ArrayList<Byte> log = new ArrayList<>();
            // Header information
            System.out.printf("Transaction ID: 0x%04X\n", getShort(log, dis));
            short flags = getShort(log, dis);
            System.out.printf("Flags: 0x%04X\n", flags);
            System.out.printf("Questions: 0x%04X\n", getShort(log, dis));
            short answers = getShort(log, dis);
            System.out.printf("Answers RRs: 0x%04X\n", answers);
            System.out.printf("Name Server RRs: 0x%04X\n", getShort(log, dis));
            System.out.printf("Additional RRs: 0x%04X\n", getShort(log, dis));
            System.out.println();
            // Question information
            System.out.printf("Name: %s\n", readName(log, dis));
            System.out.printf("Type: 0x%04X\n", getShort(log, dis));
            System.out.printf("Class: 0x%04X\n", getShort(log, dis));
            System.out.println();
            // Response information
            // Iterate over answers
            for (int s = 0; s < answers; s++) {
                System.out.printf("Name: %s\n", readName(log, dis));
                short type = getShort(log, dis);
                System.out.printf("Type: 0x%04X\n", type);
                System.out.printf("Class: 0x%04X\n", getShort(log, dis));
                System.out.printf("TTL: %d\n", getInt(log, dis));
                System.out.printf("Length: %d\n", getShort(log, dis) & 0xffff);
                if (type == 1) { // A record
                    System.out.printf("Value: %d.%d.%d.%d\n",
                            getByte(log, dis) & 0xff,
                            getByte(log, dis) & 0xff,
                            getByte(log, dis) & 0xff,
                            getByte(log, dis) & 0xff);
                } else if (type == 5) { // CNAME
                    System.out.printf("Value: %s\n", readName(log, dis));
                } else { // Something this can't parse
                    System.err.println("Error: sorry, I can't parse that type");
                }
                System.out.println();
            }
            // Error checking
            int recursionAvailable = (flags >> 7) & 1;
            if (recursionAvailable == 0) {
                System.err.println("Error: Recursion not available");
            }
            int responseCode = flags & 15;
            switch (responseCode) {
                case (0):
                    break;
                case (1):
                    System.err.println("Error: Erroneous query format");
                    break;
                case (2):
                    System.err.println("Error: Server unable to process query");
                    break;
                case (3):
                    System.err.println("Error: Name unavailable");
                    break;
                case (4):
                    System.err.println(
                            "Error: Requested query type not supported");
                    break;
                case (5):
                    System.err.println("Error: Query refused");
                    break;
                default:
                    System.err.println("Error: Invalid response code");
            }
        } catch (IOException ioex) {
            System.err.println("Error: Erroneous response format");
        } finally {
            System.out.println();
        }
    }
    /*
     * Adds the next byte from the stream to a log.
     * @param log The log which keeps track of previously read bytes
     * @param dis The stream from which to read
     * @return The byte that was read
     */
    public static byte getByte(ArrayList<Byte> log, DataInputStream dis)
            throws IOException {
        byte a = 0;
        try {
            a = dis.readByte();
            log.add(a);
        } catch (IOException ioex) {
            throw ioex;
        }
        return a;
    }
    /*
     * Adds the next 2 bytes from the stream to a log.
     * @param log The log which keeps track of previously read bytes
     * @param dis The stream from which to read
     * @return The 2 bytes that were read
     */
    public static short getShort(ArrayList<Byte> log, DataInputStream dis)
            throws IOException {
        byte a = 0, b = 0;
        try {
            a = dis.readByte();
            log.add(a);
            b = dis.readByte();
            log.add(b);
        } catch (IOException ioex) {
            throw ioex;
        }
        return (short) ((a << 8) | b);
    }
    /*
     * Adds the next 4 bytes from the stream to a log.
     * @param log The log which keeps track of previously read bytes
     * @param dis The stream from which to read
     * @return The 4 bytes that were read
     */
    public static int getInt(ArrayList<Byte> log, DataInputStream dis)
            throws IOException {
        byte a = 0, b = 0, c = 0, d = 0;
        try {
            a = dis.readByte();
            log.add(a);
            b = dis.readByte();
            log.add(b);
            c = dis.readByte();
            log.add(c);
            d = dis.readByte();
            log.add(d);
        } catch (IOException ioex) {
            throw ioex;
        }
        return (a << 24)
            | (b << 16)
            | (c << 8)
            | d;
    }
    /*
     * Reads a string with respect to pointers from a DNS response.
     * @param log The log to whicht to write previously read data
     * @param dis The stream from which to read
     * @return The string read from the DNS response
     */
    public static String readName(ArrayList<Byte> log, DataInputStream dis)
            throws IOException {
        String string = null;
        try {
            // Build name as we go
            StringBuilder name = new StringBuilder();
            // Read while there's no null byte
            byte b = getByte(log, dis);
            for (;;) {
                // Determine if we're reading a pointer
                if (((b >> 6) & 3) == 3) {
                    // Calculate offset
                    int offset = b << 8;
                    b = getByte(log, dis);
                    offset = (offset | b) & 0x3fff;
                    // Get string from pointer
                    name.append(readPointer(log, offset));
                    // Since pointers can only be at the end, stop
                    break;
                } else {
                    // If there's no pointer, read like normal
                    byte count = b;
                    for (byte chars = 0; chars < count; chars++) {
                        b = getByte(log, dis);
                        byte[] array = { b };
                        name.append(new String(array));
                    }
                    b = getByte(log, dis);
                    // Stop if we hit a null byte
                    if (b == 0) {
                        break;
                    }
                }
                // Append dot separator
                name.append(".");
            }
            string = name.toString();
        } catch (IOException ioex) {
            throw ioex;
        }
        return string;
    }
    /*
     * Takes a log of parsed bytes and a pointer and returns
     * the result of dereferencing the pointer as a string.
     * @param log The bytes read so far
     * @param offset The offset of the pointer relative to the log
     * @return The string to which the pointer points
     */
    public static String readPointer(ArrayList<Byte> log, int offset) {
        StringBuilder name = new StringBuilder();
        byte b = log.get(offset);
        for (;;) {
            if (((b >> 6) & 3) == 3) {
                int os = b << 8;
                offset++;
                b = log.get(offset);
                os = (os | b) & 0x3fff;
                name.append(readPointer(log, os));
                break;
            } else {
                byte count = b;
                for (byte chars = 0; chars < count; chars++) {
                    offset++;
                    b = log.get(offset);
                    byte[] array = { b };
                    name.append(new String(array));
                }
                offset++;
                b = log.get(offset);
                if (b == 0) {
                    break;
                }
            }
            name.append(".");
        }
        return name.toString();
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
