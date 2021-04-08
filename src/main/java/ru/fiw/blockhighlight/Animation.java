package ru.fiw.blockhighlight;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;

public class Animation {
    public HashMap<Integer, ArrayList<BlockHighlight>> frames = new HashMap<>();
    public int lastTick = 0;
    public Location location;
    public int viewDistanceSquared;
}
