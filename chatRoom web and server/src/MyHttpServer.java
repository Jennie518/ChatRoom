import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

public class MyHttpServer {
    // use try to see if 8020 is not working then the client can try 8090.but both is working
    private static ArrayList<Room> rooms = new ArrayList<>();
    public static void main(String[] args) throws IOException {
//        Room room =  new Room();
        ServerSocket server = null;
        try {
            server = new ServerSocket(8080);
        } catch (IOException e) {
            try {
                server = new ServerSocket(8090);
            } catch (IOException e1) {// if 8090 and 8020 both not working catch an error
                throw new RuntimeException("sorry server is busy" + e1.getMessage());
            }
        }

        while (true) {//use while to wait the client to accept
            Socket client = server.accept(); //connection handler should be the thread
            ConnectionHandler connectionHandler  = new ConnectionHandler(client);

          Thread thread = new Thread(connectionHandler);
          thread.start();
        }
    }

}
