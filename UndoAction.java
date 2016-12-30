package trunk.Model;

import javafx.scene.Scene;
import javafx.stage.Stage;
import trunk.View.CAView;
import trunk.View.EquitiesView;
import trunk.View.FPTS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by sadaf345 on 4/5/2016.
 */
public class UndoAction implements handleCommand{
    public String actionType;
    public String objType;
    public String file;
    public CashAccount obj;
    public CashAccount transfer;
    public double transferAmount;

    public Equity item;
    public Portfolio p;
    public static ArrayDeque<UndoAction> undo = new ArrayDeque<>();
    public static ArrayDeque<UndoAction> redo = new ArrayDeque<>();


    public UndoAction(String action, String objectType, CashAccount c1, CashAccount c2, double num,Equity eq, String file) {
        this.actionType = action;
        this.obj = c1;
        this.objType = objectType;
        this.transfer = c2;
        this.transferAmount = num;
        this.item = eq;
        this.file = file;
    }


    public UndoAction(Portfolio p) {
        this.p = p;
    }

    @Override
    public void handleCommand(final Stage primaryStage, FPTS fpts, EquitiesView equitiesView, CAView caView) {
        Iterator undoIterator = createUndoIterator();

        for( Iterator iterate = undoIterator; iterate.hasNext();) {
            UndoAction iter = (UndoAction) iterate.next();
            if (iter.actionType.equals("Add") && iter.objType.equals("CashAccount") ) {
                UndoAddCashAcc(iter.obj, this.p);
                redo.addFirst(iter);
                undo.remove(iter);
                Scene scene = caView.CashAccounts(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }
            else if (iter.actionType.equals("Remove") && iter.objType.equals("CashAccount") ) {
                UndoRemoveCashAcc(iter.obj, this.p);
                redo.addFirst(iter);
                undo.remove(iter);
                Scene scene = caView.CashAccounts(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }

            else if(iter.actionType.equals("Transfer") && iter.objType.equals("CashAccount")){
                UndoTransfer(iter.obj, iter.transfer, iter.transferAmount, this.p);
                redo.addFirst(iter);
                undo.remove(iter);
                Scene scene = caView.CashAccounts(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }

            else if (iter.actionType.equals("Add") && iter.objType.equals("Equity") ) {
                UndoAddEq(iter.item, this.p);
                redo.addFirst(iter);
                undo.remove(iter);
                Scene scene = equitiesView.ViewEquities(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }
            else if (iter.actionType.equals("Remove") && iter.objType.equals("Equity") ) {
                UndoRemoveEq(iter.item, this.p);
                redo.addFirst(iter);
                undo.remove(iter);
                Scene scene = equitiesView.ViewEquities(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }

            else if (iter.actionType.equals("Import") && iter.objType.equals("CashAccount") ) {
                UndoImportCA(iter.file, this.p);
                redo.addFirst(iter);
                undo.remove(iter);
                Scene scene = caView.CashAccounts(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }
            else if (iter.actionType.equals("Import") && iter.objType.equals("Equity") ) {
                UndoImportEQ(iter.file, this.p);
                redo.addFirst(iter);
                undo.remove(iter);
                Scene scene = equitiesView.ViewEquities(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }
        }
    }

    public Iterator createUndoIterator() {
        Iterator undoIter =  undo.iterator();
        return undoIter;
    }
    public Iterator createRedoIterator() {
        Iterator undoIter =  redo.iterator();
        return undoIter;
    }

    public void redo(final Stage primaryStage, FPTS fpts, EquitiesView equitiesView, CAView caView){
        Iterator undoIterator = createRedoIterator();
        for( Iterator iterate = undoIterator; iterate.hasNext();) {
            UndoAction iter = (UndoAction) iterate.next();
            if (iter.actionType.equals("Add") && iter.objType.equals("CashAccount") ) {
                UndoRemoveCashAcc(iter.obj, this.p);
                addToQueue(iter);
                redo.remove(iter);
                Scene scene = caView.CashAccounts(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }
            else if (iter.actionType.equals("Remove") && iter.objType.equals("CashAccount") ) {
                UndoAddCashAcc(iter.obj, this.p);
                addToQueue(iter);
                redo.remove(iter);
                Scene scene = caView.CashAccounts(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }
            else if(iter.actionType.equals("Transfer") && iter.objType.equals("CashAccount")){
                UndoTransfer(iter.transfer,iter.obj, iter.transferAmount, this.p);
                addToQueue(iter);
                redo.remove(iter);
                Scene scene = caView.CashAccounts(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }

            else if (iter.actionType.equals("Add") && iter.objType.equals("Equity") ) {
                UndoRemoveEq(iter.item, this.p);
                addToQueue(iter);
                redo.remove(iter);
                Scene scene = equitiesView.ViewEquities(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }
            else if (iter.actionType.equals("Remove") && iter.objType.equals("Equity") ) {
                UndoAddEq(iter.item, this.p);
                addToQueue(iter);
                redo.remove(iter);
                Scene scene = equitiesView.ViewEquities(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }

            else if (iter.actionType.equals("Import") && iter.objType.equals("CashAccount") ) {
                CashAccount c = new CashAccount("","",0,"",p);
                c.importCA(iter.file,p);

                addToQueue(iter);
                redo.remove(iter);
                Scene scene = caView.CashAccounts(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }
            else if (iter.actionType.equals("Import") && iter.objType.equals("Equity") ) {
                Equity c = new Equity("","",0,0,null,null);

                c.importEQ(iter.file,p);

                addToQueue(iter);
                redo.remove(iter);
                Scene scene = equitiesView.ViewEquities(primaryStage,p, fpts);
                fpts.stage.setScene(scene);
                break;
            }
        }
    }
    public void UndoAddCashAcc(CashAccount obj, Portfolio p) {
        obj.deleteCashAccount(obj.getName(), p); // deletes
    }

    public void UndoRemoveCashAcc(CashAccount obj, Portfolio p) { // need Redo class
        obj.addCashAccount(obj.name,obj.balance,obj.getDate(),p);
    }

    public void UndoAddEq(Equity obj, Portfolio p) {
        obj.removeEquity(obj.tickerSymbol,"None",p);
    }



    public void UndoRemoveEq(Equity obj, Portfolio p) {
        obj.addEquity(obj.tickerSymbol,obj.name,obj.shares,obj.acquisitionPrice,obj.acquisitionDate,obj.marketAverage,false,p);
    }

    public void UndoTransfer(CashAccount c1, CashAccount c2, double num, Portfolio p) {
        CashAccount c = new CashAccount("","",0,"",null);
        c.transfer(c2,c1,num,p);
    }


    public void UndoImportCA(String csv, Portfolio p) {
        BufferedReader br = null;
        String line = "";
        String[] ca = null;
        ArrayList<CashAccount> toRemove = new ArrayList<>();


        try {

            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null) {
                ca = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);;

                for(CashAccount c : p.cashAccList){
                    if(c.name.equals(ca[2])){
                        toRemove.add(c);
                    }
                }
                for(CashAccount c : toRemove){
                    p.cashAccList.remove(c);
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
    }

    public void UndoImportEQ(String csv, Portfolio p) {
        BufferedReader br = null;
        String line = "";
        String[] equity = null;
        ArrayList<Equity> toRemove = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null) {
                equity = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);;

                for(Equity c : p.equityList2){
                    if(c.tickerSymbol.equals(equity[0].replaceAll("^\"|\"$", ""))){
                        toRemove.add(c);
                    }
                }
                for(Equity c : toRemove){
                    p.equityList2.remove(c);
                    //Resetting Market Averages
                    for(String m : c.marketAverage){
                        ArrayList<String> avgArray = new ArrayList<>();
                        avgArray.add(m);
                        Equity marketAvg = new Equity("", "", 0, 0, null, avgArray);
                        double price = 0;
                        int count = 0;
                        for(Equity q : p.equityList2){
                            if(q.marketAverage.contains(m)){
                                price += q.acquisitionPrice;
                                count += 1;
                            }
                        }
                        p.equityList2.remove(marketAvg);
                        if(!m.equals("DOW")) {
                            marketAvg.acquisitionPrice = Math.round((price / count) * 100.0) / 100.0;
                        } else{
                            marketAvg.acquisitionPrice = Math.round(((price / count) / .14602128057775) * 100.0) / 100.0;
                        }
                        p.equityList2.add(marketAvg);
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
            ArrayList<Equity> equityListCopy = new ArrayList<>(p.equityList2);
            HashSet<String> existingAvgs = new HashSet<>();
            for(Equity e : equityListCopy){
                if(!e.tickerSymbol.equals("")){
                    existingAvgs.addAll(e.marketAverage);
                }
            }
            for(Equity e : equityListCopy){
                if(e.tickerSymbol.equals("") && !existingAvgs.contains(e.marketAverage.get(0))){
                    p.equityList2.remove(e);
                }
            }
        }
    }

    public void allclear(){
        undo.clear();
        redo.clear();
    }

    public void addToQueue(UndoAction inst) {
        undo.addFirst(inst);
    }

}
