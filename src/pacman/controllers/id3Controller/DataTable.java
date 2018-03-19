package pacman.controllers.id3Controller;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.LinkedList;

/**This class represents a tabular data structure for holding data used by the ID3 classifier. The data is stored in a
 * 2d ArrayList where each column at index 0 holds the value of the column name. It is paramount that the CLASS column
 * is the last column in the list.
 *
 * Created by: Patrik Lind, 17-03-2018
 *
 * @param <T>
 */
public class DataTable <T> implements Cloneable {
    //int, MOVE , DiscreteTag, bool
    protected ArrayList<ArrayList<T>> table;
    protected T edgeLabel;




    /**Adds a tuple to the table, if the table have less columns than the tuple additional column will be
     * initiated
     *
     * @param tuple : ArrayList<T>
     */
    private void addTuple(ArrayList<T> tuple) {
        for(int i = 0; i<tuple.size(); i++){
            if(table.size()<i+1){
                table.add(i,new ArrayList<T>());
            }
            table.get(i).add(tuple.get(i));
        }
    }


    /**Loads the recorded data from 'trainingData.txt' and creates a table based on that data.
     * TODO make this method more dynamic, no hard coding!
     */

    protected  <T> ArrayList<T>[] getTuple(int i) {
        if(i<0 || i>=table.get(0).size())
            return null;

        ArrayList<T>[] tuple = new ArrayList[2];
        tuple[0] = new ArrayList<T>();
        tuple[1] = new ArrayList<T>();
        for (int j = 0; j<table.size();j++){
            tuple[0].add((T) table.get(j).get(0));
            tuple[1].add((T) table.get(j).get(i));
        }
        return tuple;
    }

    protected void loadRecordedData() {
        DataTuple[] pacManData= DataSaverLoader.LoadPacManData();
        //Create headers for columns
        ArrayList tuple = new ArrayList<>();
        tuple.add( DataTuple.DiscreteTag.PILL_DISTANCE);
        tuple.add(DataTuple.DiscreteTag.DIRECTION_TO_PILL);
        tuple.add(DataTuple.DiscreteTag.GHOST_DISTANCE);
        tuple.add(DataTuple.DiscreteTag.GHOST_DIRECTION);
        tuple.add(DataTuple.DiscreteTag.CLASS);        //MUST BE LAST!!


        //insert headers
        addTuple(tuple);

        //insert tuples
        /**CLASS
         * MAZE - need to navigate
         * pacmanPosition in maze
         *
         */
        for(int i =0;i<pacManData.length;i++){
            tuple.clear();

            //calculate closest ghost and its distanceTag
            Constants.MOVE closestGhostDir = pacManData[i].blinkyDir;
            int closestGhostDistance = pacManData[i].blinkyDist;
            if(pacManData[i].inkyDist<closestGhostDistance){
                closestGhostDir = pacManData[i].inkyDir;
                closestGhostDistance = pacManData[i].inkyDist;
            }
            if(pacManData[i].pinkyDist<closestGhostDistance){
                closestGhostDir = pacManData[i].pinkyDir;
                closestGhostDistance = pacManData[i].pinkyDist;
            }
            if(pacManData[i].sueDist<closestGhostDistance){
                closestGhostDir = pacManData[i].sueDir;
                closestGhostDistance = pacManData[i].sueDist;
            }

            //get pill distanceTag
            tuple.add( pillDistance(pacManData[i].pillDist) );
            //get direction to pill
            tuple.add(parseMoveToPill(pacManData[i].pillMove));
            //get discrete distanceTag
            tuple.add(ghostDistance(closestGhostDistance));
            //get his direction
            tuple.add(parseMoveGhost(closestGhostDir));


            //get class
            tuple.add(parseMoveClass(pacManData[i].DirectionChosen));
            addTuple(tuple);
        }

    }

