import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Singleton object that manages the server state
public class ServerState {

    private static ServerState instance;
    private List<ClientConnection> connectedClients;
    private HashMap<String, List<ClientConnection>> createdRoom;
    private HashMap<String, List<ClientConnection>> createdRoomInGameState;

    private ServerState() {
        connectedClients = new ArrayList<>();
        createdRoom = new HashMap<String, List<ClientConnection>>();
        createdRoomInGameState = new HashMap<String, List<ClientConnection>>();
    }

    public static synchronized ServerState getInstance() {
        if(instance == null) {
            instance = new ServerState();
        }
        return instance;
    }

    //########For connectedClients#######
    public synchronized void clientConnected(ClientConnection client) {
        connectedClients.add(client);
    }

    public synchronized void clientDisconnected(ClientConnection client) {
        connectedClients.remove(client);
    }

    public synchronized List<ClientConnection> getConnectedClients() {
        return connectedClients;
    }

    //########For room#######
    public synchronized void roomCreated(String roomName) {
        createdRoom.put(roomName,new ArrayList<ClientConnection>());
    }

    public synchronized void addClientIntoRoom(String roomName, ClientConnection client) {
        createdRoom.get(roomName).add(client);
    }

    public synchronized HashMap<String, List<ClientConnection>> getRoom() {
        return createdRoom;
    }

    //########For room in game state#######
    public synchronized void createdRoomInGameState(String roomName) {
        createdRoomInGameState.put(roomName,new ArrayList<ClientConnection>());
    }

    public synchronized void logoutRoomInGameState(String roomName) {
        createdRoomInGameState.remove(roomName);
    }

    public synchronized HashMap<String, List<ClientConnection>> getCreatedRoomInGameState() {
        return createdRoomInGameState;
    }
}