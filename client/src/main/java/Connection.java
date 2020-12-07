import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class Connection {

    private static final Connection instance = new Connection();
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean connected;

    private Connection() {
        try {
            socket = new Socket("Localhost", 8000);
            connected = true;
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String sendGet(String[] wordDefinition) {
        Message message = new Message(Message.Type.GET, wordDefinition);
        try {

            oos.writeObject(message);
            oos.flush();
            
            if (ois == null)
                ois = new ObjectInputStream(socket.getInputStream());
            Message result = (Message) ois.readObject();
            
            return result.getDefinition();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public LinkedList<String> sendGetList() {
        Message message = new Message(Message.Type.GETLIST); 
        try {
            
            oos.writeObject(message);
            oos.flush();
            
            if (ois == null)
                ois = new ObjectInputStream(socket.getInputStream());
            Message result = (Message) ois.readObject();
            return result.getList();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void sendAddEdit(String[] wordDefinition) {
        Message message = new Message(Message.Type.ADDEDIT, wordDefinition);
        try {

            oos.writeObject(message);
            oos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendClose() {
        Message message = new Message(Message.Type.CLOSE);
        try {

            oos.writeObject(message);
            oos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public static Connection getInstance() {
        return instance;
    }
}
