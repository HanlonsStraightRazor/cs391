package p1;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
/*
 * Server that receives arbitrary text from a
 * Client and sends it back capitalized.
 * Capable of accepting multiple connections
 * in a sequential fashion.
 * @author Martin Mueller
 */
public class Server {
    // The port on which the server listens for connections
    public static final int LISTEN_PORT;
    /*
     * Constructor.
     * Initializes LISTEN_PORT.
     */
    public Server() {
        LISTEN_PORT = 1234;
    }
    /*
     * Returns a capitalized version of a string from a client over TCP.
     * Runs in an infinite loop.
     * @param args Command line arguments to be supplied (ignored).
     */
    public static void main(String args[]) {
        // Set up socket
        System.out.print("Initializing socket...");
        try (ServerSocket listenSocket = new ServerSocket(LISTEN_PORT)) {
            System.out.println("Done");
            // Wait for connection and connect to some client
            System.out.print("Listening for connection...");
            Socket connection = listenSocket.accept();
            System.out.println("Connected to " + connection);
            // Read input from client
            System.out.print("Waiting for input...");
            DataInputStream inputStream =
                new DataInputStream(connection.getInputStream());
            int bytesRead = 0;
            byte input[] = new byte[READ_BUFFER_SIZE];
            StringBuilder sb = new StringBuilder();
            try {
                int byteQuantum = inputStream.read(input);
                while (byteQuantum != -1) {
                    bytesRead += byteQuantum;
                    sb.append(new String(input, 0, byteQuantum));
                }
            } catch (IOException ioe) {
                System.out.print("Unable to read input...");
            } catch (NullPointerException npe) {
                System.out.print("Unable to initialize read buffer...");
            } finally {
                System.out.println(bytesRead + " bytes read");
            }
            // Process input as string and convert it to upper case
            String inputString = sb.toString();
            System.out.println("Received string: " + inputString);
            String outputString = inputString.toUpperCase();
            // Send transformed string to client
            System.out.print("Sending string: " + outputString + "...");
            byte output[] = outputString.getBytes();
            DataInputStream outputStream =
                new DataInputStream(connection.getOutputStream());
            try {
                outputStream.write(output);
            } catch (IOException ioe) {
                System.out.println("Unable to send string");
            }
            System.out.println("Sent");
            // Close the connection
            System.out.print("Closing connection...");
            connection.close();
            System.out.println("Closed");
        } catch (IOException ioex) {
            System.out.println(ioex);
        }
    }
}
