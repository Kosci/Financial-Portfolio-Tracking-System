package trunk.Model;

//import trunk.Model.*;



import trunk.Control.UpdateEQ;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Class representation of the Financial Portfolio
 */
public class Portfolio implements Serializable{
    public String loginID; // Login ID of portfolio user
    public String password; // Password associated with loginID of user
    public ArrayList<Equity> equityList1 = new ArrayList<>(); //List of owned equities
    public ArrayList<Equity> equityList2 = new ArrayList<>(); //List of available equities
    public ArrayList<Equity> marketAverages = new ArrayList<>(); //List of market averages
    public ArrayList<Watchlist> watchlists = new ArrayList<>(); //List of watchlist items

    public ArrayList<CashAccount> cashAccList = new ArrayList<>();
    public ArrayList<History> cashAccountHistory = new ArrayList<>();
    public ArrayList<History> equityHistory = new ArrayList<>();


    public void initialEquities(){
        BufferedReader br = null;
        String line = "";
        String[] equity = null;
        ArrayList<Equity> equities = new ArrayList<>();
        try {

            br = new BufferedReader(new FileReader("svn/equities.csv"));
            while ((line = br.readLine()) != null) {
                equity = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (equity.length == 4) {
                    Equity e = new Equity("N/A","N/A",0,0,null,null);
                    ArrayList<String> marketAverageList = new ArrayList<String>();
                    if (equity[0].contains("^")){
                        e.tickerSymbol = equity[0].substring(0, equity[0].lastIndexOf("^")).replaceAll("[^=,\\da-zA-Z]", "");
                    }else{
                        e.tickerSymbol = equity[0].replaceAll("[^=,\\da-zA-Z]", "");
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
                    e.addEquity(e.tickerSymbol, e.name, 0, e.acquisitionPrice, "N/A", e.marketAverage, true, this);
                } else if (equity.length > 4) {
                    Equity e = new Equity("N/A","N/A",0,0,null,null);
                    ArrayList<String> marketAverageList = new ArrayList<String>();
                    if (equity[0].contains("^")){
                        e.tickerSymbol = equity[0].substring(0, equity[0].lastIndexOf("^")).replaceAll("[^=,\\da-zA-Z]", "");
                    }else{
                        e.tickerSymbol = equity[0].replaceAll("[^=,\\da-zA-Z]", "");;
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
                    e.addEquity(e.tickerSymbol, e.name, 0, e.acquisitionPrice, "N/A", e.marketAverage, true, this);
                }
            }

        }catch (FileNotFoundException ee) {
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

    }
    public Portfolio(String loginID, String password) {
        this.loginID = loginID;
        this.password = password;
        this.equityList1 = new ArrayList<>();
        this.equityList2 = new ArrayList<>();
        initialEquities();
        try{
            UpdateEQ updateEQ = new UpdateEQ();
            updateEQ.updateEquities(this, this.equityList2);
        }catch(Exception ex){
            ex.getMessage();
        }
        this.cashAccList = new ArrayList<>();
    }
    public Portfolio(ArrayList<Equity> ownedE, ArrayList<CashAccount> ownedCA){
        this.equityList1 = ownedE;
        this.cashAccList = ownedCA;
        //this.cashAccountHistory = new ArrayList<>();
        //this.equityHistory = new ArrayList<>();
    }

    public void importP(String eFileName, Portfolio p) {
        BufferedReader br = null;
        String line = "";
        String[] string = null;
        Equity e = new Equity("N/A", "N/A", 0, 0, null, null);


        try {
            br = new BufferedReader(new FileReader(eFileName));
            while ((line = br.readLine()) != null) {
                string = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (string[0].equals("equity")) {
                    if (string.length == 6) {
                        ArrayList<String> marketAverageList = new ArrayList<String>();
                        e.tickerSymbol = string[2].replaceAll("^\"|\"$", "");
                        e.shares = Integer.parseInt(string[1].replaceAll("^\"|\"$", ""));
                        e.acquisitionPrice = (Double.parseDouble(string[3].replace("\"", "").replaceAll("^\"|\"$", ""))) / e.shares;
                        e.name = string[4];
                        marketAverageList.add(string[5].replaceAll("^\"|\"$", ""));
                        e.marketAverage = marketAverageList;
                        e.MA = e.marketAverage.toString().replace("[", "").replace("]", "");
                        Date date = null;
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            date = format.parse("00/00/0000");
                        } catch (ParseException ee) {
                            ee.printStackTrace();
                        }
                        e.addEquity(e.tickerSymbol, e.name, e.shares, e.acquisitionPrice, "N/A", e.marketAverage, false, p);
                    } else if (string.length > 6) {

                        ArrayList<String> marketAverageList = new ArrayList<String>();
                        e.tickerSymbol = string[2].replaceAll("^\"|\"$", "");
                        e.shares = Integer.parseInt(string[1].replaceAll("^\"|\"$", ""));
                        e.acquisitionPrice = (Double.parseDouble(string[3].replace("\"", "").replaceAll("^\"|\"$", ""))) / e.shares;
                        if (string[5].contains("Inc")){
                            e.name = string[4] += ", Inc.";
                        }else{
                            e.name = string[4];
                        }
                        int x = 5;
                        while (x < string.length && !string[x].equals("")) {
                            if (!string[x].contains("Inc")) {
                                marketAverageList.add(string[x].replaceAll("^\"|\"$", ""));
                                x++;
                            }else{
                                x++;
                            }
                        }
                        e.marketAverage = marketAverageList;
                        e.MA = e.marketAverage.toString().replace("[", "").replace("]", "");
                        e.addEquity(e.tickerSymbol, e.name, e.shares, e.acquisitionPrice, "N/A", e.marketAverage, false, p);
                    }
                } else if (string[0].equals("cashacc")) {
                    CashAccount c = new CashAccount("N/A", "N/A", 0.0, "", null);
                    c.name = string[2];
                    c.balance = Double.parseDouble(string[1].replace("\"", ""));
                    //c.dateCreated = string[3];

                    if (!p.cashAccList.contains(c)) {
                        c.addCashAccount(c.name, c.balance, c.dateCreated, p);
                    }
                } else if (string[0].equals("transaction")) {
                    if (string[1].equals("EQUITY") || string[1].equals("equity")) {
                        Equity c = new Equity("", "", 0, 0, "", null);
                        History caH = new History("N/A", "N/A", c, "", 0, 0);

                        if (string.length == 5) {
                            caH.type = string[1];
                            caH.equityName = string[2];
                            caH.transferAmount1 = Double.parseDouble(string[3]);
                            caH.date1 = string[4];
                        }  else{
                            caH.type = string[1];
                            caH.equityName = string[2] += ", Inc.";
                            caH.transferAmount1 = Double.parseDouble(string[4]);
                            caH.date1 = string[5];
                        }

                        p.equityHistory.add(caH);
                    } else if (string[1].equals("CASHACCOUNT") || string[1].equals("cash account")) {
                        CashAccount c = new CashAccount("", "", 0, "", null);
                        History caH = new History("N/A", "N/A", c, "", 0);

                        caH.type = string[1];
                        caH.cashAccName = string[2];
                        caH.transferAmount2 = Double.parseDouble(string[3]);
                        caH.date2 = string[4];

                        p.cashAccountHistory.add(caH);
                    }
                } else if (string[0].equals("watchlistItem")) {
                    Watchlist w = new Watchlist("", "", "", 0, "", "", "");

                    w.tickerSymbol = string[1];
                    w.lowtrigger = string[2];
                    w.hightrigger = string[3];

                    p.watchlists.add(w);
                } else if (string[0].equals("marketAverage")) {

                    String tmp = string[1];
                    List<Equity> curMarAvgs = this.marketAverages.stream().filter(u -> u.name.equals(tmp)).collect(Collectors.toList());
                    
                    if (!(curMarAvgs.get(0).name.equals(string[1]))){
                        Equity c = new Equity("", "", 0, 0, "", null);
                        c.MA = string[1];
                        c.shares = Integer.parseInt(string[2]);
                        c.acquisitionPrice = Double.parseDouble(string[3]);
                        p.marketAverages.add(c);
                    }else{
                        curMarAvgs.get(0).shares += Integer.parseInt(string[2]);
                    }


                    /**
                    Equity c = new Equity("", "", 0, 0, "", null);
                    c.MA = string[1];
                    c.shares = Integer.parseInt(string[2]);
                    c.acquisitionPrice = Double.parseDouble(string[3]);
                    p.marketAverages.add(c); */
                }

            }
            }catch(FileNotFoundException ee){
                ee.printStackTrace();
            }catch(IOException ee){
                ee.printStackTrace();
            }finally{
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }
                }
            }
        }



