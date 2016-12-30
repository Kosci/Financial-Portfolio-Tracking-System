package trunk.Control;

import trunk.Model.Equity;
import trunk.Model.Portfolio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class YahooGET {

    String urlCall = "";

    public String yahooCall(String symbolString) throws IOException {

        String url =
                String.format("http://query.yahooapis.com/v1/public/yql?q=select%%20*%%20from%%20yahoo.finance.quotes%%20where%%20symbol%%20in%%20(%s)&env=store://datatables.org/alltableswithkeys", symbolString);

        URL YahooURL = new URL(url);
        HttpURLConnection con = (HttpURLConnection) YahooURL.openConnection();

        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }

    public String formatSymbols(ArrayList<Equity> equities){
        int i = 0;

        while( i < equities.size() ){
            if (i == equities.size() - 1) {
                urlCall += "%22";
                urlCall += equities.get(i).tickerSymbol;
                urlCall += "%22";
            } else {
                urlCall += "%22";
                urlCall += equities.get(i).tickerSymbol;
                urlCall += "%22";
                urlCall += "%2C";
                urlCall += "%20";
            }
            i++;
        }
        return urlCall;
    }

}
