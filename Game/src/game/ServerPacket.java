package game;

import java.io.Serializable;
import java.util.List;

public class ServerPacket implements Serializable {
    List<Player> players;

    public ServerPacket(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
