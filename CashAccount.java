package trunk.Model;

/**
 * A class to represent an instance of a Cash Account.
 * A Cash Account is responsible for maintaining funds, interacting
 * with equities, and interacting with a Portfolio.
 */

import trunk.Control.HistoryInvoker;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CashAccount implements Serializable {
    public String portfolioID;
    public String name;
    public double balance;
    public String dateCreated;
    Portfolio portfolio;

    /**
     * Constructor for a CashAccount Object.
     * @param portfolioID : A string representing the portfolioID this account belongs to.
     * @param name : A string representing the name of this account.
     * @param dateCreated : A String representing the date the account was created.
     * @param portfolio : The Portfolio object this account belongs to.
     */
    public CashAccount(String portfolioID, String name,
                       double initAmount, String dateCreated, trunk.Model.Portfolio portfolio){

        this.portfolioID = portfolioID;
        this.name = name;
        this.dateCreated = dateCreated;
        DecimalFormat df = new DecimalFormat("#.00");
        this.balance = Double.parseDouble(df.format(initAmount));
        this.portfolio = portfolio;
    }


    /**
     * A getter for the portfolioID associated with this account.
     * @return : A string representing the portfolioID.
     */
    public String getPortfolioID(){
        return this.portfolioID;
    }

    /**
     * A getter for the name given to this account.
     * @return : A string representing the account's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * A getter for the date associated with this account's creation.
     * @return : A string representing the creation date.
     */
    public String getDate() {
        return this.dateCreated;
    }

    /**
     * A getter for balance associated with this account
     * @return : The double amount representing current cash amount in this account
     */
    public double getBalance(){return this.balance;}

    /**
     * Transfer funds from one account to another.
     * @param amount : An int representing the amount to transfer.
     * @param receiver : A CashAccount representing the account
     *                 receiving the amount.
     */
    public void transferFunds(int amount, CashAccount receiver){
        if (amount > this.balance){
            return;
        }else{
            receiver.balance += amount;
            this.balance -= amount;

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            HistoryInvoker test = new HistoryInvoker();
            test.setLog(new LogCAHistory(), this.portfolio, new History("Update", "CASHACCOUNT", this, dateFormat.format(date),-amount));
            test.logAction();
        }
    }



    /**
     * Add a cash account
     */
    public void addCashAccount(String name, double initAmount, String date, Portfolio p){
        boolean same = false;
        for(CashAccount c : p.cashAccList){
            if(c.getName().equals(name)){
                same = true;
                break;
            }
        }
        if(!same) {
            CashAccount newAcc = new CashAccount(p.loginID, name, initAmount, date, p);
            p.cashAccList.add(newAcc);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date2 = new Date();

            HistoryInvoker test = new HistoryInvoker();
            test.setLog(new LogCAHistory(), p, new History("Add", "CASHACCOUNT", newAcc, dateFormat.format(date2),0));
            test.logAction();
        }
        else{
            for(int i = 0; i < p.cashAccList.size(); i++){
                if(p.cashAccList.get(i).getName().equals(name)){
                    p.cashAccList.get(i).balance = initAmount;
                    p.cashAccList.get(i).dateCreated = date;
                    break;
                }
            }
        }
    }

    /*
     * Import cash accounts
     */
    public ArrayList<CashAccount> importCA(String csv, Portfolio p) {
        BufferedReader br = null;
        String line = "";
        String[] ca = null;

        ArrayList<CashAccount> existingAccts = new ArrayList<>();

        try {

            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null) {
                ca = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                CashAccount c = new CashAccount("N/A", "N/A", 0.0, "", null);
                c.name = ca[2];
                c.balance = Double.parseDouble(ca[1].replace("\"", ""));
                c.dateCreated = ca[3];

                if(!p.cashAccList.contains(c)) {
                    addCashAccount(c.name, c.balance, c.dateCreated, p);
                } else{
                    existingAccts.add(c);
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
        return existingAccts;
    }

    /**
     * Export Cash Account
     */
    public void exportCA(String eFileName, Portfolio p) {
        try{
            FileWriter writer = new FileWriter(eFileName);

            for (CashAccount ca : p.cashAccList){
                writer.append("cashacc");
                writer.append(',');
                writer.append(ca.balance + "");
                writer.append(',');
                writer.append(ca.getName());
                writer.append(',');
                writer.append(ca.dateCreated);
                writer.append('\n');
            }

            writer.flush();
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Remove cash account based on name given.
     */
    public void deleteCashAccount(String name, Portfolio p){
        ArrayList<CashAccount> copyList = new ArrayList<>(p.cashAccList);

        copyList.forEach((account) -> {
            if (account.name.equals(name)){
                p.cashAccList.remove(account);

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                HistoryInvoker test = new HistoryInvoker();
                test.setLog(new LogCAHistory(), p, new History("Remove", "CASHACCOUNT",account,dateFormat.format(date),0));
                test.logAction();
            }
        });
    }

    /**
     * Update funds of account depending on transaction type
     */
    public void updateBalance(String name, double amount, Boolean deposit, Portfolio p){
        p.cashAccList.forEach((account) -> {
            if (account.name.equals(name)){
                if(deposit) {
                    account.balance += amount;
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    HistoryInvoker test = new HistoryInvoker();
                    test.setLog(new LogCAHistory(), p, new History("Update", "CASHACCOUNT", account,dateFormat.format(date),amount));
                    test.logAction();
                }
                else {
                    account.balance -= amount;
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    HistoryInvoker test = new HistoryInvoker();
                    test.setLog(new LogCAHistory(), p, new History("Update", "CASHACCOUNT", account,dateFormat.format(date),-amount));
                    test.logAction();
                }
            }
        });
    }

    /*
    * Transfer money between two cash accounts
    */
    public void transfer(CashAccount c1, CashAccount c2, Double num, Portfolio p){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        for(CashAccount c : p.cashAccList){
            if(c.name.equals(c2.name)){
                c.balance -= num;


                HistoryInvoker test = new HistoryInvoker();
                test.setLog(new LogCAHistory(), p, new History("Update", "CASHACCOUNT", c,dateFormat.format(date),-num));
                test.logAction();
                break;
            }
        }
        for(CashAccount c : p.cashAccList){
            if(c.name.equals(c1.name)){
                c.balance += num;

                HistoryInvoker test = new HistoryInvoker();
                test.setLog(new LogCAHistory(), p, new History("Update", "CASHACCOUNT", c,dateFormat.format(date),num));
                test.logAction();
                break;
            }
        }
    }



    @Override
    public boolean equals(Object obj){
        if(obj instanceof CashAccount){
            if(this.name.equals(((CashAccount) obj).name)){
                return true;
            }
        }
        return false;
    }
}
