package p1;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.Socket;
import java.net.UnknownHostException;
/*
 * Client which sends a string to a server
 * and receives a string back before closing.
 * @author Martin Mueller
 */
public class GlitchyClient {
    // The host of the server to connect to (null => loopback)
    public static final String HOST = null;
    // The port over which the client connects to the server
    public static final int PORT = 1234;
    /*
     * Initializes connection to server, sends data,
     * receives data, displays data, then exits.
     * @param args String to pass to the server
     */
    public static void main(String args[]) {
        Socket connection = connect(HOST, PORT);
        if (connection != null) {
            disconnect(connection);
        }
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
            System.out.println("Connected to " + connection);
        } catch (UnknownHostException unhostex) {
            System.out.println("Cannot resolve host");
        } catch (IOException ioex) {
            System.out.println("Cannot connect to server");
        } catch (SecurityException secex) {
            System.out.println("Permission denied");
        } catch (IllegalArgumentException illargex) {
            System.out.println("Invalid port");
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
            DataOutputStream output = new DataOutputStream(
                connection.getOutputStream()
            );
            output.writeUTF(outputString);
            output.flush();
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
            try {
                System.out.println("Received string: " + input.readUTF());
            } catch (EOFException eofex) {
                System.out.println("Reached EOF before end of input");
            } catch (UTFDataFormatException udfex) {
                System.out.println("Invalid UTF");
            } catch (IOException ioex2) {
                System.out.println("Cannot read input");
            }
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
