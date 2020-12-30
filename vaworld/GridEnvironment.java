package vaworld;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import java.util.ArrayList;
import java.util.Vector;


public abstract class GridEnvironment extends Environment implements Cloneable
{
    public static final Location LEFT = new Location(-1, 0);
    public static final Location RIGHT = new Location(1, 0);
    public static final Location UP = new Location(0, 1);
    public static final Location DOWN = new Location(0, -1);

    public Location size;

    public Object grid[][];

    public boolean visited[][];

    public Location start;

    public GridCanvas canvas;

    GridPanel gridPanel;
    protected boolean newlyVisited;

    JFrame holder;

    public GridEnvironment(Agent[] a, int xsize, int ysize, JFrame f)
    {
        holder = f;
        size= new Location(xsize+2,ysize+2);
        grid = new Object[xsize+2][ysize+2];
        visited = new boolean[xsize+2][ysize+2];
        for(int i = 0; i < xsize+2; i++)
            for(int j = 0; j < ysize+2; j++)
                visited[i][j] = false;
        visited[1][1] = true;
        newlyVisited = true;
        start = new Location(1,1);
        agents = a;
        placeWalls();
        initGrid();
        canvas = new GridCanvas();
        gridPanel = new GridPanel();
    }
    public Object clone() {
        Object copy = null;
        try {
            return super.clone();
        } catch(Exception exception) {};
        return null;
    }


    public void forward(AgentBody body)
    {
        Location newloc = body.loc.forward(body.heading);
        Vector v = (Vector)grid[newloc.x][newloc.y];
        if (v.size()>0 && v.firstElement() instanceof Wall)
            body.bump=true;
        else
        {
            removeObj(body.loc, body);  // move body to new location
            addObj(newloc, body);
            if(visited[newloc.x][newloc.y] == false) newlyVisited = true;
            visited[newloc.x][newloc.y] = true;
        }
    }


    public void turn(AgentBody body, String direction)

    {
        Location[] headings = {new Location(1,0), new Location(0,1),
                new Location(-1,0), new Location(0,-1)};
        int now = pos(body.heading,headings);
        if (direction.equals("right"))
            body.heading=headings[(now + 3) % 4];
        else if (direction.equals("left"))
            body.heading=headings[(now + 1) % 4];
    }


    public void grab(AgentBody body)
    {
        body.grabbed = false;
        Location loc = body.loc;
        Vector v = (Vector)grid[loc.x][loc.y];
        for(int i=0; i<v.size(); i++) {
            Obj o = (Obj)v.elementAt(i);
            if (!(o instanceof Wall) &&
                    !(o instanceof AgentBody)) {
                body.container.addElement(o);
                v.removeElement(o);
                body.grabbed = true;
            }
        }
    }


    public boolean termination()
    {
        boolean terminate = true;
        for (int i=0; i<agents.length; i++)
            if (agents[i].body.alive)
                terminate = false;
        return terminate;
    }


    public void updateEnv()
    {
        for (int i=0; i<agents.length; i++)
            // dissipate bumps
            agents[i].body.bump=false;
        newlyVisited = false;
        executeAgentActions();
    }


    public void removeObj(Location loc, Obj ob)
    {
        Vector v = (Vector)grid[loc.x][loc.y];
        v.removeElement(ob);
    }


    public void addObj(Location loc, Obj ob)
    {
        Vector v = (Vector)grid[loc.x][loc.y];
        v.addElement(ob);
        ob.loc=loc;
    }


    void placeWalls()
    {
        Wall w = new Wall();
        for (int i=0; i<size.x; i++){
            Vector v1 = new Vector();
            Vector v2 = new Vector();
            v1.addElement(w);
            v2.addElement(w);
            grid[i][0] = v1;
            grid[i][size.y-1] = v2;
        }
        for (int i=1; i<size.y-1; i++){
            Vector v1 = new Vector();
            Vector v2 = new Vector();
            v1.addElement(w);
            v2.addElement(w);
            grid[0][i] = v1;
            grid[size.x-1][i] = v2;
        }
    }


    void initGrid()
    {
        for (int i=1; i<size.x-1; i++)
            for (int j=1; j<size.y-1; j++){
                Vector v = new Vector();
                grid[i][j] = v;
            }
        for (int i=0; i<agents.length; i++){
            Location loc = agents[i].body.loc;
            Vector v = (Vector)grid[1][1];
            v.addElement(agents[i].body);
        }
    }


    public void fillGrid(double prob, Class c)
    {
        Object a = ((Vector) grid[1][1]).elementAt(0);
        ((Vector) grid[1][1]).removeElementAt(0);
        for (int i=1; i<size.x-1; i++)
            for (int j=1; j<size.y-1; j++){
                if (Math.random() < prob)
                    try{
                        addObj(new Location(i,j), (Obj)c.newInstance());
                    }
                    catch(Exception e) {System.out.println(e);}
            }
        ((Vector) grid[1][1]).addElement(a);
    }


