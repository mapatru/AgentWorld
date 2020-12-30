package vaworld;

import java.awt.*;
import java.util.Vector;



public abstract class Obj {

    public String name = new String();

    public boolean alive = false;

    public Location loc = new Location(1, 1);

    public Location heading = new Location(1, 0);

    public boolean bump = false;

    public Vector container = new Vector();

    public boolean sound = false;


    public abstract void draw(Graphics g, Point p, int cellSize);


}