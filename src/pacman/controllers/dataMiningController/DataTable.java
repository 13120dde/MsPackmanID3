package pacman.controllers.dataMiningController;

import java.util.ArrayList;
import java.util.LinkedList;

public class DataTable <T> implements Cloneable {
    //int, MOVE , DiscreteTag, bool
    protected ArrayList<ArrayList<T>> table;

    public DataTable(){
        table = new ArrayList<ArrayList<T>>();
        // loadTrainingData();

    }

    public static void main(String[] args) {
        DataTable data = new DataTable();
        data.test();



    }

    private void test() {

        loadExampleData();
        ArrayList a = getUniqueValsFromColumn(table.get(0));
        everyTupleInSameClass();
        majorityClassValue();
        LinkedList<T> attrList = getAttributeList();
        //T label = getClassLabel(13);
        ArrayList<T> col = getColumn(this, (T) ExampleValues.CREDIT_RATING);
        DataTable[] tables =  partitionSetOnAttributeValue((T) ExampleValues.INCOME,this);

    }


    protected void loadTrainingData() {
    }

    protected void loadExampleData() {
        ArrayList a1 = new ArrayList();
        a1.add(ExampleValues.AGE);
        a1.add(ExampleValues.YOUTH);
        a1.add(ExampleValues.YOUTH);
        a1.add(ExampleValues.MIDDLE_AGED);
        a1.add(ExampleValues.SENIOR);
        a1.add(ExampleValues.SENIOR);
        a1.add(ExampleValues.SENIOR);
        a1.add(ExampleValues.MIDDLE_AGED);
        a1.add(ExampleValues.YOUTH);
        a1.add(ExampleValues.YOUTH);
        a1.add(ExampleValues.SENIOR);
        a1.add(ExampleValues.YOUTH);
        a1.add(ExampleValues.MIDDLE_AGED);
        a1.add(ExampleValues.MIDDLE_AGED);
        a1.add(ExampleValues.SENIOR);

        ArrayList a2 = new ArrayList();
        a2.add(ExampleValues.INCOME);
        a2.add(ExampleValues.HIGH);
        a2.add(ExampleValues.HIGH);
        a2.add(ExampleValues.HIGH);
        a2.add(ExampleValues.MEDIUM);
        a2.add(ExampleValues.LOW);
        a2.add(ExampleValues.LOW);
        a2.add(ExampleValues.LOW);
        a2.add(ExampleValues.MEDIUM);
        a2.add(ExampleValues.LOW);
        a2.add(ExampleValues.MEDIUM);
        a2.add(ExampleValues.MEDIUM);
        a2.add(ExampleValues.MEDIUM);
        a2.add(ExampleValues.HIGH);
        a2.add(ExampleValues.MEDIUM);

        ArrayList a3 = new ArrayList();
        a3.add(ExampleValues.STUDENT);
        a3.add(ExampleValues.NO);
        a3.add(ExampleValues.NO);
        a3.add(ExampleValues.NO);
        a3.add(ExampleValues.NO);
        a3.add(ExampleValues.YES);
        a3.add(ExampleValues.YES);
        a3.add(ExampleValues.YES);
        a3.add(ExampleValues.NO);
        a3.add(ExampleValues.YES);
        a3.add(ExampleValues.YES);
        a3.add(ExampleValues.YES);
        a3.add(ExampleValues.NO);
        a3.add(ExampleValues.YES);
        a3.add(ExampleValues.NO);


        ArrayList a4 = new ArrayList();
        a4.add(ExampleValues.CREDIT_RATING);
        a4.add(ExampleValues.FAIR);
        a4.add(ExampleValues.EXCELLENT);
        a4.add(ExampleValues.FAIR);
        a4.add(ExampleValues.FAIR);
        a4.add(ExampleValues.FAIR);
        a4.add(ExampleValues.EXCELLENT);
        a4.add(ExampleValues.EXCELLENT);
        a4.add(ExampleValues.FAIR);
        a4.add(ExampleValues.FAIR);
        a4.add(ExampleValues.FAIR);
        a4.add(ExampleValues.EXCELLENT);
        a4.add(ExampleValues.EXCELLENT);
        a4.add(ExampleValues.FAIR);
        a4.add(ExampleValues.EXCELLENT);

        ArrayList a5 = new ArrayList();
        a5.add(ExampleValues.CLASS);
        a5.add(ExampleValues.NO);
        a5.add(ExampleValues.NO);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.NO);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.NO);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.NO);

        table.add(a1);
        table.add(a2);
        table.add(a3);
        table.add(a4);
        table.add(a5);




    }

    protected void loadExampleData2() {
        ArrayList a1 = new ArrayList();
        a1.add(ExampleValues.AGE);
        a1.add(ExampleValues.YOUTH);
        a1.add(ExampleValues.SENIOR);
        a1.add(ExampleValues.MIDDLE_AGED);

        ArrayList a2 = new ArrayList();
        a2.add(ExampleValues.INCOME);
        a2.add(ExampleValues.HIGH);
        a2.add(ExampleValues.MEDIUM);
        a2.add(ExampleValues.LOW);

        ArrayList a3 = new ArrayList();
        a3.add(ExampleValues.STUDENT);
        a3.add(ExampleValues.NO);
        a3.add(ExampleValues.YES);
        a3.add(ExampleValues.YES);


        ArrayList a4 = new ArrayList();
        a4.add(ExampleValues.CREDIT_RATING);
        a4.add(ExampleValues.EXCELLENT);
        a4.add(ExampleValues.FAIR);
        a4.add(ExampleValues.EXCELLENT);

        ArrayList a5 = new ArrayList();
        a5.add(ExampleValues.CLASS);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.YES);
        a5.add(ExampleValues.NO);

        table.add(a1);
        table.add(a2);
        table.add(a3);
        table.add(a4);
        table.add(a5);




    }

    protected DataTable<T> clone() throws CloneNotSupportedException {
        return (DataTable<T>) super.clone();
    }

    protected LinkedList<T> getAttributeList() {
        LinkedList<T> list = new LinkedList<>();
        for(ArrayList<T> a : table){
            list.add(a.get(0));
        }
        return list;
    }

    protected LinkedList<T> getAttributeListTest() {
        LinkedList<T> list = new LinkedList<>();
        list.addLast((T) ExampleValues.INCOME);
        list.addLast((T) ExampleValues.AGE);
        list.addLast((T) ExampleValues.CREDIT_RATING);
        list.addLast((T) ExampleValues.STUDENT);
        return list;
    }

    protected T getClassLabel(int i) {
        return (T) table.get(table.size()-1).get(i);
    }

    private ArrayList<T> getUniqueValsFromColumn(ArrayList<T> column){
        ArrayList<T> uniqueVals = new ArrayList();
        for(T value : column){
            if (!uniqueVals.contains(value)){
                uniqueVals.add((T) value);
            }
        }
        uniqueVals.remove(0);
        return uniqueVals;
    }

    /**Checks if all the tuples belong to the same class.
     *
     * @return : boolean
     */
    protected boolean everyTupleInSameClass() {

        boolean flag = true;
        if(table.isEmpty()){
            return true;
        }
        else if(getUniqueValsFromColumn(table.get(table.size()-1)).size()>1) {
            flag = false;
        }

        System.out.println("IN: everyTupleInSameClass()\n\t: "+flag);

        return flag;
    }

    /**
     *
     * @return majorityValue : T
     */
    protected T majorityClassValue() {

        ArrayList<T> classValues = table.get(table.size()-1);
        T majorityValue = null;
        ArrayList<T> uniqueVals = getUniqueValsFromColumn(table.get(table.size()-1));
        int[] indexOfHighestVals = new int[uniqueVals.size()];

        for(T value : classValues){
            for(int i =0; i<uniqueVals.size(); i++){
                if(value==uniqueVals.get(i)){
                    indexOfHighestVals[i]++;
                }
            }
        }

        int highest = Integer.MIN_VALUE;
        int index = 0;
        for(int i = 0; i<indexOfHighestVals.length; i++){
            if(indexOfHighestVals[i]>highest){
                highest = indexOfHighestVals[i];
                index = i;
            }
        }

        majorityValue = uniqueVals.get(index);


        System.out.println("IN: majorityClassValue()\n\t: "+majorityValue+", count= "+highest);
        return majorityValue;
    }



    protected DataTable[] partitionSetOnAttributeValue(T columnLabel, DataTable dataSet) {

        //Find unique column values
        ArrayList<T> column = dataSet.getColumn(dataSet,columnLabel);
        int index=0;
        for(int i =0; i<dataSet.table.size(); i++){
            T col = column.get(0);
            ArrayList<T> tab = (ArrayList<T>) dataSet.table.get(i);
            if(column.get(0)==tab.get(0)){
                index=i;
                break;
            }
        }

        ArrayList uniqueVals = getUniqueValsFromColumn(table.get(index));
        DataTable[] partitionedSets = new DataTable[uniqueVals.size()];
        for(int i = 0;i<partitionedSets.length;i++){
            partitionedSets[i]=new DataTable();
        }

        //for each partition, remove unvalid tuples
        for(int i = 0; i<partitionedSets.length; i++){
         //   partitionedSets[i] = (DataTable) dataSet;
            ArrayList<ArrayList<T>> table = (ArrayList<ArrayList<T>>) dataSet.table;
            int colSize = table.size();
            ArrayList<ArrayList<T>> clonedTable= new ArrayList<ArrayList<T>>();
            for(int g = 0; g<colSize; g++){
                clonedTable.add(new ArrayList<>());
                clonedTable.get(g).add(0, table.get(g).get(0));

            }

            //Traverse col
            for(int j = 0; j<colSize;j++){
                //traverse row
                int rowSize = table.get(j).size();
                for(int k = 1 ; k<rowSize;k++){
                    T val = table.get(j).get(k);
                    T uniqueVal = (T) uniqueVals.get(i);
                    if(val==uniqueVal){
                        //add remove tuple
                        for(int z=0; z<table.size();z++){
                            T element = table.get(z).get(k);
                            if(element!=columnLabel){
                                clonedTable.get(z).add(element);
                            }
                        }
                    }
                }
            }
            partitionedSets[i].table= (ArrayList) clonedTable.clone();


            System.out.println();


        }

        //remove unwanted column
        for(int j =0;j<partitionedSets.length;j++){

            ArrayList<T> columnToRemove = partitionedSets[j].getColumn(dataSet,columnLabel);
            index=0;
            for(int i =0; i<dataSet.table.size(); i++){
                T col = columnToRemove.get(0);
                ArrayList<T> tab = (ArrayList<T>) dataSet.table.get(i);
                if(columnToRemove.get(0)==tab.get(0)){
                    index=i;
                    break;
                }
            }
            partitionedSets[j].table.remove(index);
        }
        System.out.println();

        return partitionedSets;
    }




    private ArrayList<T> getColumn(DataTable dataSet,T attribute) {
        ArrayList<T> res = null;
        for (int i = 0; i <dataSet.table.size(); i++){
            ArrayList<T> a = (ArrayList<T>) dataSet.table.get(i);
            if(a.get(0)==attribute){
                res = a;
            }

        }
        return res;
    }

    protected enum ExampleValues{
        YOUTH,
        MIDDLE_AGED,
        SENIOR,
        HIGH,
        MEDIUM,
        LOW,
        NO,
        YES,
        FAIR,
        EXCELLENT,
        AGE,
        INCOME,
        STUDENT,
        CREDIT_RATING,
        CLASS
    }
}
