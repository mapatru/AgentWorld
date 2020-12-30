package vaworld;



public abstract class Environment implements Runnable
{

    public Agent[] agents = new Agent[0];

    public int step = 0;

    public int maxSteps = 1000;

    public boolean display = true;

    public Thread runner;

    public abstract Object getPercept(Agent a);

    public abstract int performanceMeasure(Agent a);

    public abstract void snapshot();

    public abstract boolean termination();

    public abstract boolean legalAction(String a);


    public void start()
    {
        runner = new Thread(this);
        runner.start();
    }

    public void stop()
    {
        runner = null;
    }


    public void run()
    {
        Simulator mySim = new Simulator();
        mySim.start();
    }


    public void takeStep()
    {
        if (step < maxSteps && !termination()) {
            step++;

            for (int j=0; j<agents.length; j++)
            {
                Agent a = agents[j];
                a.percept= getPercept(a);
                a.determineAction();
            }

            updateEnv();

            for (int j=0; j<agents.length; j++)
                agents[j].score=performanceMeasure(agents[j]);
            displayEnv();
        }
    }


    public void displayEnv()
    {
        if (display) {
            snapshot();
            try { Thread.sleep(300); }
            catch(Exception e) {}
        }
    }


    public void updateEnv()
    {
        executeAgentActions();
    }


    public void executeAgentActions()
    {

        for (int i=0; i<agents.length; i++) {
            String act = agents[i].action;
            if (legalAction(act))
                agents[i].takeAction(this);
        }
    }
    protected class Simulator extends Thread {
        public void run() {
            displayEnv();
            while (step < maxSteps && !termination()) {
                takeStep();
            }
        }
    }
}
