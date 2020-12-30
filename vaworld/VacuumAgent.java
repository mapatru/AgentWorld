package vaworld;


import java.util.*;


public abstract class VacuumAgent extends Agent
{
    public final int OFF =0;
    public final int COLLECT = 1;
    public final int LEFT = 2;
    public final int RIGHT = 3;
    public final int FORWARD = 4;

    public HashMap<Integer,String> map = new HashMap<Integer,String>();

    public  String lastAction = null;

    public VacuumAgent(){
        super();
        map.put(OFF, "shut-off");
        map.put(COLLECT, "collect");
        map.put(LEFT, "turn left");
        map.put(RIGHT, "turn right");
        map.put(FORWARD, "forward");
    }


    public void shutOff( )
    {
        body.alive=false;
    }


    void collect(VacuumFloor vw)
    {
        vw.grab(body);
    }


    public void takeAction(Environment e)
    {
        VacuumFloor vw = (VacuumFloor) e;
        if (action.equals("collect")) collect(vw);
        else if (action.equals("forward")) vw.forward(body);
        else if (action.equals("turn right")) vw.turn(body,"right");
        else if (action.equals("turn left")) vw.turn(body,"left");
        else if (action.equals("shut-off")) shutOff();
    }

    public boolean bumped(){
        Vector p = (Vector)percept;
        return p.elementAt(0).equals("bump");
    }

    public boolean seesDirt(){
        Vector p = (Vector)percept;
        return p.elementAt(1).equals("cobj");
    }

    public boolean isHome(){
        Vector p = (Vector)percept;
        return p.elementAt(2).equals("home");
    }

    public void determineAction(){
        int act = this.getAction();
        action = map.get(act);
        lastAction = this.action;
    }

    public abstract int getAction();
}