import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;

public class Server {

    public static int num=1;

    public static void main(String[] args){
        ServerSocket listeningSocket=null;

        try {
            //Create a server socket listening on port 4444
            listeningSocket = new ServerSocket(4444);

            System.out.println(Thread.currentThread().getName() +
                    " - Server listening on port 4444 for a connection");

            //<editor-fold desc="Description">
            //Listen for incoming connections for ever
            while (true) {

                //Accept an incoming client connection request
                Socket clientSocket = listeningSocket.accept();

                //Create a thread for connection
                ClientConnection clientConnection = new ClientConnection(clientSocket);
                clientConnection.start();

                //Show someone wants to connect
                System.out.println(Thread.currentThread().getName()+" - A new client asks connection.");
            }
            //</editor-fold>

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(listeningSocket != null) {
                try {
                    listeningSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

