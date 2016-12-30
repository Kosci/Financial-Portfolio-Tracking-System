package trunk.Model;


import trunk.Model.*;

import java.io.*;


/**
 * A class to represent an instance of an event or transaction
 * that should be logged to history.
 */

public class History implements Serializable{
    String type;
    String action;


    Equity equity;
    String equityName;

    CashAccount cashAccount;
    String cashAccName;
    Double transferAmount1;
    Double transferAmount2;

    Double shares;

    String date1;
    String date2;
    /**
     * Constructor for a History Object for a event related to an Equity.
     * @param action : A String representing the action. (e.g. add, remove, etc.)
     * @param type : A String representing which history type referenced. (e.g. "Equity")
     * @param equity : The Equity involved in the action.
     */
    public History(String action, String type, Equity equity, String date, double transferAmount, double shares){
        this.type = type;
        this.action = action;
        this.equityName = equity.name;
        this.date1 = date;
        this.transferAmount1 = transferAmount;
        this.shares = shares;
    }

    /**
     * Constructor for a History Object for an event related to Cash Accounts.
     * @param action : A String representing the action. (e.g. add, remove, etc.)
     * @param type : A String representing which history type referenced. (e.g. "Cash Account")
     * @param cashAccount : The Cash Account involved in the action.
     */
    public History(String action, String type, CashAccount cashAccount, String date, double transferAmount){
        this.type = type;
        this.action = action;
        this.cashAccName = cashAccount.name;
        this.date2 = date;
        this.transferAmount2 = transferAmount;
    }

    /**
     * A getter for the history type referenced.
     * @return : A string representing this type.
     */
    public String getType(){
        return this.type;
    }

    /**
     * A getter for the action associated with the event that occurred.
     * @return : A string representing this action.
     */
    public String getAction(){
        return this.action;
    }

    /**
     * A getter for the name of the Equity involved in an event.
     * @return : A string representing the name.
     */
    public String getEquityName(){
        return this.equityName;
    }

    /**
     * A getter for the name of the Cash Account involved in an event.
     * @return : A string representing the name.
     */
    public String getCashAccName(){
        return this.cashAccName;
    }

    public Double getTransferAmount1(){return this.transferAmount1;}

    public Double getTransferAmount2(){return this.transferAmount2;}

    public String getDate1(){return this.date1;}

    public String getDate2(){return this.date2;}

    public Double getShares(){return this.shares;}

    /*
     * Import cash account history
     */
    public void importCAHistory(String csv, Portfolio p) {
        BufferedReader br = null;
        String line = "";
        String[] ca = null;
        CashAccount c = new CashAccount("","",0,"",null);
        //History caH = new History("N/A", "N/A", c);


        try {

            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null) {
                History caH = new History("N/A", "N/A", c,"",0);
                ca = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);;

                caH.type = ca[2];
                caH.action = ca[1];
                caH.cashAccName = ca[3];

                p.cashAccountHistory.add(caH);
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

    }

    /*
     * Import equity history
     */
    public void importEQHistory(String csv, Portfolio p) {
        BufferedReader br = null;
        String line = "";
        String[] ca = null;
        Equity c = new Equity("","",0,0,"",null);

        try {

            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null) {
                History caH = new History("N/A", "N/A",c,"",0,0);
                ca = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);;

                caH.type = ca[2];
                caH.action = ca[1];
                caH.equityName = ca[3];

                p.equityHistory.add(caH);
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

    }

    /**
     * Export Cash Account History
     * Export example : 'Add', 'CashAccount', 'NameOfAccount'
     */
    public void exportCAHistory(String eFileName, Portfolio p){
        try{
            FileWriter writer = new FileWriter(eFileName);

            for (History h : p.cashAccountHistory){
                writer.append("transaction");
                writer.append(",");
                writer.append(h.action);
                writer.append(',');
                writer.append(h.type);
                writer.append(',');
                writer.append(h.cashAccName);
                writer.append("\n");
            }

            writer.flush();
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Export equity History
     * Export example: 'Remove', 'Equity', 'NameOfEquity'
     */
    public void exportEquityHistory(String eFileName, Portfolio p){
        try{
            FileWriter writer = new FileWriter(eFileName);

            for (History h : p.equityHistory){
                writer.append("transaction");
                writer.append(",");
                writer.append(h.action);
                writer.append(',');
                writer.append(h.type);
                writer.append(',');
                writer.append(h.equityName);
                writer.append("\n");
            }

            writer.flush();
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
