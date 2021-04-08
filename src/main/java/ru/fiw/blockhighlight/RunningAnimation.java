package ru.fiw.blockhighlight;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@RequiredArgsConstructor
public class RunningAnimation {
    @NonNull
    private Animation animation;
    private int tick = 0;
    private boolean force = false;
    @NonNull
    private Player pl;

    public void render() {
        ArrayList<BlockHighlight> animationFrames = animation.frames.get(tick);
        if (animationFrames == null) {
            return;
        }

        if (pl.getLocation().distanceSquared(animation.location) < animation.viewDistanceSquared || force) {
            for (BlockHighlight frame : animationFrames) {
                Util.sendBlockHighlight(pl, frame);
            }
        }

    }

    public boolean isLastTick() {
        return tick == animation.lastTick;
    }

    public void addTick() {
        tick++;
    }
}
