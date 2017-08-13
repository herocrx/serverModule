import java.net.*;
import java.io.*;
import java.util.*;

public class EchoThread extends Thread {
    protected Socket socket;
    BufferedReader brinp = null;

    BufferedReader in;
    PrintWriter out;
    PrintWriter writer;

    DataTCP dataTCP;

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
        try{
             writer = new PrintWriter("HubertFile.txt", "UTF-8");
        } catch (IOException e) {
            System.err.println("The file couldn't be created");
        }
        dataTCP  = new DataTCP();
        // writer.println(outputLine);


    }


    boolean decodeMessage(final ArrayList<String> message)
    {

        dataTCP.Pattern.add('(');
        dataTCP.Pattern.add('%');
        dataTCP.Pattern.add('^');
        dataTCP.Pattern.add('&');
        dataTCP.Pattern.add(')');
        int i = 0 ;

        switch(0){
            case 0:
                for(int j = 0 ; i < dataTCP.Pattern.size()-1; i++,j++) {
                    if(message.get(0).charAt(i) != dataTCP.Pattern.get(i))
                        return false;
                }
                i++;
            case 1:
                while(message.get(0).charAt(i) != dataTCP.Pattern.get(0)){
                        dataTCP.CurrentDate.add(message.get(0).charAt(i));
                    i++;
                }
            case 2:
                for(int j = 0 ; j < dataTCP.Pattern.size()-1; i++,j++) {
                    if(message.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return false;
                }
                i++;
            case 3:
                while(message.get(0).charAt(i) != dataTCP.Pattern.get(0)){
                    dataTCP.FileName.add(message.get(0).charAt(i));
                    i++;
                }
            case 4:
                for(int j = 0 ; j < dataTCP.Pattern.size()-1; i++,j++) {
                    if(message.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return false;
                }
                i++;
            case 5:
                while(message.get(0).charAt(i) != dataTCP.Pattern.get(0)) {
                    dataTCP.ipClient.add(message.get(0).charAt(i));
                    i++;
                }
            case 6:
                for(int j = 0 ; j < dataTCP.Pattern.size()-1; i++,j++) {
                    if(message.get(0).charAt(i) != dataTCP.Pattern.get(j))
                        return false;
                }
                i++;
            case 7:
                while(message.get(0).charAt(i) != dataTCP.Pattern.get(0)) {
                    dataTCP.portUDP.add(message.get(0).charAt(i));
                    i++;
                }
            case 8:
                for(int j = 0 ; j < dataTCP.Pattern.size(); i++,j++) {
                    if(message.get(0).charAt(i) != dataTCP.Pattern.get(j))
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
        } catch (NullPointerException e)
        {
            System.err.println("The array is not declared");
        }
        if(!decodeMessage(outputLine)) return;
        showValues();
        sendFileThroughUDP();

    }

    boolean sendFileThroughUDP()
    {


    // DatagramSocket datagramSocket = new DatagramSocket(dataTCP.portUDP);
        return true;
    }

    void showValues()
    {
        System.out.format("Pattern used for transmission: %s \n",dataTCP.Pattern.toString()
                .replace(" ","")
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim());           //remove trailing spaces from partially initialized arrays);
        System.out.format("Data of transmitted information: %s \n",dataTCP.CurrentDate.toString()
                .replace(" ","")
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim());           //remove trailing spaces from partially initialized arrays);
        System.out.format("Filename which is requested: %s \n",dataTCP.FileName.toString()
                .replace(" ","")
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim());           //remove trailing spaces from partially initialized arrays);
        System.out.format("IP address of the client %s \n",dataTCP.ipClient.toString()
                .replace(" ","")
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim());           //remove trailing spaces from partially initialized arrays);
        System.out.println("Port for UDP transmission: " + String.valueOf(dataTCP.portUDP)
                .replace(" ","")
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim());           //remove trailing spaces from partially initialized arrays);
    }


}


class DataTCP
{
    DataTCP()
    {
        CurrentDate=  new ArrayList<Character>();
        FileName=  new ArrayList<Character>();
        Pattern=  new ArrayList<Character>();
        portUDP=  new ArrayList<Character>();
        ipClient=  new ArrayList<Character>();
    }
    ArrayList<Character> CurrentDate;
    ArrayList<Character> FileName;
    ArrayList<Character> Pattern;
    ArrayList<Character> ipClient;
    ArrayList<Character> portUDP;
}
