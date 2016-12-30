package trunk.Control;

import trunk.Model.*;

/**
 * With this simulation, the equity prices never change. This class implements
 * the FutureSimulation interface, and acts as a concrete strategy within the
 * Strategy design pattern.
 */
public class NoGrowth  implements FutureSimulation {

    /**
     * The method to run the simulation.
     * @param portfolio : A Portfolio object that will run the simulation.
     * @param percentage : A double that represents a per annum percentage.
     * @param interval : An int that represents a period of time.
     * @param steps : An int that represents the number of intervals to run.
     * @return : A double representing the equity price after the simulation.
     */
    public double[] simulate(Portfolio portfolio, double percentage, int interval, int steps) {
        steps += 1;
        double[] finalValues = new double[steps];
        double finalValue = 0;
        double cashValue = 0;

        for (int i = 0; i < portfolio.equityList1.size(); i++) {
            Equity e = portfolio.equityList1.get(i);
            finalValue += e.shares * e.acquisitionPrice;
        }

        for (int i = 0; i < portfolio.cashAccList.size(); i++) {
            CashAccount ca = portfolio.cashAccList.get(i);
            cashValue += ca.balance;
        }

        finalValues[0] = finalValue + cashValue;
        for (int j = 1; j  < steps; j++) {
            finalValues[j] = finalValue + cashValue;
        }

        return finalValues;
    }

    @Override
    public double[] simulate(Equity e, double percentage, int interval, int steps) {
        double[] vals = new double[steps];
        double initValue = e.shares * e.acquisitionPrice;
        vals[0] = initValue;

        for (int i = 1; i < steps; i++) {
            vals[i] = initValue;
        }

        return vals;
    }
}
