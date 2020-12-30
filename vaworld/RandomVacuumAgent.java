package vaworld;

public class RandomVacuumAgent extends VacuumAgent
{

    public int getAction()
    {
        int i = (int)Math.floor(Math.random()*5);
        switch (i) {
            case 0: return this.FORWARD;
            case 1: return this.RIGHT;
            case 2: return this.LEFT;
            case 3: return this.OFF;
            case 4: return this.COLLECT;
        }
        return this.FORWARD;
    }
}
