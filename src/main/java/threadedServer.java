import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;



public class threadedServer {

    protected final Logger log;
    ServerSocket serverSocket;
    Socket clientSocket;
    int portNumber;

    public threadedServer(int portNumber1) {
        serverSocket = null;
        clientSocket = null;
        portNumber = portNumber1;
        log = Logger.getLogger(getClass().getName()); //java.util.logging.Logger
    }

    public void createSocket() {
        log.log(Level.INFO, "Socket of the server has port: " + portNumber);
        log.info("Socket is being created");
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.err.format("Could not listen on port: " + portNumber);
            System.exit(1);
        }

    }

    public void endConnection() {
        log.info("Socket is destoryed");
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.format("Cannot close clientSocket");
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.format("Cannot close clientSocket");
        }
    }

    public void startConnection() {
        log.info("Connection between 2 machines are trying to be established");
        while(true) {
            try {
                clientSocket = serverSocket.accept();
                log.log(Level.INFO, "Socket is connected to the server");
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            new EchoThread(clientSocket).start();
        }
    }

}
