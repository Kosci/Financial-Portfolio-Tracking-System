package trunk.Model;

import trunk.Control.HistoryInvoker;
import trunk.Control.UpdateEQ;

import java.io.*;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * A class to represent an instance of an Equity.
 * An Equity object represents various real-life equities
 * including stock, bonds, mutual funds, etc.
 */

public class Equity implements Serializable {
    public String tickerSymbol;
    public String name;
    public ArrayList<String> marketAverage;
    public String MA;

    public int shares;
    public double acquisitionPrice;
    public String acquisitionDate;

    /**
     * Constructor for an Equity object.
     * @param tickerSymbol: A String representing the market symbol associated
     *                    with this equity.
     * @param name : A string representing the name of the equity.
     * @param shares : An int representing the number of shares of this equity owned.
     * @param acquisitionPrice : A double representing the orginal price pad for
     *                         this equity, per share.
     * @param acquisitionDate : A String representing the date this equity was purchased.
     * @param marketAverage : An ArrayList representing the index the equity belongs to.
     */
    public Equity(String tickerSymbol, String name, int shares, double acquisitionPrice,
                  String acquisitionDate, ArrayList<String> marketAverage) {

        this.tickerSymbol = tickerSymbol;
        this.name = name;
        this.shares = shares;
        this.acquisitionPrice = acquisitionPrice;
        this.acquisitionDate = acquisitionDate;
        this.marketAverage = marketAverage;
        if(marketAverage != null) {
            this.MA = marketAverage.toString();
        }
    }

    /**
     * A getter for the ticker/market symbol associated with this equity.
     * @return : A string representing the market symbol.
     */
    public String getTickerSymbol() {
        return this.tickerSymbol;
    }

    /**
     * A getter for the name of this equity.
     * @return : A string representing the equity's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * A getter for the market averages this equity belongs to.
     * @return : An ArrayList of the sector index.
     */
    public ArrayList<String> getMarketAverage(){return this.marketAverage;}

    /**
     * A getter for the number of shares of this equity owned.
     * @return : A int representing the number of shares.
     */
    public int getShares(){
        return this.shares;
    }

    /**
     * A getter for the original price paid for this equity.
     * @return : A double representing the acquisition price.
     */
    public double getAcquisitionPrice(){
        return this.acquisitionPrice;
    }

    /**
     * A getter for the date this equity was purchased.
     * @return : A string representing the acquisition date.
     */
    public String getAcquisitionDate(){
        return this.acquisitionDate;
    }

    public String getMA() {
        return this.MA;
    }

    /**
     * A getter for the total value of this equity.
     * @return : A double that consists of the acquisition price multiplied by the
     * number of shares of this equity owned.
     */
    public double getTotal(){
        return Math.round((acquisitionPrice * shares) * 100.0) / 100.0;
    }

