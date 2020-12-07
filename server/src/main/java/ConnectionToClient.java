import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class ConnectionToClient implements Runnable {

    private final UUID id;
    private final Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private volatile boolean active;

    public ConnectionToClient(Socket socket) {
        this.socket = socket;
        this.id = UUID.randomUUID();
        this.active = true;
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (active) {
            try {
                Message message = (Message) ois.readObject();
                message.setId(id);
                System.out.println("Se recibio un mensaje desde el cliente " + id);
                Main.messages.add(message);

                if (message.getType() == Message.Type.CLOSE)
                    break;

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }


    public UUID getId() {
        return id;
    }


    public ObjectOutputStream getOos() {
        return oos;
    }


    public void setActive(boolean active) {
        this.active = active;
    }

}
