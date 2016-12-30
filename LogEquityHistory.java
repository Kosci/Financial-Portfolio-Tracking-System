package trunk.Model;

/**
 * A LogHistory class that represents Equity History.
 */
public class LogEquityHistory implements LogHistory {
    @Override
    public void log(Portfolio portfolio, History history) {
        portfolio.equityHistory.add(history);
    }
}
