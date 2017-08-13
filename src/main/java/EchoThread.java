import java.net.*;
import java.io.*;

public class EchoThread extends Thread {
    protected Socket socket;
    BufferedReader brinp = null;

    BufferedReader in;
    PrintWriter out;

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("clientSocket.getOutputStream is not reading the values");
        }
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine, outputLine;
            while ((inputLine = in.readLine()) != null) {
                outputLine = inputLine;
                out.println(outputLine);
                System.out.println(outputLine);
                if (inputLine.equals("Bye.")) {
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("clientSocket.getInputStream() is not reading the values");
        }
    }
}

