package pacman.controllers.id3Controller;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import pacman.game.Constants;

import java.util.ArrayList;
import java.util.LinkedList;

/**This class represents a tabular data structure for holding data used by the ID3 classifier. The data is stored in a
 * 2d ArrayList where each column at index 0 holds the value of the column name. It is paramount that the CLASS column
 * is the last column in the list in order for the classifier to work.
 *
 * Created by: Patrik Lind, 17-03-2018
 *
 * @param
 */
public class DataTable implements Cloneable {


    protected ArrayList<ArrayList<DataTuple.DiscreteTag>> table;


    protected DataTable(){
        table = new ArrayList<ArrayList<DataTuple.DiscreteTag>>();

    }

    /**Adds a tuple to the table, if the table have less columns than the tuple additional column will be
     * initiated
     *
     * @param tuple : ArrayList<T>
     */
    private void addTuple(ArrayList<DataTuple.DiscreteTag> tuple) {
        for(int i = 0; i<tuple.size(); i++){
            if(table.size()<i+1){
                table.add(i,new ArrayList<DataTuple.DiscreteTag>());
            }
            table.get(i).add(tuple.get(i));
        }
    }


    /**Creates and returns a tuple at the specified index. The structure returned holds the column names at index 0
     * and the element values at index 1. The returned data structure is used by the decision tree to classify a tuple.
     *
     * @param indexToTuple : int
     * @return
     */
    protected  ArrayList<DataTuple.DiscreteTag>[] getTuple(int indexToTuple) {
        if(indexToTuple<0 || indexToTuple>=table.get(0).size())
            return null;

        ArrayList<DataTuple.DiscreteTag>[] tuple = new ArrayList[2];
        tuple[0] = new ArrayList<>();
        tuple[1] = new ArrayList<>();
        for (int j = 0; j<table.size();j++){
            tuple[0].add(table.get(j).get(0));
            tuple[1].add(table.get(j).get(indexToTuple));
        }
        return tuple;
    }

