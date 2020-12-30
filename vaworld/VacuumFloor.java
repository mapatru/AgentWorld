package vaworld;

import java.util.Vector;
import javax.swing.*;

public class VacuumFloor extends GridEnvironment
{
    public VacuumFloor(Agent[] a, int xsize, int ysize, double probDirt, JFrame f) {
        super(a,xsize,ysize, f);

        revisedFillGrid(xsize*ysize/3,(new Cobj()).getClass());
    }


    public boolean legalAction(String a) {
        return (a.equals("collect"))||(a.equals("forward"))||(a.equals("turn right"))||(a.equals("turn left"))||(a.equals("shut-off"));
    }


    public Object getPercept(Agent a) {
        Location loc = a.body.loc;
        Vector v = new Vector(4);
        Vector gr = (Vector) grid[loc.x][loc.y];
        if (a.body.bump) v.addElement("bump");
        else v.addElement("");
        if (contains(gr,(new Cobj()).getClass())) v.addElement("cobj");
        else v.addElement("");
        if (loc.x == 1 && loc.y == 1) v.addElement("home");
        else v.addElement("");
        return v;
    }


    public int performanceMeasure(Agent a) {
        AgentBody body = a.body;
        int score = 100 * body.container.size() - 10*step;
        if (! body.alive && !(body.loc.x==1 && body.loc.y==1)) score = score - 1000;
        a.score=score;
        return score;
    }

}
