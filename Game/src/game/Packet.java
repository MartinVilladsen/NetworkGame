package game;

import java.io.Serializable;

public class Packet implements Serializable {
    private Player player;
    private String keypress;

    public Packet(Player player, String keypress) {
        this.player = player;
        this.keypress = keypress;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getKeypress() {
        return keypress;
    }

    public void setKeypress(String keypress) {
        this.keypress = keypress;
    }
}
