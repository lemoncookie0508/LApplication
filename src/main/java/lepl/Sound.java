package lepl;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.InputStream;

public abstract class Sound extends Thread {

    private Player player;
    private boolean isLoop;
    private final String sound;

    public Sound(String sound, boolean isLoop) {
        this.isLoop = isLoop;
        this.sound = sound;
        try {
            this.player = new Player(getInputStreamToString(sound));
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        do {
            try {
                player.play();
                player = new Player(getInputStreamToString(sound));
            } catch (JavaLayerException e) {
                throw new RuntimeException(e);
            }
        } while (isLoop);
    }

    public void close() {
        isLoop = false;
        player.close();
        interrupt();
    }

    public abstract InputStream getInputStreamToString(String sound);
}
