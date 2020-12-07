import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class Connection  implements Runnable{

    private final Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private final CountDownLatch countDownLatch;

    private Character type;

    public Connection(Socket socket, CountDownLatch countDownLatch) {
        this.socket = socket;
        this.countDownLatch = countDownLatch;
        this.type = null;
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
                if (countDownLatch != null) {
                    type = ois.readChar();
                    countDownLatch.countDown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        while (true) {
            try {
                Message message = (Message) ois.readObject();
                Main.messages.add(message);

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ObjectOutputStream getOos() {
        return oos;
    }


    public synchronized void setType(char type){
        this.type = type;
    }

    public synchronized char getType(){
        return type;
    }
}
