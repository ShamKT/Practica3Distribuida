import java.io.Serializable;
import java.util.LinkedList;
import java.util.UUID;

public class Message implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3264220573289994916L;

    public enum Type {
        ADDEDIT, GET, GETLIST, CLOSE, SEND, SENDLIST
    }

    private Type type;
    private String[] wordDefinition;
    private LinkedList<String> list;
    private UUID id;

    
    public Message(Type type) {
        this.type = type;
    }
    
    public Message(Type type, String[] wordDefiniton) {
        this.type = type;
        this.wordDefinition = wordDefiniton;
    }


    public Message(Type type, LinkedList<String> list) {
        this.type = type;
        this.list = list;
    }


    public String getWord() {
        return wordDefinition[0];
    }


    public void setWord(String word) {
        this.wordDefinition[0] = word;
    }


    public String getDefinition() {
        return wordDefinition[1];
    }


    public void setDefinition(String definition) {
        this.wordDefinition[1] = definition;
    }


    public LinkedList<String> getList() {
        return list;
    }


    public void setList(LinkedList<String> list) {
        this.list = list;
    }


    public Type getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
