package vaworld;

import java.awt.*;


public class Location extends Point
{
    public Location(int xint, int yint) {x=xint; y=yint;}

    public Location forward(Location heading)

    {
        Location p = new Location(0,0);
        p.x = x + heading.x;
        p.y = y + heading.y;
        return p;
    }


    public boolean equals(Location loc) {
        return loc.x == x && loc.y == y;
    }
}
