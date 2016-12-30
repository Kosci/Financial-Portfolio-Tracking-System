package trunk.Control;

import trunk.Model.Equity;
import trunk.Model.Portfolio;

import java.util.ArrayList;
import java.util.TimerTask;

public class UpdateEQ {

    public void updateEquities(Portfolio portfolio, ArrayList<Equity> equities) throws Exception {
        YahooGET get = new YahooGET();
        String yahooXML = get.yahooCall(get.formatSymbols(equities));

        YahooParse parse = new YahooParse();
        ArrayList<Double> askPrice = parse.parseXML(yahooXML, portfolio);


        for (int i = 0; i < equities.size(); i++) {
            equities.get(i).acquisitionPrice = askPrice.get(i);
        }

    }
}