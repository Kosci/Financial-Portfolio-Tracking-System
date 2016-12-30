package trunk.Model;

import java.io.Serializable;

/**
 * Created by Daniel on 4/14/2016.
 */
public class Watchlist implements Serializable, WatchlistVisitor  {
    public String tickerSymbol;
    public String lowtrigger;
    public String hightrigger;
    public double pershareprice;

    public String compare;
    public String lowtriggered;
    public String hightriggered;

    /**
     * Constructor for an Equity object.
     * @param tickerSymbol: A String representing the market symbol associated with this equity.
     * @param low : A string representing the low trigger.
     * @param high : A string representing the hightrigger.
     * @param price : A double representing the orginal price pad for this equity, per share.
     * @param compare : A String representing that a trigger was tripped
     * @param lowtriggered : A string representing that the low trigger has been tripped.
     * @param hightriggered : A string representing that the high trigger has been tripped.
     */
    public Watchlist(String tickerSymbol, String low, String high, double price, String compare, String lowtriggered, String hightriggered){
        this.tickerSymbol = tickerSymbol;
        this.lowtrigger = low;
        this.hightrigger = high;
        this.pershareprice = price;
        this.compare = compare;
        this.lowtriggered = lowtriggered;
        this.hightriggered = hightriggered;
    }

    @Override
    public void visit(Portfolio p) {
        for(Watchlist w : p.watchlists){
            w.compare = "Within range";
            if(!w.lowtrigger.equals("")) {
                if (w.pershareprice < Double.parseDouble(w.lowtrigger)) {
                    w.compare = "Low Trigger";
                    w.lowtriggered = "Was Tripped";
                }
            }
            if(!w.hightrigger.equals("")) {
                if (w.pershareprice > Double.parseDouble(w.hightrigger)) {
                    w.compare = "High Trigger";
                    w.hightriggered = "Was Tripped";
                }
            }
        }
    }

    //A String representing the market symbol associated with this equity.
    public String getTickerSymbol(){return this.tickerSymbol;}

    //A string representing the low trigger.
    public String getLowtrigger(){return this.lowtrigger;}

    //A string representing the hightrigger.
    public String getHightrigger(){return this.hightrigger;}

    //A double representing the orginal price pad for this equity, per share.
    public double getPershareprice(){return this.pershareprice;}

    //A String representing that a trigger was tripped
    public String getCompare(){return this.compare;}

    //A string representing that the low trigger has been tripped.
    public String getLowtriggered(){return this.lowtriggered;}

    //A string representing that the high trigger has been tripped.
    public String getHightriggered() {return this.hightriggered;}
}