    protected DataTuple.DiscreteTag ghostDistance(int distance)
    {
        if (distance < 20)
            return  DataTuple.DiscreteTag.VERY_LOW;
        if (distance < 40)
            return DataTuple.DiscreteTag.LOW;
        if (distance < 60)
            return DataTuple.DiscreteTag.MEDIUM;
        if (distance < 100)
            return DataTuple.DiscreteTag.HIGH;
        return DataTuple.DiscreteTag.VERY_HIGH;
    }

    /**
     * Discretize a distanceTag to a pill
     * @param distance the distanceTag
     * @return the discretized value
     */
    protected DataTuple.DiscreteTag pillDistance(int distance)
    {
        if (distance < 10)
            return DataTuple.DiscreteTag.VERY_LOW;
        if (distance < 20)
            return DataTuple.DiscreteTag.LOW;
        if (distance < 50)
            return DataTuple.DiscreteTag.MEDIUM;
        return  DataTuple.DiscreteTag.HIGH;
    }

    /**Parse the input to enum of the type of this class and returns it as T.
     *
     * @param directionChosen : Constants.MOVE
     * @return direction : T
     */
    protected DataTuple.DiscreteTag parseMoveClass(Constants.MOVE directionChosen) {
        final String s = directionChosen.name().toUpperCase();
        DataTuple.DiscreteTag direction= null;
        switch (s){
            case "UP":
                direction = DataTuple.DiscreteTag.CLASS_UP;
                break;
            case "DOWN":
                direction =  DataTuple.DiscreteTag.CLASS_DOWN;
                break;
            case "LEFT":
                direction = DataTuple.DiscreteTag.CLASS_LEFT;
                break;
            case "RIGHT":
                direction = DataTuple.DiscreteTag.CLASS_RIGHT;
        }

        return direction;
    }
    protected DataTuple.DiscreteTag parseMoveGhost(Constants.MOVE directionChosen) {
        final String s = directionChosen.name().toUpperCase();
        DataTuple.DiscreteTag direction= null;
        switch (s){
            case "UP":
                direction = DataTuple.DiscreteTag.GHOST_MOVE_UP;
                break;
            case "DOWN":
                direction = DataTuple.DiscreteTag.GHOST_MOVE_DOWN;
                break;
            case "LEFT":
                direction = DataTuple.DiscreteTag.GHOST_MOVE_LEFT;
                break;
            case "RIGHT":
                direction = DataTuple.DiscreteTag.GHOST_MOVE_RIGHT;
                break;
            case "NEUTRAL":
                direction = DataTuple.DiscreteTag.GHOST_MOVE_NEUTRAL;
        }

        return direction;
    }
    protected DataTuple.DiscreteTag parseMoveToPill(Constants.MOVE directionChosen) {
        final String s = directionChosen.name().toUpperCase();
        DataTuple.DiscreteTag direction= null;
        switch (s){
            case "UP":
                direction = DataTuple.DiscreteTag.TO_PILL_UP;
                break;
            case "DOWN":
                direction = DataTuple.DiscreteTag.TO_PILL_DOWN;
                break;
            case "LEFT":
                direction = DataTuple.DiscreteTag.TO_PILL_LEFT;
                break;
            case "RIGHT":
                direction = DataTuple.DiscreteTag.TO_PILL_RIGHT;

        }

        return direction;
    }

    public DataTable(){
        table = new ArrayList<ArrayList<T>>();

    }


    /**Clones and returns a copy of this object.
     *
     * @return clone : DataTable<T>
     * @throws CloneNotSupportedException
     */
    protected DataTable<T> clone() throws CloneNotSupportedException {
        return (DataTable<T>) super.clone();
    }

    /**Returns all attributes in the table, i.e all column names except for the last one (CLASS).
     *
     * @return : LinkedList<T>
     */
    protected LinkedList<T> getAttributeList() {
        LinkedList<T> list = new LinkedList<>();
        for(ArrayList<T> a : table){
            list.add(a.get(0));
        }
        //remove class column
        list.removeLast();
        return list;
    }

    /**Gets the tuple at index i, if i is out of bounds null will be returned. The tuple includes
     * the class value.
     *
     * @param i : int
     * @param <T>
     * @return tuple : ArrayList<Tuple>
     */



