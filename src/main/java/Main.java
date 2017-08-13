import java.util.Random;

public class Main {

    public static void main(String[] args) {
        /*
        int x = 0 ;
        final int size = 100;
        Random rand = new Random();

        int array[] = new int[size];
        for ( int  i = 0  ; i < size ; i++)
        {
            array[i] =rand.nextInt(100) +1;
        }
        for ( int i = 0 ;  i < size ; i++) {
            System.out.format("%d. The value of random nubmer is: %d%n",i+1,array[i]);
        }
        */
        int port = 10010;
        threadedServer serverHubert = new threadedServer(port);
        serverHubert.createSocket();
        serverHubert.startConnection();
        serverHubert.endConnection();
    }
}
