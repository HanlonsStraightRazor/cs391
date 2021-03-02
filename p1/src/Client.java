package ex0;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
/*
 * Client which sends a string to a server
 * and receives a string back before closing.
 * @author Martin Mueller
 */
public class Client {
    // The port over which the client connects to the server
    public static final int PORT;
    /*
     * Constructor.
     * Initializes PORT.
     */
    public Client() {
        PORT = 1234;
    }
    /*
     * Initializes connection to server, sends data,
     * receives data, displays data, then exits.
     * @param args String to pass to the server
     */
    public static void main(String args[]) {
        try (Socket socket = new Socket()) {
        } catch (IOException ioex) {
            System.out.println(ioex);
        }
    }
}
