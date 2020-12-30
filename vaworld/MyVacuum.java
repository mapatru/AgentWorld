package vaworld;

public class MyVacuum extends VacuumAgent {

    public int getAction() {
        if(bumped())
            return this.LEFT;
        else if(seesDirt())
            return this.COLLECT;

        return this.FORWARD;
    }
}
