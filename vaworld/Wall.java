package vaworld;

import java.awt.*;

public class Wall extends Obj
{

    public void draw(Graphics g, Point p, int cellSize)
    {
        g.setColor(Color.black);
        g.fillRect(p.x,p.y,cellSize,cellSize);
    }
}