    public void revisedFillGrid(int numStates, Class c)
    {
        //System.out.println("Using new fill grid with "+numStates+ " states.");
        Object a = ((Vector) grid[1][1]).elementAt(0);
        ((Vector) grid[1][1]).removeElementAt(0);

        int count = 0;
        ArrayList<Point> points = new ArrayList<Point>();
        while(count<numStates){
            int x= (int)(Math.random()*(size.x-2)+1);
            int y= (int)(Math.random()*(size.y-2)+1);
            Point p = new Point(x,y);

            if(!points.contains(p)){

                try{
                    addObj(new Location(x,y), (Obj)c.newInstance());
                }
                catch(Exception e) {System.out.println(e);}
                count++;
                points.add(p);
                //System.out.println(" count is "+count);
            }

        }
        ((Vector) grid[1][1]).addElement(a);
    }

    public void fillLoc(Location loc, Class c) {
        try {
            addObj(loc, (Obj) c.newInstance());
        } catch(Exception e) {System.out.println(e);}
    }


    public boolean contains(Vector v, Class c)
    {
        boolean holds = false;
        for (int i=0; i<v.size(); i++)
            if ( (c.isInstance(v.elementAt(i))) )
                holds = true;
        return holds;
    }


    public Obj getItem(Vector v, Class c)
    {
        for (int i=0; i<v.size(); i++)
            if ( (c.isInstance(v.elementAt(i))) )
                return (Obj) v.elementAt(i);
        return null;
    }


    public boolean neighbor(Location loc, Class c)
    {
        return contains((Vector)grid[loc.x][loc.y+1],c) ||
                contains((Vector)grid[loc.x+1][loc.y],c) ||
                contains((Vector)grid[loc.x][loc.y-1],c) ||
                contains((Vector)grid[loc.x-1][loc.y],c);
    }


    int pos(Location heading, Location[] headings)
    {
        for (int i=0; i<headings.length; i++)
            if (heading.x == headings[i].x &&
                    heading.y == headings[i].y)
                return i;
        return -1;
    }


    public void snapshot()
    {
        canvas.moveflag = true;
        if(canvas.getGraphics() != null) {
            canvas.update(canvas.getGraphics());
        }
        if (agents[0].action !=null) {
            gridPanel.action.setText(agents[0].action);
            Vector percept = (Vector) (agents[0].percept);
            String p = new String();
            for (int i = 0; i<percept.size(); i++)
                p = p + " " + percept.elementAt(i);
            gridPanel.percepts.setText(p);
        }
        gridPanel.score.setText(Integer.toString(agents[0].score));
        gridPanel.steps.setText(Integer.toString(step));
        gridPanel.repaint(0);

    }

    public static final void copyGrid (GridEnvironment from,
                                       GridEnvironment to, int xsize,int ysize)
    {
        Object[][] newGrid = new Object[xsize][ysize];
        for (int i=0; i<xsize; i++)
            for (int j=0; j<ysize; j++) {
                Vector vnew = new Vector();
                Vector vold = (Vector)from.grid[i][j];
                for (int k = 0; k<vold.size(); k++) {
                    Class c = vold.elementAt(k).getClass();
                    Obj o = null;
                    try { o = (Obj)c.newInstance();}
                    catch(Exception ex) {};
                    vnew.addElement(o);
                }
                newGrid[i][j] = vnew;
            }
        to.grid = newGrid;
    }


    public class GridCanvas extends Canvas
    {
        int cellSize = 35;
        int startx = 25;
        int starty = 25;
        int endx, endy;
        int startx2 = 0, endx2 = 0;
        int lastx = 1;
        int lasty = 1;
        int currentx = 1;
        int currenty = 1;
        boolean moveflag = false;

        public GridCanvas()
        {
            Color metalColor = MetalLookAndFeel.getDesktopColor();
            Graphics g = getGraphics();
            setBackground(Color.WHITE);
            if (size.x>12 || size.y>10) cellSize = 280/(size.x-2);
            startx2 = endx + 60;
            endx2 = startx2 + (size.x-2)*cellSize;
        }