    public void exportP(String eFileName, Portfolio p) {
        try{
            FileWriter writer = new FileWriter(eFileName);

            for (Equity equity : p.equityList1){
                writer.append("equity");
                writer.append(',');
                writer.append(equity.shares + "");
                writer.append(',');
                writer.append(equity.tickerSymbol);
                writer.append(',');
                writer.append(equity.getTotal() + "");
                writer.append(',');
                writer.append(equity.name);
                equity.marketAverage.forEach((SI) -> {
                    try {
                        writer.append(",");
                        writer.append(SI.toString());
                    }
                    catch (IOException IO){
                        IO.printStackTrace();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                });

                writer.append('\n');
            }

            for (CashAccount ca : p.cashAccList){
                writer.append("cashacc");
                writer.append(',');
                writer.append(ca.balance + "");
                writer.append(',');
                writer.append(ca.getName());
                writer.append('\n');
            }

            for (History h : p.cashAccountHistory){
                writer.append("transaction");
                writer.append(',');
                writer.append(h.type);
                writer.append(',');
                writer.append(h.cashAccName);
                writer.append(',');
                writer.append(h.transferAmount2.toString());
                writer.append(',');
                writer.append(h.date2);
                writer.append("\n");
            }

            for (History h : p.equityHistory){
                writer.append("transaction");
                writer.append(',');
                writer.append(h.type);
                writer.append(',');
                writer.append(h.equityName);
                writer.append(',');
                writer.append(h.transferAmount1.toString());
                writer.append(',');
                writer.append(h.date1);
                writer.append("\n");
            }

            for (Watchlist w : p.watchlists){
                writer.append("watchlistItem");
                writer.append(',');
                writer.append(w.getTickerSymbol());
                writer.append(',');
                writer.append(w.lowtrigger);
                writer.append(',');
                writer.append(w.hightrigger);
                writer.append("\n");
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

    /*
     * Code for saving market averages
     */
    public void saveMAs(Portfolio p){
        ArrayList<Portfolio> port = null;
        try {
            FileInputStream fileIn = new FileInputStream("myfile");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            port = (ArrayList<Portfolio>) in.readObject();
            in.close();
            fileIn.close();
        } catch (ClassNotFoundException ee) {
            ee.printStackTrace();
        } catch (FileNotFoundException ee) {
            ee.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        }
        port.forEach(iter -> {
            iter.marketAverages = p.marketAverages;
        });

        try
        {
            FileOutputStream fileOut =
                    new FileOutputStream("myfile");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(port);
            out.close();
            fileOut.close();
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }
}
