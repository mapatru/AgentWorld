package vaworld;

public abstract class Agent
{

    public  AgentBody body;

    public  int score;

    public  Object percept;

    public  String action;


    public Agent()
    {
        body = new AgentBody();
        score = 0;
    }


    public abstract void determineAction();

    public abstract void takeAction(Environment e);

}
