package ru.fiw.blockhighlight;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@AllArgsConstructor
public class BlockHighlight {
    @Getter
    private int x;
    @Getter
    private int y;
    @Getter
    private int z;
    @Getter
    private int color;
    @Getter
    private String text;
    @Getter
    private int time;

    public static BlockHighlight getHideBehindBlocks(int time) {
        return new BlockHighlight(0, 0, 0, new Color(0, 0, 0, 0).getRGB(), " ", time);
    }
}
