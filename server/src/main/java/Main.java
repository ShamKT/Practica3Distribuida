import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    static volatile LinkedList<Message> messages;
    private static ServerSocket server;
    private static Socket toPeer;
    private static HashMap<UUID, ConnectionToClient> connections;

    public static void main(String[] args) {

        connections = new HashMap<>();
        messages = new LinkedList<>();

        try {
            server = new ServerSocket(8000);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while (toPeer == null) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(0, 501));
                // toPeer = new Socket("Localhost", ThreadLocalRandom.current().nextInt(8001, 8003));
                toPeer = new Socket("Localhost", 8001);

                System.out.println("Conectado con peer");
            } catch (ConnectException e) {
                System.out.println("Fallo la conexión con el peer, reintentando...");
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        ObjectOutputStream oosPeer = null;

        try {
            oosPeer = new ObjectOutputStream(toPeer.getOutputStream());
            oosPeer.writeChar('S');
            oosPeer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        new Thread(() -> {
            ObjectInputStream oisPeer;
            try {
                oisPeer = new ObjectInputStream(toPeer.getInputStream());
                while (true) {
                    messages.add((Message) oisPeer.readObject());
                    System.out.println("Se recibio un mensaje desde el peer");

                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }, "Peer-Input").start();


        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Esperando a un cliente");
                    ConnectionToClient client = new ConnectionToClient(server.accept());
                    System.out.println("Conectado con un cliente");
                    new Thread(client, "Client-" + client.getId().toString()).start();
                    connections.put(client.getId(), client);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, "ServerSocket-Accept").start();


        while (true) {
            if (!messages.isEmpty()) {
                Message message = messages.pop();

                switch (message.getType()) {
                    case ADDEDIT:
                    case GET:
                    case GETLIST:

                        try {
                            oosPeer.writeObject(message);
                            oosPeer.flush();
                            System.out.println("Se envio un mensaje hacia el peer");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;

                    case CLOSE:

                        connections.remove(message.getId());

                        break;

                    case SEND:
                    case SENDLIST:

                        try {
                            connections.get(message.getId()).getOos().writeObject(message);
                            connections.get(message.getId()).getOos().flush();
                            System.out.println("Se envio un mensaje hacia el cliente " + message.getId().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    default:
                        break;
                }
            }

        }
    }

}