        public void paint(Graphics g)
        {
            int numRows = size.y-2;
            int numCols = size.x-2;
            Location loc = agents[0].body.loc;
            currentx = loc.x;
            currenty = loc.y;

            if (! moveflag){
                endx = startx + numCols*cellSize;
                endy = starty + numRows*cellSize;
                startx2 = endx + 60;
                endx2 = startx2 + numCols*cellSize;
                g.setColor(Color.white);
                g.fillRect(startx,starty,numCols*cellSize,numRows*cellSize);
                g.fillRect(startx2, starty, numCols*cellSize, numRows*cellSize);
            }

            // draw row and col lines
            g.setColor(Color.black);
            for (int i=0; i<=numCols; i++) {
                g.drawLine(startx+i*cellSize, starty,
                        startx+i*cellSize, endy);
                g.drawLine(startx2+i*cellSize, starty,
                        startx2+i*cellSize, endy);
            }
            for (int i=1; i<=numCols; i++) {
                g.drawString(Integer.toString(i),
                        startx+i*cellSize-cellSize/2,endy+15);
                g.drawString(Integer.toString(i),
                        startx2+i*cellSize-cellSize/2,endy+15);
            }
            for (int i=0; i<=numRows; i++) {
                g.drawLine(startx, starty+i*cellSize, endx,
                        starty+i*cellSize);
                g.drawLine(startx2, starty+i*cellSize, endx2,
                        starty+i*cellSize);
            }
            for (int i=0; i<numRows; i++) {
                g.drawString(Integer.toString(numRows-i),startx-15,
                        starty+(i+1)*cellSize-cellSize/2);
                g.drawString(Integer.toString(numRows-i),startx2-15,
                        starty+(i+1)*cellSize-cellSize/2);
            }
            // draw grid objects
            for (int i=1; i<=numCols; i++)
                for (int j=1; j<=numRows; j++){
                    Vector v = (Vector) grid[i][j];
                    for (int k=0; k<v.size(); k++) {
                        ((Obj)v.elementAt(k)).draw(g, screenpos(i,j),
                                cellSize);
                    }
                }
            g.setColor(Color.black);

            VacuumAgent vAgent = null;

            if(agents[0] instanceof VacuumAgent) vAgent = (VacuumAgent) agents[0];

            if(vAgent != null) {
                for (int x=1; x<=size.x-2; x++) {
                    for (int y=1; y<=size.y-2; y++) {
                        if(visited[x][y]) g.drawString("V", startx2 + (x-1)*cellSize + cellSize/2, endy - y*cellSize + cellSize/2);
                    }
                }
            }

            lastx = currentx;
            lasty = currenty;
            moveflag = false;
        }

        public void update(Graphics g)
        {

            g.setColor(Color.white);
            g.fillRect(screenpos(lastx,lasty).x, screenpos(lastx,lasty).y,
                    cellSize,cellSize);
            g.fillRect(screenpos(currentx,currenty).x,
                    screenpos(currentx,currenty).y,cellSize,cellSize);
            paint(g);
        }

        Point screenpos(int x,int y)
        {
            return new Point(startx+cellSize*(x-1),endy-cellSize*y);
        }

        Point screenpos2(int x, int y) {
            return new Point(startx2 + cellSize*(x-1), endy-cellSize*y);
        }

        public void updateHere(Location oldloc, Location newloc,Graphics g)
        {
            g.setColor(Color.white);
            Point p1 = screenpos(oldloc.x,oldloc.y);
            Point p2 = screenpos(newloc.x, newloc.y);
            g.fillRect(p1.x, p1.y,cellSize,cellSize);
            g.fillRect(p2.x, p2.y,cellSize,cellSize);
            paint(g);
        }
    }

    class GridPanel extends JPanel
            implements ActionListener
    {
        JLabel score = new JLabel(" 0");
        JLabel percepts = new JLabel ("        ");
        JLabel action = new JLabel ("        ");
        JLabel steps = new JLabel(" 0");
        JTextField maxStepsField = new JTextField("1000",4);
        JButton run = new JButton("Run");
        JButton step = new JButton("Step");

        GridPanel()
        {
            setBackground(Color.white);
            percepts.setForeground(Color.darkGray);
            action.setForeground(Color.darkGray);
            steps.setForeground(Color.darkGray);
            score.setForeground(Color.darkGray);
            JPanel p1 = new JPanel();
            p1.setBackground(Color.white);
            p1.setLayout(new GridLayout(1,4));
            p1.add(new JLabel("   Agent Percepts: "));
            p1.add(percepts);
            p1.add(new JLabel("   Agent Action: "));
            p1.add(action);

            JPanel p2 = new JPanel();
            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(2, 8, 2, 8);
            constraints.gridwidth = 1;
            p2.setBackground(Color.white);
            p2.setLayout(gridbag);
            JLabel label = new JLabel("Steps: ");
            gridbag.setConstraints(label, constraints);
            p2.add(label);
            gridbag.setConstraints(steps, constraints);
            p2.add(steps);
            JLabel label2 = new JLabel("Score: ");
            gridbag.setConstraints(label2, constraints);
            p2.add(label2);
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(score, constraints);
            p2.add(score);
            constraints.gridwidth = 1;
            JLabel label3 = new JLabel("Max Steps: ");
            gridbag.setConstraints(label3, constraints);
            p2.add(label3);
            gridbag.setConstraints(maxStepsField, constraints);
            p2.add(maxStepsField);

            JPanel p3 = new JPanel();
            p3.setLayout(new GridLayout(1,3));
            run.addActionListener(this);
            p3.add(run);
            step.addActionListener(this);
            p3.add(step);

            JPanel p4 = new JPanel();
            p4.setBackground(Color.white);
            p4.setLayout(new BorderLayout());
            p4.add("West",p1);
            p4.add("East",p2);

            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createLoweredBevelBorder()));
            setLayout(new BorderLayout());
            add("West",p3);
            add("Center",p4);
        }

        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();
            if (action.equals("Run")){
                run.setEnabled(false);
                step.setEnabled(false);
                maxSteps = Integer.parseInt(maxStepsField.getText());
                run();
            }
            else if (action.equals("Step")) {
                maxSteps = Integer.parseInt(maxStepsField.getText());
                takeStep();
            }
        }

    }
}