    /**Loads the recorded data, transforms and discretize  to further store in the data table. This is the working method
     * in where attribute selection for the data table is hard coded. When adding additional attributes be aware of the
     * requirement of the CLASS label to be the last column in the table.
     *
     */
    protected void loadRecordedData() {
        DataTuple[] rawData= DataSaverLoader.LoadPacManData();
        //Create headers for columns and add them to the table
        ArrayList<DataTuple.DiscreteTag> tuple = new ArrayList<>();
        tuple.add( DataTuple.DiscreteTag.PILL_DISTANCE);
        tuple.add(DataTuple.DiscreteTag.DIRECTION_TO_PILL);
        tuple.add(DataTuple.DiscreteTag.GHOST_DISTANCE);
        tuple.add(DataTuple.DiscreteTag.GHOST_DIRECTION);
        tuple.add(DataTuple.DiscreteTag.GHOST_EDIBLE);
        tuple.add(DataTuple.DiscreteTag.CLASS);        //MUST BE LAST!!

        addTuple(tuple);

        //Get each tuple from the raw data, transform it and store it in the table
        for(int i =0;i<rawData.length;i++){
            tuple.clear();

            //calculate closest ghost and its distanceTag & isEdible
            boolean[] ghostsEdible = new boolean[4];
            int indexToClosestGhost = 0;
            Constants.MOVE closestGhostDir = rawData[i].blinkyDir;
            int closestGhostDistance = rawData[i].blinkyDist;
            if(rawData[i].inkyDist<closestGhostDistance){
                closestGhostDir = rawData[i].inkyDir;
                closestGhostDistance = rawData[i].inkyDist;
                indexToClosestGhost=1;
            }
            if(rawData[i].pinkyDist<closestGhostDistance){
                closestGhostDir = rawData[i].pinkyDir;
                closestGhostDistance = rawData[i].pinkyDist;
                indexToClosestGhost=2;

            }
            if(rawData[i].sueDist<closestGhostDistance){
                closestGhostDir = rawData[i].sueDir;
                closestGhostDistance = rawData[i].sueDist;
                indexToClosestGhost=3;

            }
            ghostsEdible[0] = rawData[i].isBlinkyEdible;
            ghostsEdible[1] = rawData[i].isInkyEdible;
            ghostsEdible[2] = rawData[i].isPinkyEdible;
            ghostsEdible[3] = rawData[i].isSueEdible;

            //get pill distanceTag
            tuple.add( pillDistance(rawData[i].pillDist) );
            //get direction to pill
            tuple.add(parseMoveToPill(rawData[i].pillMove));
            //get discrete distanceTag
            tuple.add(ghostDistance(closestGhostDistance));
            //get his direction
            tuple.add(parseMoveGhost(closestGhostDir));
            //get edible
            if (ghostsEdible[indexToClosestGhost])
                tuple.add(DataTuple.DiscreteTag.EDIBLE_TRUE);
            else
                tuple.add(DataTuple.DiscreteTag.EDIBLE_FALSE);


            //get class
            tuple.add(parseMoveClass(rawData[i].DirectionChosen));
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
     * @param distanceToPill the distanceTag
     * @return the discretized value
     */
    protected DataTuple.DiscreteTag pillDistance(int distanceToPill)
    {
        if (distanceToPill < 10)
            return DataTuple.DiscreteTag.VERY_LOW;
        if (distanceToPill < 20)
            return DataTuple.DiscreteTag.LOW;
        if (distanceToPill < 50)
            return DataTuple.DiscreteTag.MEDIUM;
        return  DataTuple.DiscreteTag.HIGH;
    }

    /**Parse the input to enum of the type of this class and returns it as DiscreteTag.
     *
     * @param directionChosen : Constants.MOVE
     * @return direction : DiscreteTag
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

    /**Clones and returns a copy of this object.
     *
     * @return clone : DataTable<T>
     * @throws CloneNotSupportedException
     */
    protected DataTable clone() throws CloneNotSupportedException {
        return (DataTable) super.clone();
    }

    /**Returns all attributes in the table, i.e all column names except for the last one (CLASS).
     *
     * @return : LinkedList<T>
     */
    protected LinkedList<DataTuple.DiscreteTag> getAttributeList() {
        LinkedList<DataTuple.DiscreteTag> list = new LinkedList<>();
        for(ArrayList<DataTuple.DiscreteTag> a : table){
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
    protected DataTuple.DiscreteTag getClassLabel(int i) {
        if(table.isEmpty())
            throw new IndexOutOfBoundsException("Table is empty");
        if(i<1)
            i=1;
        else if (i>=table.get(0).size())
            i = table.get(0).size()-1;

        return table.get(table.size()-1).get(i);
    }

    /**Finds and returns all the unique values in a column (column name excluded).
     *
     * @param column : ArrayList<T>
     * @return uniqueVals : ArrayList<T>
     */
    protected ArrayList<DataTuple.DiscreteTag> getUniqueValsFromColumn(ArrayList<DataTuple.DiscreteTag> column){
        ArrayList<DataTuple.DiscreteTag> uniqueVals = new ArrayList();
        for(DataTuple.DiscreteTag value : column){
            if (!uniqueVals.contains(value)){
                uniqueVals.add(value);
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
    protected DataTuple.DiscreteTag majorityClassValue() {

        ArrayList<DataTuple.DiscreteTag> classValues = table.get(table.size()-1);
        DataTuple.DiscreteTag majorityValue = null;
        ArrayList<DataTuple.DiscreteTag> uniqueVals = getUniqueValsFromColumn(table.get(table.size()-1));
        int[] indexOfHighestVals = new int[uniqueVals.size()];

        for(int j = 1; j<classValues.size();j++){
            DataTuple.DiscreteTag value = classValues.get(j);
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


        if(Utilities.log)
            System.out.println("### IN majorityClassValue\n\t "+majorityValue+", count= "+highest);
        return majorityValue;
    }


    /**
     *
     * @param columnLabel : T
     * @param dataSet : DataTable
     * @return : paritionedTable : DataTable[]
     */
    protected DataTable[] partitionSetOnAttributeValue(DataTuple.DiscreteTag columnLabel, DataTable dataSet) throws Exception {

        //Find unique column values
        ArrayList<DataTuple.DiscreteTag> column = dataSet.getColumn(dataSet,columnLabel);
        int index=0;
        for(int i =0; i<dataSet.table.size(); i++){
            DataTuple.DiscreteTag col = column.get(0);
            ArrayList<DataTuple.DiscreteTag> tab =  dataSet.table.get(i);
            if(column.get(0)==tab.get(0)){
                index=i;
                break;
            }
        }

        ArrayList<DataTuple.DiscreteTag> uniqueVals = getUniqueValsFromColumn(table.get(index));
        DataTable[] partitionedTable = new DataTable[uniqueVals.size()];
        for(int i = 0;i<partitionedTable.length;i++){
            partitionedTable[i]=new DataTable();
        }

        //for each partition, remove unvalid tuples
        for(int i = 0; i<partitionedTable.length; i++){
         //   partitionedSets[i] = (DataTable) dataSet;
            ArrayList<ArrayList<DataTuple.DiscreteTag>> table =  dataSet.table;
            int colSize = table.size();
            ArrayList<ArrayList<DataTuple.DiscreteTag>> clonedTable= new ArrayList<>();

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
                    DataTuple.DiscreteTag val = table.get(j).get(k);
                    DataTuple.DiscreteTag uniqueVal = uniqueVals.get(i);
                    if(val==uniqueVal){
                        //add tuple to the partition i
                        for(int l=0; l<table.size();l++){
                            DataTuple.DiscreteTag element = table.get(l).get(k);
                            if(element!=columnLabel){
                                clonedTable.get(l).add(element);
                            }
                        }
                    }
                }
            }
            partitionedTable[i].table=  clonedTable; //TODO .clone()??

        }

        //remove unwanted column (on which the partition occurs)
        for(int j =0;j<partitionedTable.length;j++){

            ArrayList<DataTuple.DiscreteTag> columnToRemove = partitionedTable[j].getColumn(dataSet,columnLabel);
            index=0;
            for(int i =0; i<dataSet.table.size(); i++){
                DataTuple.DiscreteTag col = columnToRemove.get(0);
                ArrayList<DataTuple.DiscreteTag> tab =  dataSet.table.get(i);
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
    protected ArrayList<DataTuple.DiscreteTag> getColumn(DataTable dataSet,DataTuple.DiscreteTag attribute) throws Exception {
        ArrayList<DataTuple.DiscreteTag> column = null;
        for (int i = 0; i <dataSet.table.size(); i++){
            ArrayList<DataTuple.DiscreteTag> a = dataSet.table.get(i);
            if(a.get(0)==attribute){
                column = a;
            }

        }
        if(column == null)
            throw new Exception(attribute+"is not present in the table");
        return column;
    }

    /**Splits the data table passed in as arguments into two partitions with the distribution: 2/3 & 1/3.
     * The array returned stores the larger partition at index 0.
     *
     * @param dataSet : DataSet
     * @return arr : DataSet[]
     * TODO
     */
    public DataTable[] splitTableForHoldout(DataTable dataSet) {

        DataTable[] arr = new DataTable[2];
        arr[0] = new DataTable();
        arr[1] = new DataTable();
        try {
            DataTable table = dataSet.clone();
            int rows = table.table.get(0).size();
            for(int i =0;i<rows;i++){
                if(i<= (int)rows*0.67){
                    ArrayList<DataTuple.DiscreteTag>[] tuple = table.getTuple(i);
                    arr[0].addTuple(tuple[1]);
                    if(i==0)
                        arr[1].addTuple(tuple[1]);

                }else{
                    ArrayList<DataTuple.DiscreteTag>[] tuple = table.getTuple(i);
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

        loadRecordedData();
        ArrayList a = getUniqueValsFromColumn(table.get(0));
       boolean flag = everyTupleInSameClass();
        DataTuple.DiscreteTag val =  majorityClassValue();
        LinkedList<DataTuple.DiscreteTag> attrList = getAttributeList();
        ArrayList<DataTuple.DiscreteTag>[] tuple = getTuple(1);
     //   tuple =getTuple(new Game());
        DataTable[] tables = splitTableForHoldout(this);
        //T label = getClassLabel(13);
     //   ArrayList<T> col = getColumn(this, (T) DiscreteValues.CREDIT_RATING);
        try {
            DataTable[] tablesPartitioned =  partitionSetOnAttributeValue( DataTuple.DiscreteTag.GHOST_DIRECTION,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();

    }


    public String toString(){
        StringBuilder sb = new StringBuilder();

        ArrayList<String> columnNames = new ArrayList<>();
        for (ArrayList<DataTuple.DiscreteTag> col : table){
            sb.append("Columns in table: "+col.get(0).toString()+"\t\n Number of tuples: "+(col.size()-1));
        }

        return "### DataTable\n\t"+sb.toString();
    }


    //Temp for testing
    public static void main(String[] args) {
        DataTable data = new DataTable();
        data.test();



    }

    public double getTableSize() {
        return table.get(0).size()-1;
    }
}
