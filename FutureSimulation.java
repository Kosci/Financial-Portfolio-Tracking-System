package trunk.Control;
/**
 * Created by zachary on 3/6/2016.
 */

import trunk.Model.*;

/**
 * Created by zachary on 3/6/2016.
 */
public interface FutureSimulation {
    public double[] simulate(Portfolio p, double percentage, int interval, int steps);
    public double[] simulate(Equity e, double percentage, int interval, int steps);
}
