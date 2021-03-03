package p1;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
/*
 * Server that receives arbitrary text from a
 * Client and sends it back capitalized.
 * Capable of accepting multiple connections
 * in a sequential fashion.
 * @author Martin Mueller
 */
public class Server {
    // The port on which the server listens for connections
    public static final int LISTEN_PORT = 1234;
    /*
     * Returns a capitalized version of a string from a client over TCP.
     * Runs in an infinite loop.
     * @param args Command line arguments to be supplied (ignored).
     */
    public static void main(String args[]) {
        ServerSocket sock = initSocket();
        while (sock != null) {
            Socket connection = connect(sock);
            if (connection == null) {
                break;
            }
            String inputString = getInput(connection);
            if (inputString != null) {
                sendOutput(connection, inputString.toUpperCase());
            }
            disconnect(connection);
        }
        if (sock != null) {
            closeSocket(sock);
        }
    }
    /*
     * Establishes and returns a server socket on LISTEN_PORT.
     * @return The server socket
     */
    private static ServerSocket initSocket(){
        ServerSocket sock = null;
        try {
            System.out.print("Initializing socket...");
            sock = new ServerSocket(LISTEN_PORT);
            System.out.println("Initialized");
        } catch (IOException ioex) {
            System.out.println("Failed to establish socket");
        } catch (SecurityException secex) {
            System.out.println("Permission denied");
        } catch (IllegalArgumentException illargex) {
            System.out.println("Invalid port");
        }
        return sock;
    }
    /*
     * Connects to a client from a server socket and returns
     * the corresponding socket for the session.
     * @param sock The active server socket listening for connections
     * @return A session socket
     */
    private static Socket connect(ServerSocket sock) {
        Socket connection = null;
        try {
            System.out.print("Listening for connection...");
            connection = sock.accept();
            System.out.println("Connected to " + connection);
        } catch (SocketTimeoutException socktimeoutex) {
            System.out.println("Connection timed out");
        } catch (IOException ioex) {
            System.out.println("Failed to establish connection");
        } catch (SecurityException secex) {
            System.out.println("Permission denied");
        } catch (IllegalBlockingModeException illblockmodeex) {
            System.out.println("Illegal blocking mode");
        }
        return connection;
    }
    /*
     * Reads an arbitrarily long string over a given socket.
     * @param connection The session socket
     * @return The string sent over the session socket
     */
    private static String getInput(Socket connection) {
        String inputString = null;
        try {
            System.out.println("Waiting for input...");
            DataInputStream input = new DataInputStream(
                connection.getInputStream()
            );
            try {
                inputString = input.readUTF();
            } catch (EOFException eofex) {
                System.out.println("Reached EOF before end of input");
            } catch (UTFDataFormatException udfex) {
                System.out.println("Invalid UTF");
            } catch (IOException ioex2) {
                System.out.println("Cannot read input");
            }
            System.out.println("Received string: " + inputString);
        } catch (IOException ioex) {
            System.out.println("Connection unavailable");
        }
        return inputString;
    }
    /*
     * Sends a given string over a given session socket.
     * @param connection The session socket
     * @param outputString The string to send
     */
    private static void sendOutput(Socket connection, String outputString) {
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
    private static void closeSocket(ServerSocket sock) {
        try {
            System.out.print("Closing server socket...");
            sock.close();
            System.out.println("Closed");
        } catch (IOException ioex) {
            System.out.println("Unable to gracefully close socket");
        }
    }
}
