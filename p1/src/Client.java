package p1;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.lang.SecurityException;
import java.net.Socket;
import java.net.UnknownHostException;
/*
 * Client which sends a string to a server
 * and receives a string back before closing.
 * @author Martin Mueller
 */
public class Client {
    // The host of the server to connect to (null => loopback)
    public static final String HOST = null;
    // The port over which the client connects to the server
    public static final int PORT = 1234;
    // The size of the read buffer in bytes
    public static final int BUFFER_SIZE = 4096;
    /*
     * Initializes connection to server, sends data,
     * receives data, displays data, then exits.
     * @param args String to pass to the server
     */
    public static void main(String args[]) {
        Socket connection = connect(HOST, PORT);
        if (connection != null) {
            sendString(connection, args[0]);
            receiveString(connection);
        }
        disconnect(connection);
    }
    /*
     * Connects to a server on a given host and port.
     * @param host The host to connect to
     * @param port The port to connect to
     * @return A session socket
     */
    private static Socket connect(String host, int port) {
        Socket connection = null;
        try {
            System.out.print("Connecting to server...");
            connection = new Socket(host, port);
            System.out.print("Connected");
        } catch (UnknownHostException unhostex) {
            System.out.print("Cannot resolve host");
        } catch (IOException ioex) {
            System.out.print("Cannot connect to server");
        } catch (SecurityException secex) {
            System.out.print("Permission denied");
        } catch (IllegalArgumentException illargex) {
            System.out.print("Invalid port");
        }
        return connection;
    }
    /*
     * Sends a given string to a server over a given connection.
     * @param connection The session socket
     * @param outputString The string to send
     */
    private static void sendString(Socket connection, String outputString) {
        try {
            System.out.println("Sending string: " + outputString);
            DataOutputStream outputStream = new DataOutputStream(
                connection.getOutputStream()
            );
            outputStream.writeBytes(outputString);
            System.out.println("Sent");
        } catch (IOException ioex) {
            System.out.println("Cannot send string");
        }
    }
    /*
     * Reads an arbitrarily long string over a given socket.
     * @param connection The session socket
     */
    private static void receiveString(Socket connection) {
        try {
            System.out.println("Waiting for input...");
            DataInputStream input = new DataInputStream(
                connection.getInputStream()
            );
            byte bytes[] = new byte[BUFFER_SIZE];
            StringBuilder sb = new StringBuilder();
            int bytesRead = 0;
            try {
                while (bytesRead > -1) {
                    bytesRead = input.read(bytes);
                    sb.append(new String(bytes, 0, bytesRead));
                }
            } catch (IOException ioex2) {
                System.out.println("Cannot read input");
            }
            System.out.println("Received string: " + sb.toString());
        } catch (IOException ioex) {
            System.out.println("Connection unavailable");
        }
    }
    /*
     * Closes a given session socket.
     * @param connection The session socket
     */
    private static void disconnect(Socket connection) {
        try {
            System.out.print("Closing connection...");
            connection.close();
            System.out.println("Closed");
        } catch (IOException ioex) {
            System.out.println("Unable to gracefully terminate connection");
        }
    }
}