    /**Returns the class label at index i. If i is out of bounds, it will either get the first or last label. The
     * Accepted range of i:  1 < i < table.get().get().size()-1
     *
     * @param i : int
     * @return classLabel : T
     */
    protected T getClassLabel(int i) {
        if(table.isEmpty())
            throw new IndexOutOfBoundsException("Table is empty");
        if(i<1)
            i=1;
        else if (i>=table.get(0).size())
            i = table.get(0).size()-1;

        return (T) table.get(table.size()-1).get(i);
    }

    /**Finds and returns all the unique values in a column (column name excluded).
     *
     * @param column : ArrayList<T>
     * @return uniqueVals : ArrayList<T>
     */
    protected ArrayList<T> getUniqueValsFromColumn(ArrayList<T> column){
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

        return flag;
    }

    /**Traverses the table to find the classifier which occurs the most in the table. If more than 1 classifiers
     * share the first place, the first classifier found is returned.
     *
     * @return majorityValue : T
     */
    protected T majorityClassValue() {

        ArrayList<T> classValues = table.get(table.size()-1);
        T majorityValue = null;
        ArrayList<T> uniqueVals = getUniqueValsFromColumn(table.get(table.size()-1));
        int[] indexOfHighestVals = new int[uniqueVals.size()];

        for(int j = 1; j<classValues.size();j++){
            T value = classValues.get(j);
            for(int i =0; i<uniqueVals.size(); i++){
                if(value==uniqueVals.get(i)){
                    indexOfHighestVals[i]++;
                    break;
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


        System.out.println("### IN majorityClassValue\n\t "+majorityValue+", count= "+highest);
        return majorityValue;
    }


    /**
     *
     * @param columnLabel : T
     * @param dataSet : DataTable
     * @return : paritionedTable : DataTable[]
     */
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
        DataTable[] partitionedTable = new DataTable[uniqueVals.size()];
        for(int i = 0;i<partitionedTable.length;i++){
            partitionedTable[i]=new DataTable();
        }

        //for each partition, remove unvalid tuples
        for(int i = 0; i<partitionedTable.length; i++){
         //   partitionedSets[i] = (DataTable) dataSet;
            ArrayList<ArrayList<T>> table = (ArrayList<ArrayList<T>>) dataSet.table;
            int colSize = table.size();
            ArrayList<ArrayList<T>> clonedTable= new ArrayList<ArrayList<T>>();

            //Add column names
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
                        //add tuple to the partition i
                        for(int l=0; l<table.size();l++){
                            T element = table.get(l).get(k);
                            if(element!=columnLabel){
                                clonedTable.get(l).add(element);
                            }
                        }
                    }
                }
            }
            partitionedTable[i].table= (ArrayList) clonedTable.clone();

        }

        //remove unwanted column (on which the partition occurs)
        for(int j =0;j<partitionedTable.length;j++){

            ArrayList<T> columnToRemove = partitionedTable[j].getColumn(dataSet,columnLabel);
            index=0;
            for(int i =0; i<dataSet.table.size(); i++){
                T col = columnToRemove.get(0);
                ArrayList<T> tab = (ArrayList<T>) dataSet.table.get(i);
                if(columnToRemove.get(0)==tab.get(0)){
                    index=i;
                    break;
                }
            }
            partitionedTable[j].table.remove(index);
        }

