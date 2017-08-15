import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.*;


public class EchoThread extends Thread {
    protected Socket socket;
    BufferedReader brinp = null;

    BufferedReader in;
    PrintWriter out;
    PrintWriter writer;

    DataTCP dataTCP;

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
        try {
            writer = new PrintWriter("HubertFile.txt", "UTF-8");
        } catch (IOException e) {
            System.err.println("The file couldn't be created");
        }
        dataTCP = new DataTCP();
        // writer.println(outputLine);


    }

    enum informationDecodeStateMachine
    {
        decodePattern_1 ,
        decodeCurrentDate,
        decodePattern_2,
        decodeFileName,
        decodePattern_3,
        decodeTCPAddress,
        decodePattern_4,
        decodePortUdp,
        decodePattern_5
    }


    boolean decodeMessage(final ArrayList<String> message) {

        dataTCP.Pattern.add('(');
        dataTCP.Pattern.add('%');
        dataTCP.Pattern.add('^');
        dataTCP.Pattern.add('&');
        dataTCP.Pattern.add(')');
        int i = 0;

        informationDecodeStateMachine stateMachine = informationDecodeStateMachine.decodePattern_1;
        switch (stateMachine) {
            case decodePattern_1:
                for (int j = 0; i < dataTCP.Pattern.size() - 1; i++, j++) {
                    if (message.get(0).charAt(i) != dataTCP.Pattern.get(i))
                        return false;
                }
                i++;
            case decodeCurrentDate:
                while (message.get(0).charAt(i) != dataTCP.Pattern.get(0)) {
                    dataTCP.CurrentDate.add(message.get(0).charAt(i));
                    i++;
                }
            case decodePattern_2:
                for (int j = 0; j < dataTCP.Pattern.size() - 1; i++, j++) {
                    if (message.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return false;
                }
                i++;
            case decodeFileName:
                while (message.get(0).charAt(i) != dataTCP.Pattern.get(0)) {
                    dataTCP.FileName.add(message.get(0).charAt(i));
                    i++;
                }
            case decodePattern_3:
                for (int j = 0; j < dataTCP.Pattern.size() - 1; i++, j++) {
                    if (message.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return false;
                }
                i++;
            case decodeTCPAddress:
                while (message.get(0).charAt(i) != dataTCP.Pattern.get(0)) {
                    dataTCP.ipClient.add(message.get(0).charAt(i));
                    i++;
                }
            case decodePattern_4:
                for (int j = 0; j < dataTCP.Pattern.size() - 1; i++, j++) {
                    if (message.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return false;
                }
                i++;
            case decodePortUdp:
                while (message.get(0).charAt(i) != dataTCP.Pattern.get(0)) {
                    dataTCP.portUDP.add(message.get(0).charAt(i));
                    i++;
                }
            case decodePattern_5:
                for (int j = 0; j < dataTCP.Pattern.size(); i++, j++) {
                    if (message.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return false;
                }
        }
        return true;
    }

    public void run() {
        //TCP connection reading the values
        ArrayList<String> outputLine = new ArrayList<String>();
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("clientSocket.getOutputStream is not reading the values");
        }
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputLine.add(in.readLine());
        } catch (IOException e) {
            System.err.println("clientSocket.getInputStream() is not reading the values");
        } catch (NullPointerException e) {
            System.err.println("The array is not declared");
        }
        if (!decodeMessage(outputLine)) return;
        transformReceivedValues();
        showReceivedValues();
        sendFileThroughUDP();

    }

    boolean sendFileThroughUDP() {
        final int maxPacketSize = 65508;
        byte[] buffer = new byte[maxPacketSize];
        //open a file to send
/// getBytes from anyWhere
// I'm getting byte array from File
        File file = null;
        FileInputStream fileStream = null;
        String projectDirectory = "/home/heroadm/IdeaProjects/TrainingWebApplication/";
        try {
            fileStream = new FileInputStream(file = new File(projectDirectory + receivedFileName));
        }
        catch (FileNotFoundException e) {
            System.err.println("The file is not found");
            System.err.println(projectDirectory+receivedFileName);
        }
        long numberRequiredPackets = file.length() / maxPacketSize;

        //sending informationPacket
        System.out.println("Sending information packet");
        String informationData = "Filename:" + receivedFileName
                + "\nSize:" + file.length() + "\n";
        byte[] ar = informationData.getBytes();
        try {
            InetAddress address = InetAddress.getByName(receivedipClient);
            DatagramPacket packet = new DatagramPacket(ar, ar.length, address,
                    Integer.parseInt(receivedPortUDP));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.err.format("Unknown host: %s",receivedipClient);
        }


        System.out.println("Sending data packets");
        //sending dataPacket
        for (int i = 0; i < numberRequiredPackets; i++) {
            System.out.format("%s data packet is sent \n", i + 1);
            byte[] arr = new byte[maxPacketSize];
            try {
                fileStream.read(arr, 0, maxPacketSize);
                InetAddress address = InetAddress.getByName(receivedipClient);
                DatagramPacket packet = new DatagramPacket(arr, arr.length, address, Integer.parseInt(receivedPortUDP));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("UDP connection is finished!");
        return true;
    }


    String receivedPortUDP = null;
    String receivedipClient = null;
    String receivedFileName = null;
    String receivedCurrentDate = null;
    String receivedPattern = null;

    public void showReceivedValues()
    {
        System.out.format("Pattern used for transmission: %s \n",receivedPattern);
        System.out.format("Date of transmitted information: %s \n",receivedCurrentDate);
        System.out.format("Filename which is requested: %s \n",receivedFileName);
        System.out.format("IP address of the client %s \n",receivedipClient);
        System.out.format("Port of sent by client: %s \n",receivedPortUDP);

    }

    public void transformReceivedValues()
    {
         receivedPattern = dataTCP.Pattern.toString()
                .replace(" ","")
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();          //remove trailing spaces from partially initialized arrays);

        receivedCurrentDate =  dataTCP.CurrentDate.toString()
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", ""); //remove the left bracket;           //remove trailing spaces from partially initialized arrays);
       receivedFileName = dataTCP.FileName.toString()
                .replace(" ","")
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();           //remove trailing spaces from partially initialized arrays);
        receivedipClient = dataTCP.ipClient.toString()
                .replace(" ","")
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();           //remove trailing spaces from partially initialized arrays);
        receivedPortUDP =  dataTCP.portUDP.toString()
                .replace(" ","")
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();           //remove trailing spaces from partially initialized arrays);
    }
}



class DataTCP
{
    DataTCP()
    {
        CurrentDate =  new ArrayList<Character>();
        FileName    =  new ArrayList<Character>();
        Pattern     =  new ArrayList<Character>();
        portUDP     =  new ArrayList<Character>();
        ipClient    =  new ArrayList<Character>();
    }
    ArrayList<Character> CurrentDate;
    ArrayList<Character> FileName;
    ArrayList<Character> Pattern;
    ArrayList<Character> ipClient;
    ArrayList<Character> portUDP;
}
