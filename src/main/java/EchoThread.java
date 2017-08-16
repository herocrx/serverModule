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
    ArrayList<String> tcpClientPacketData = new ArrayList<String>();
    DataTCP dataTCP;

    String receivedPortUDP = null;
    String receivedipClient = null;
    String receivedFileName = null;
    String receivedCurrentDate = null;
    String receivedPattern = null;

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
        try {
            writer = new PrintWriter("HubertFile.txt", "UTF-8");
        } catch (IOException e) {
            System.err.println("The file couldn't be created");
        }
        dataTCP = new DataTCP();
    }

    enum informationPacketStateMachine
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



    public void run()
    {
        receiveTCPpacket();
        convertTCPPacketToObjectsAsArray();
        TCPObjectsToStrings();
        showTCPReceivedPacketData();
        sendRequestedFileToClient();
    }

    void receiveTCPpacket()
    {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("clientSocket.getOutputStream is not reading the values");
        }
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            tcpClientPacketData.add(in.readLine());
        } catch (IOException e) {
            System.err.println("clientSocket.getInputStream() is not reading the values");
        } catch (NullPointerException e) {
            System.err.println("The array is not declared");
        }
    }

    void convertTCPPacketToObjectsAsArray()
    {

        dataTCP.Pattern.add('(');
        dataTCP.Pattern.add('%');
        dataTCP.Pattern.add('^');
        dataTCP.Pattern.add('&');
        dataTCP.Pattern.add(')');
        int i = 0;

        informationPacketStateMachine stateMachine = informationPacketStateMachine.decodePattern_1;
        switch (stateMachine) {
            case decodePattern_1:
                for (int j = 0; i < dataTCP.Pattern.size() - 1; i++, j++) {
                    if (tcpClientPacketData.get(0).charAt(i) != dataTCP.Pattern.get(i))
                        return;
                }
                i++;
            case decodeCurrentDate:
                while (tcpClientPacketData.get(0).charAt(i) != dataTCP.Pattern.get(0)) {
                    dataTCP.CurrentDate.add(tcpClientPacketData.get(0).charAt(i));
                    i++;
                }
            case decodePattern_2:
                for (int j = 0; j < dataTCP.Pattern.size() - 1; i++, j++) {
                    if (tcpClientPacketData.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return;
                }
                i++;
            case decodeFileName:
                while (tcpClientPacketData.get(0).charAt(i) != dataTCP.Pattern.get(0)) {
                    dataTCP.FileName.add(tcpClientPacketData.get(0).charAt(i));
                    i++;
                }
            case decodePattern_3:
                for (int j = 0; j < dataTCP.Pattern.size() - 1; i++, j++) {
                    if (tcpClientPacketData.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return;
                }
                i++;
            case decodeTCPAddress:
                while (tcpClientPacketData.get(0).charAt(i) != dataTCP.Pattern.get(0)) {
                    dataTCP.ipClient.add(tcpClientPacketData.get(0).charAt(i));
                    i++;
                }
            case decodePattern_4:
                for (int j = 0; j < dataTCP.Pattern.size() - 1; i++, j++) {
                    if (tcpClientPacketData.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return;
                }
                i++;
            case decodePortUdp:
                while (tcpClientPacketData.get(0).charAt(i) != dataTCP.Pattern.get(0)) {
                    dataTCP.portUDP.add(tcpClientPacketData.get(0).charAt(i));
                    i++;
                }
            case decodePattern_5:
                for (int j = 0; j < dataTCP.Pattern.size(); i++, j++) {
                    if (tcpClientPacketData.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return;
                }
        }
        return;
    }

     void TCPObjectsToStrings()
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

    void showTCPReceivedPacketData()
    {
        System.out.format("Pattern used for transmission: %s \n",receivedPattern);
        System.out.format("Date of transmitted information: %s \n",receivedCurrentDate);
        System.out.format("Filename which is requested: %s \n",receivedFileName);
        System.out.format("IP address of the client %s \n",receivedipClient);
        System.out.format("Port of sent by client: %s \n",receivedPortUDP);

    }

    void sendRequestedFileToClient()
    {
        final int maxPacketSize = 65507;
        byte[] buffer = new byte[maxPacketSize];
        File file = null;
        FileInputStream fileStream = null;
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        }
        catch(SocketException e)
        {
            System.out.println("Socket for UDP cannot be created");
        }
        String projectDirectory = "/home/heroadm/IdeaProjects/TrainingWebApplication/";
        try {
            fileStream = new FileInputStream(file = new File(projectDirectory + receivedFileName));
        }
        catch (FileNotFoundException e) {
            System.err.println("The file is not found");
            System.err.println(projectDirectory+receivedFileName);
        }
        long numberRequiredPackets = (file.length() / maxPacketSize) +1;

        //sending informationPacket
        System.out.println("Sending information packet");
        String informationData = "Filename:" + receivedFileName
                + " \nSize:" + file.length() + "\n";
        byte[] ar = informationData.getBytes();
        try {
            InetAddress address = InetAddress.getByName(receivedipClient);
            DatagramPacket packet = new DatagramPacket(ar, ar.length, address,
                    Integer.parseInt(receivedPortUDP));
            socket.send(packet);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.err.format("Unknown host: %s",receivedipClient);
        } catch (IOException e)
        {
            System.err.println("Packet is not sent by socket");
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
                sleep(50);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("UDP connection is finished!");
        return;
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