        return partitionedTable;
    }


    /**Returns the column with column name T attribute.
     *
     * @param dataSet : DataTable<T>
     * @param attribute : T <<MUST BE A COLUMN NAME>>
     * @return column : ArratList<T>
     * @throws NullPointerException
     */
    protected ArrayList<T> getColumn(DataTable dataSet,T attribute) throws NullPointerException{
        ArrayList<T> column = null;
        for (int i = 0; i <dataSet.table.size(); i++){
            ArrayList<T> a = (ArrayList<T>) dataSet.table.get(i);
            if(a.get(0)==attribute){
                column = a;
            }

        }
        if(column == null)
            throw new NullPointerException("Attribute passed in as argument probably isn't a column name");
        return column;
    }

    /**Splits the data table passed in as arguments into two partitions with the distribution: 2/3 & 1/3.
     * The array returned stores the larger partition at index 0.
     *
     * @param dataSet : DataSet
     * @return arr : DataSet[]
     * TODO
     */
    public DataTable<T>[] splitTableForHoldout(DataTable dataSet) {

        DataTable<T>[] arr = new DataTable[2];
        arr[0] = new DataTable<>();
        arr[1] = new DataTable<>();
        try {
            DataTable<T> table = dataSet.clone();
            int rows = table.table.get(0).size();
            for(int i =0;i<rows;i++){
                if(i<= (int)rows*0.67){
                    ArrayList<T>[] tuple = table.getTuple(i);
                    arr[0].addTuple(tuple[1]);
                    if(i==0)
                        arr[1].addTuple(tuple[1]);

                }else{
                    ArrayList<T>[] tuple = table.getTuple(i);
                    arr[1].addTuple(tuple[1]);
                }


            }
            //remove last col of [1]
           // arr[1].table.remove(arr[1].table.size()-1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return arr;
    }



    /**
     * Temp for testing
     */
    private void test() {

        //loadExampleData();
        loadRecordedData();
        ArrayList a = getUniqueValsFromColumn(table.get(0));
        everyTupleInSameClass();
      T val =  majorityClassValue();
        LinkedList<T> attrList = getAttributeList();
        ArrayList<T>[] tuple = getTuple(1);
     //   tuple =getTuple(new Game());
        DataTable<T>[] tables = splitTableForHoldout(this);
        //T label = getClassLabel(13);
     //   ArrayList<T> col = getColumn(this, (T) DiscreteValues.CREDIT_RATING);
       // DataTable[] tables =  partitionSetOnAttributeValue((T) DiscreteValues.INCOME,this);

    }



    //Temp for testing
    protected void loadExampleData() {
       ArrayList a1 = new ArrayList();
        a1.add(DataTuple.DiscreteTag.AGE);
        a1.add(DataTuple.DiscreteTag.YOUTH);
        a1.add(DataTuple.DiscreteTag.YOUTH);
        a1.add(DataTuple.DiscreteTag.MIDDLE_AGED);
        a1.add(DataTuple.DiscreteTag.SENIOR);
        a1.add(DataTuple.DiscreteTag.SENIOR);
        a1.add(DataTuple.DiscreteTag.SENIOR);
        a1.add(DataTuple.DiscreteTag.MIDDLE_AGED);
        a1.add(DataTuple.DiscreteTag.YOUTH);
        a1.add(DataTuple.DiscreteTag.YOUTH);
        a1.add(DataTuple.DiscreteTag.SENIOR);
        a1.add(DataTuple.DiscreteTag.MIDDLE_AGED);
        a1.add(DataTuple.DiscreteTag.YOUTH);
        a1.add(DataTuple.DiscreteTag.MIDDLE_AGED);
        a1.add(DataTuple.DiscreteTag.SENIOR);

        ArrayList a2 = new ArrayList();
        a2.add(DataTuple.DiscreteTag.INCOME);
        a2.add(DataTuple.DiscreteTag.HIGH);
        a2.add(DataTuple.DiscreteTag.HIGH);
        a2.add(DataTuple.DiscreteTag.HIGH);
        a2.add(DataTuple.DiscreteTag.MEDIUM);
        a2.add(DataTuple.DiscreteTag.LOW);
        a2.add(DataTuple.DiscreteTag.LOW);
        a2.add(DataTuple.DiscreteTag.LOW);
        a2.add(DataTuple.DiscreteTag.MEDIUM);
        a2.add(DataTuple.DiscreteTag.LOW);
        a2.add(DataTuple.DiscreteTag.MEDIUM);
        a2.add(DataTuple.DiscreteTag.MEDIUM);
        a2.add(DataTuple.DiscreteTag.MEDIUM);
        a2.add(DataTuple.DiscreteTag.HIGH);
        a2.add(DataTuple.DiscreteTag.MEDIUM);

        ArrayList a3 = new ArrayList();
        a3.add(DataTuple.DiscreteTag.STUDENT);
        a3.add(DataTuple.DiscreteTag.NO);
        a3.add(DataTuple.DiscreteTag.NO);
        a3.add(DataTuple.DiscreteTag.NO);
        a3.add(DataTuple.DiscreteTag.NO);
        a3.add(DataTuple.DiscreteTag.YES);
        a3.add(DataTuple.DiscreteTag.YES);
        a3.add(DataTuple.DiscreteTag.YES);
        a3.add(DataTuple.DiscreteTag.NO);
        a3.add(DataTuple.DiscreteTag.YES);
        a3.add(DataTuple.DiscreteTag.YES);
        a3.add(DataTuple.DiscreteTag.YES);
        a3.add(DataTuple.DiscreteTag.NO);
        a3.add(DataTuple.DiscreteTag.YES);
        a3.add(DataTuple.DiscreteTag.NO);

        ArrayList a4 = new ArrayList();
        a4.add(DataTuple.DiscreteTag.CREDIT_RATING);
        a4.add(DataTuple.DiscreteTag.FAIR);
        a4.add(DataTuple.DiscreteTag.EXCELLENT);
        a4.add(DataTuple.DiscreteTag.FAIR);
        a4.add(DataTuple.DiscreteTag.FAIR);
        a4.add(DataTuple.DiscreteTag.FAIR);
        a4.add(DataTuple.DiscreteTag.EXCELLENT);
        a4.add(DataTuple.DiscreteTag.EXCELLENT);
        a4.add(DataTuple.DiscreteTag.FAIR);
        a4.add(DataTuple.DiscreteTag.FAIR);
        a4.add(DataTuple.DiscreteTag.FAIR);
        a4.add(DataTuple.DiscreteTag.EXCELLENT);
        a4.add(DataTuple.DiscreteTag.EXCELLENT);
        a4.add(DataTuple.DiscreteTag.FAIR);
        a4.add(DataTuple.DiscreteTag.EXCELLENT);

        ArrayList a5 = new ArrayList();
        a5.add(DataTuple.DiscreteTag.CLASS);
        a5.add(DataTuple.DiscreteTag.NO);
        a5.add(DataTuple.DiscreteTag.NO);
        a5.add(DataTuple.DiscreteTag.YES);
        a5.add(DataTuple.DiscreteTag.YES);
        a5.add(DataTuple.DiscreteTag.YES);
        a5.add(DataTuple.DiscreteTag.NO);
        a5.add(DataTuple.DiscreteTag.YES);
        a5.add(DataTuple.DiscreteTag.NO);
        a5.add(DataTuple.DiscreteTag.YES);
        a5.add(DataTuple.DiscreteTag.YES);
        a5.add(DataTuple.DiscreteTag.YES);
        a5.add(DataTuple.DiscreteTag.YES);
        a5.add(DataTuple.DiscreteTag.YES);
        a5.add(DataTuple.DiscreteTag.NO);


        table.add(a1);
        table.add(a2);
        table.add(a3);
        table.add(a4);
        table.add(a5);

        System.out.println(toString());

    }

    public String toString(){
        StringBuilder sb = new StringBuilder();

        ArrayList<String> columnNames = new ArrayList<>();
        for (ArrayList<T> col : table){
            sb.append(col.get(0).toString()+", size: "+col.size()+", ");
        }

        return "### DataTable\n\t"+sb.toString();
    }


    //Temp for testing
    public static void main(String[] args) {
        DataTable data = new DataTable();
        data.test();



    }


    public void printTuple(ArrayList<T>[] tuple) {
        for(int i =0;i<tuple.length;i++){
            for(T val : tuple[i]){
                System.out.print(val+"\t\t");
            }
            System.out.println();

        }
    }
}
