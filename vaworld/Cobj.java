package vaworld;

import java.awt.*;


// Collectable Object
public class Cobj extends Obj
{
    public void draw(Graphics g, Point p, int cellSize)
    {
        g.setColor(Color.PINK);
        g.fillRect(p.x+1,p.y+1,cellSize-1,cellSize-1);
    }
}