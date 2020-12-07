import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    static volatile LinkedList<Message> messages = new LinkedList<>();
    static volatile HashMap<Character, Connection> connections = new HashMap<>();

    private static ServerSocket server;
    private static String type;

    public static void main(String[] args) {

        type = args[2];


        try {
            server = new ServerSocket(Integer.parseInt((args[0])));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Esperando una conexion");
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    Connection client = new Connection(server.accept(), countDownLatch);
                    new Thread(client).start();
                    countDownLatch.await();

                    connections.put(client.getType(), client);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }, "ServerSocket-Accept").start();

        if (type.equals("AM")) {
            Socket toPeer = null;

            while (toPeer == null) {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(0, 501));
                    toPeer = new Socket("localhost", Integer.parseInt(args[1]));
                } catch (ConnectException e) {
                    System.out.println("Fallo la conexión con el peer, reintentando...");
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
            Connection peer = new Connection(toPeer, null);
            peer.setType('P');
            try {
                peer.getOos().writeChar('P');
                peer.getOos().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(peer).start();
            connections.put(peer.getType(), peer);
        }


        while (true) {
            if (!messages.isEmpty()) {
                Message message = messages.pop();

                Message messageBack = null;

                if (message.getType() == Message.Type.SEND)
                    sendBack(message);

                else if (message.getType() == Message.Type.SENDLIST) {
                    LinkedList<String> resultList = selectWordList();
                    message.getList().addAll(resultList);
                    message.getList().sort(Comparator.naturalOrder());
                    sendBack(message);

                } else if (message.getType() == Message.Type.GETLIST) {

                    if (connections.containsKey('S')) {
                        try {
                            connections.get('P').getOos().writeObject(message);
                            connections.get('P').getOos().flush();
                            System.out.println("Mensaje enviado al peer");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LinkedList<String> resultList = selectWordList();
                        messageBack = new Message(Message.Type.SENDLIST, resultList);
                        messageBack.setId(message.getId());
                        sendBack(messageBack);
                    }
                } else {

                    String word = message.getWord();

                    if ((((word.charAt(0) >= 65 && word.charAt(0) <= 77) || (word.charAt(0) >= 97 && word.charAt(0) <= 109)) && type.equals("AM"))
                            || (((word.charAt(0) >= 78 && word.charAt(0) <= 90) || (word.charAt(0) >= 110 && word.charAt(0) <= 122)) && type.equals("NZ"))) {

                        switch (message.getType()) {
                            case ADDEDIT:
                                if (selectWord(word) == null)
                                    insertWord(word, message.getDefinition());
                                else {
                                    updateWord(word, message.getDefinition());
                                }
                                break;

                            case GET:
                                String[] resultWord = selectWord(word);
                                if (resultWord == null)
                                    messageBack = new Message(Message.Type.SEND, new String[] {"",""});
                                else
                                    messageBack = new Message(Message.Type.SEND, resultWord);

                                messageBack.setId(message.getId());
                                sendBack(messageBack);

                                break;

                            default:
                                break;
                        }
                    } else {
                        try {
                            connections.get('P').getOos().writeObject(message);
                            connections.get('P').getOos().flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    private static void sendBack(Message message) {
        if (connections.containsKey('S')) {
            try {
                connections.get('S').getOos().writeObject(message);
                connections.get('S').getOos().flush();
                System.out.println("Mensaje enviado al servidor");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                connections.get('P').getOos().writeObject(message);
                connections.get('P').getOos().flush();
                System.out.println("Mensaje enviado al peer");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void insertWord(String word, String definition) {
        try {

            PreparedStatement insert = ConexionMySQL.getInstance().getConnection()
                    .prepareStatement("INSERT INTO palabra (palabra, significado) VALUES (?, ?)");
            insert.setString(1, word);
            insert.setString(2, definition);

            insert.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void updateWord(String word, String definition) {
        try {

            PreparedStatement insert = ConexionMySQL.getInstance().getConnection()
                    .prepareStatement("UPDATE palabra SET significado = ? WHERE palabra = ?");
            insert.setString(1, definition);
            insert.setString(2, word);

            insert.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static String[] selectWord(String word) {
        try {

            PreparedStatement select = ConexionMySQL.getInstance().getConnection()
                    .prepareStatement("SELECT * FROM palabra WHERE palabra = ?");

            select.setString(1, word);

            ResultSet res = select.executeQuery();

            String[] wordDefinition = new String[2];

            if (res.next()) {
                for (int i = 0; i < 2; i++)
                    wordDefinition[i] = res.getString(i + 2);
                return wordDefinition;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static LinkedList<String> selectWordList() {
        try {
            PreparedStatement select = ConexionMySQL.getInstance().getConnection()
                    .prepareStatement("SELECT palabra FROM palabra");

            ResultSet res = select.executeQuery();

            LinkedList<String> list = new LinkedList<>();

            while (res.next()) {
                list.add(res.getString(1));
            }

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getType() {
        return type;
    }
}
