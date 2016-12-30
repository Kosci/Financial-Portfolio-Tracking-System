/**
 * A class that acts as the Invoker in the command design pattern.
 * Gets a request from a Portfolio or a Cash Account and has LogHistory
 * run the appropriate command.
 */
package trunk.Control;

import trunk.Model.*;

public class HistoryInvoker {
    private LogHistory event;
    private trunk.Model.Portfolio portfolio;
    private History history;

    /**
     * A constructor for the HistoryInvoker.
     * Instantiates which type of log should be used.
     * @param event : A LogHistory object representing the type of log to be used.
     * @param portfolio : The Portfolio involved with the events occurring.
     * @param history : A History object representing the event that occurred.
     */
    public void setLog(LogHistory event, trunk.Model.Portfolio portfolio, History history){
        this.event = event;
        this.portfolio = portfolio;
        this.history = history;
    }

    /**
     * Sends the portfolio involved with the event that occurred and the history object
     * representing that event to the appropriate log type.
     */
    public void logAction(){
        event.log(portfolio, history);
    }
}
