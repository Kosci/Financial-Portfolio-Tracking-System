package trunk.Model;

/**
 * A LogHistory class that represents Cash Account History.
 */


public class LogCAHistory implements LogHistory {
    @Override
    public void log(Portfolio portfolio, History history) {
        portfolio.cashAccountHistory.add(history);
    }
}