    /**
     * Import equities
     */
    public Equity importEQ(String csv, Portfolio p){
        BufferedReader br = null;
        String line = "";
        String[] equity = null;
        Equity e = new Equity("N/A","N/A",0,0,null,null);


        try {
            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null) {
                equity = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if(equity[0].equals("equity")){
                    if (equity.length == 6) {
                        ArrayList<String> marketAverageList = new ArrayList<String>();
                        if(equity[2].contains("^")){
                            //e.tickerSymbol = equity[2].substring(0, equity[2].lastIndexOf("^")).replaceAll("^\"|\"$", "");
                            e.tickerSymbol = equity[2].substring(0, equity[2].lastIndexOf("^")).replaceAll("[^=,\\da-zA-Z]", "");
                        }else{
                            e.tickerSymbol = equity[2].replaceAll("^\\s+|\\s+$", "").replaceAll("[^=,\\da-zA-Z]", "");
                        }
                        e.shares = Integer.parseInt(equity[1].replaceAll("^\"|\"$", ""));
                        e.acquisitionPrice = Double.parseDouble(equity[3].replace("\"", "").replaceAll("^\"|\"$", ""));
                        e.name = equity[4];
                        marketAverageList.add(equity[5].replaceAll("^\"|\"$", ""));
                        e.marketAverage = marketAverageList;
                        e.MA = e.marketAverage.toString().replace("[","").replace("]","");
                        Date date = null;
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            date = format.parse("00/00/0000");
                        } catch (ParseException ee) {
                            ee.printStackTrace();
                        }
                        e.addEquity(e.tickerSymbol, e.name, e.shares, e.acquisitionPrice, "N/A", e.marketAverage, true, p);
                    } else if (equity.length > 6) {
                        ArrayList<String> marketAverageList = new ArrayList<String>();

                        if (equity[2].contains("^")){
                            e.tickerSymbol = equity[2].substring(0, equity[2].lastIndexOf("^")).replaceAll("[^=,\\da-zA-Z]", "");
                        }else{
                            e.tickerSymbol = equity[2].replaceAll("^\\s+|\\s+$", "").replaceAll("[^=,\\da-zA-Z]", "");
                        }

                        e.shares = Integer.parseInt(equity[1].replaceAll("^\"|\"$", ""));
                        e.acquisitionPrice = Double.parseDouble(equity[3].replace("\"", "").replaceAll("^\"|\"$", ""));
                        e.name = equity[4];
                        int x = 5;
                        while (x < equity.length && !equity[x].equals("")) {
                            marketAverageList.add(equity[x].replaceAll("^\"|\"$", ""));
                            x++;
                        }
                        e.marketAverage = marketAverageList;
                        e.MA = e.marketAverage.toString().replace("[","").replace("]","");
                        e.addEquity(e.tickerSymbol, e.name, e.shares, e.acquisitionPrice, "N/A", e.marketAverage, true, p);
                    }
                }
                else if(equity[0].equals("marketAverage")){
                    Equity c = new Equity("","",0,0,"",null);
                    c.MA = equity[1];
                    c.shares = Integer.parseInt(equity[2]);
                    c.acquisitionPrice = Double.parseDouble(equity[3]);
                    p.marketAverages.add(c);
                }
                else {
                    if (equity.length == 4) {
                        ArrayList<String> marketAverageList = new ArrayList<String>();
                        if (equity[0].contains("^")){
                            e.tickerSymbol = equity[0].substring(0, equity[0].lastIndexOf("^")).replaceAll("[^=,\\da-zA-Z]", "");
                        }else{
                            e.tickerSymbol = equity[0].replaceAll("^\\s+|\\s+$", "").replaceAll("[^=,\\da-zA-Z]", "");
                        }
                        e.name = equity[1].replaceAll("^\"|\"$", "");
                        e.acquisitionPrice = Double.parseDouble(equity[2].replace("\"", "").replaceAll("^\"|\"$", ""));
                        marketAverageList.add(equity[3].replaceAll("^\"|\"$", ""));
                        e.marketAverage = marketAverageList;
                        e.MA = e.marketAverage.toString().replace("[","").replace("]","");
                        Date date = null;
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            date = format.parse("00/00/0000");
                        } catch (ParseException ee) {
                            ee.printStackTrace();
                        }
                        addEquity(e.tickerSymbol, e.name, 0, e.acquisitionPrice, "N/A", e.marketAverage, true, p);
                    } else if (equity.length > 4) {
                        ArrayList<String> marketAverageList = new ArrayList<String>();
                        if (equity[0].contains("^")){
                            e.tickerSymbol = equity[0].substring(0, equity[0].lastIndexOf("^")).replaceAll("[^=,\\da-zA-Z]", "");
                        }else{
                            e.tickerSymbol = equity[0].replaceAll("^\\s+|\\s+$", "").replaceAll("[^=,\\da-zA-Z]", "");
                        }
                        e.name = equity[1].replaceAll("^\"|\"$", "");
                        e.acquisitionPrice = Double.parseDouble(equity[2].replace("\"", "").replaceAll("^\"|\"$", ""));
                        int x = 3;
                        while (x < equity.length && !equity[x].equals("")) {
                            marketAverageList.add(equity[x].replaceAll("^\"|\"$", ""));
                            x++;
                        }
                        e.marketAverage = marketAverageList;
                        e.MA = e.marketAverage.toString().replace("[","").replace("]","");
                        addEquity(e.tickerSymbol, e.name, e.shares, e.acquisitionPrice, "N/A", e.marketAverage, true, p);
                    }
                }
            }

        } catch (FileNotFoundException ee) {
            ee.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }

        return e;
    }

    /**
     * Export equities.
     */
    public void exportEQ(String eFileName, Portfolio p) {
        try{
            FileWriter writer = new FileWriter(eFileName);

            for (Equity equity : p.equityList1){
                writer.append("equity" + ',');
                writer.append(equity.shares + "");
                writer.append(',');
                writer.append(equity.tickerSymbol);
                writer.append(',');
                writer.append(equity.getTotal() + "");
                writer.append(',');
                writer.append(equity.name);
                writer.append(",");
                equity.marketAverage.forEach((SI) -> {
                    try {
                        writer.append(SI.toString());
                        writer.append(",");
                    }
                    catch (IOException IO){
                        IO.printStackTrace();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                });

                writer.append('\n');
            }
            for(Equity ma : p.marketAverages){
                writer.append("marketAverage");
                writer.append(',');
                writer.append(ma.MA.replace("[","").replace("]",""));
                writer.append(',');
                writer.append(Integer.toString(ma.shares));
                writer.append(',');
                writer.append(Double.toString(ma.acquisitionPrice));
                writer.append("\n");
            }

            writer.flush();
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Add an equity
     */
    public void addEquity(String tickerSymbol, String name, int numShares, double salePrice,
                          String saleDate, ArrayList marketAverage, Boolean imported, Portfolio p){
        Boolean exists = false;
        for(Equity equity : p.equityList1){
            if(!tickerSymbol.equals("")) {
                if (equity.tickerSymbol.equals(tickerSymbol)) {
                    exists = true;
                }
            } else{
                if(equity.marketAverage.get(0).equals(marketAverage.get(0))){
                    exists = true;
                }
            }
        }
        if(!exists) {

            Equity e = new Equity("", "", 0, 0, null, null);

            e.tickerSymbol = tickerSymbol;
            e.name = name;
            e.shares = numShares;
            e.acquisitionPrice = salePrice;
            e.acquisitionDate = saleDate;
            e.marketAverage = marketAverage;
            e.MA = marketAverage.toString().replace("[","").replace("]","");

            if (imported && numShares > 0) {
                p.equityList2.add(e);
                p.equityList1.add(e);
            }else if(imported && numShares == 0){
                p.equityList2.add(e);
            }
            else {
                p.equityList1.add(e);
            }

            for(String avg : e.MA.split(",")) {
                ArrayList<String> avgArray = new ArrayList<>();
                avg = avg.trim();
                avgArray.add(avg);
                Equity marketAvg = new Equity("", "", 0, salePrice, null, avgArray);
                boolean exist = true;
                for(Equity equity : p.marketAverages){
                    if(equity.MA.equals(avg)){
                        exist = false;
                    }
                }
                if(exist == false){
                    p.marketAverages.add(marketAvg);
                } else{
                    double price = 0;
                    int count = 0;
                    ArrayList<Integer> shares = new ArrayList<>();
                    for(Equity q : p.equityList2){
                        if(q.marketAverage.contains(avg)){
                            price += q.acquisitionPrice;
                            count += 1;
                            shares.add(q.shares);
                        }
                    }
                    for(Equity q : p.equityList1){
                        if(q.marketAverage.contains(avg)){
                            shares.add(q.shares);
                        }
                    }
                    p.marketAverages.remove(marketAvg);
                    int totalshares = 0;
                    for(int s : shares){
                        totalshares += s;
                    }
                    marketAvg.shares = totalshares;
                    if(!avg.equals("DOW")) {
                        marketAvg.acquisitionPrice = Math.round((price / count) * 100.0) / 100.0;
                    } else{
                        marketAvg.acquisitionPrice = Math.round(((price / count) / .14602128057775) * 100.0) / 100.0;
                    }
                    p.marketAverages.add(marketAvg);
                }

            }
            if(imported == false) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                HistoryInvoker test = new HistoryInvoker();
                test.setLog(new LogEquityHistory(), p, new History("Add", "EQUITY", e, dateFormat.format(date), 0, numShares));
                test.logAction();
            }

        }
        else{
            Equity e = null;
            double amount = 0;
            for(Equity equity : p.equityList1){
                if(!tickerSymbol.equals("")) {
                    if (equity.tickerSymbol.equals(tickerSymbol)) {
                        equity.shares += numShares;
                        e = equity;
                        amount = numShares * equity.getAcquisitionPrice();
                    }
                } else{
                    if(equity.marketAverage.get(0).equals(marketAverage.get(0))){
                        equity.shares += numShares;
                        e = equity;
                        amount = numShares * equity.getAcquisitionPrice();
                    }
                }
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            HistoryInvoker test = new HistoryInvoker();
            test.setLog(new LogEquityHistory(), p, new History("Add Shares", "EQUITY", e,dateFormat.format(date),amount,numShares));
            test.logAction();
        }
    }

    /**
     * Remove Equity based on symbol given.
     */
    public void removeEquity(String symbol, String cashAcct, Portfolio p){
        ArrayList<Equity> copyList = new ArrayList<>(p.equityList1);

        for(Equity equity : copyList) {
            double amount = 0;
            double shares = 0;
            if (equity.tickerSymbol.equals(symbol)){
                if(!cashAcct.equals("None")) {
                    CashAccount c = new CashAccount("","",0,"",p);
                    amount = -equity.getTotal();
                    shares = -equity.shares;
                    c.updateBalance(cashAcct, equity.getTotal(), true,p);
                }
                p.equityList1.remove(equity);

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                HistoryInvoker test = new HistoryInvoker();
                test.setLog(new LogEquityHistory(), p, new History("Remove", "EQUITY", equity,dateFormat.format(date),amount,shares));
                test.logAction();
            }
        }
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Equity){
            if(this.tickerSymbol.equals(((Equity) obj).tickerSymbol)){
                if(this.name.equals(((Equity) obj).name)){
                    if(this.marketAverage.equals(((Equity) obj).marketAverage)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}