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
 * OBSERVE: Every column must have unique enums DataTuple.DiscreteTag, even if different columns share similar elements
 * eg class move != ghost move
 *
 * Created by: Patrik Lind, 17-03-2018
 *
 * @param
 */
public class DataTable implements Cloneable {


    protected ArrayList<ArrayList<String>> table;


    protected DataTable(){
        table = new ArrayList<ArrayList<String>>();
    }

    /**Adds a tuple to the table, if the table have less columns than the tuple additional column will be
     * initiated
     *
     * @param tuple : ArrayList<T>
     */
    protected void addTuple(ArrayList<String> tuple) {
        for(int i = 0; i<tuple.size(); i++){
            if(table.size()<i+1){
                table.add(i,new ArrayList<String>());
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
    protected  ArrayList<String>[] getTuple(int indexToTuple) {
        if(indexToTuple<=0 )
            indexToTuple=1;
        if(indexToTuple>table.get(0).size()-1)
            indexToTuple = table.get(0).size()-1;

        ArrayList<String>[] tuple = new ArrayList[2];
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
     * OBSERVE! When hardcoding additional columns, make sure that you hardcode in getMove callback in MsPacmanID#
     *
     */
    protected void loadRecordedData() {
        DataTuple[] rawData= DataSaverLoader.LoadPacManData();

        //Create headers for columns and add them to the table
        ArrayList<String> tuple = new ArrayList<>();

        //Power pill
        if(Utilities.POWER_PILLS){
            tuple.add("power_pill_distance");
            tuple.add("direction_to_power_pill");

        }

        //Pill
        tuple.add( "pill_distance");
        tuple.add("direction_to_pill");

        if(Utilities.ALL_GHOSTS){

            //Blinky
            //  tuple.add(DataTuple.DiscreteTag.BLINKY_DISTANCE);
            tuple.add("bliky_direction");
            //  tuple.add(DataTuple.DiscreteTag.BLINKY_EDIBLE);

            //Pinky
            //tuple.add(DataTuple.DiscreteTag.PINKY_DISTANCE);
            tuple.add("pinky_direction");
            //tuple.add(DataTuple.DiscreteTag.PINKY_EDIBLE);

            //Inky
            //tuple.add(DataTuple.DiscreteTag.INKY_DISTANCE);
            tuple.add("inky_direction");
            //tuple.add(DataTuple.DiscreteTag.INKY_EDIBLE);

            //Sue
            //tuple.add(DataTuple.DiscreteTag.SUE_DISTANCE);
            tuple.add("sue_direction");
            //tuple.add(DataTuple.DiscreteTag.SUE_EDIBLE);


        }

        tuple.add("closest_ghost_distance");
        tuple.add("closest_ghost_direction");
      //  tuple.add("closest_ghost_edible");

        tuple.add("class");        //MUST BE LAST!!

        addTuple(tuple);

        //Get each tuple from the raw data, transform it and store it in the table
        for(int i =0;i<rawData.length;i++){
            tuple.clear();

            //calculate closest ghost and its distanceTag & isEdible
            boolean[] ghostsEdible = new boolean[4];
            int indexToClosestGhost = 0;
            Constants.MOVE closestGhostDir = rawData[i].blinkyDir;
            int closestGhostDistance = rawData[i].blinkyDist;
            if(rawData[i].pinkyDist<closestGhostDistance){
                closestGhostDir = rawData[i].pinkyDir;
                closestGhostDistance = rawData[i].pinkyDist;
                indexToClosestGhost=1;

            }
            if(rawData[i].inkyDist<closestGhostDistance){
                closestGhostDir = rawData[i].inkyDir;
                closestGhostDistance = rawData[i].inkyDist;
                indexToClosestGhost=2;
            }
            if(rawData[i].sueDist<closestGhostDistance){
                closestGhostDir = rawData[i].sueDir;
                closestGhostDistance = rawData[i].sueDist;
                indexToClosestGhost=3;

            }
            ghostsEdible[0] = rawData[i].isBlinkyEdible;
            ghostsEdible[1] = rawData[i].isPinkyEdible;
            ghostsEdible[2] = rawData[i].isInkyEdible;
            ghostsEdible[3] = rawData[i].isSueEdible;

            if(Utilities.POWER_PILLS){
                //get power pill distance
                tuple.add( parseDistancePill((rawData[i].powerpillDist)));
                //get direction to power pill
                tuple.add(parseMove(rawData[i].powerpillMove));

            }
            //get pill distanceTag
            tuple.add( parseDistancePill(rawData[i].pillDist));
            //get direction to pill
            tuple.add(parseMove(rawData[i].pillMove));

            if(Utilities.ALL_GHOSTS){

                //  tuple.add(ghostDistance(rawData[i].blinkyDist));
                tuple.add(parseMove(rawData[i].blinkyDir));
            /*if(ghostsEdible[0])
                tuple.add(DataTuple.DiscreteTag.EDIBLE_TRUE);
            else
                tuple.add(DataTuple.DiscreteTag.EDIBLE_FALSE);
*/
                //pinky
                //          tuple.add(ghostDistance(rawData[i].pinkyDist));
                tuple.add(parseMove(rawData[i].pinkyDir));
    /*        if(ghostsEdible[1])
                tuple.add(DataTuple.DiscreteTag.EDIBLE_TRUE);
            else
                tuple.add(DataTuple.DiscreteTag.EDIBLE_FALSE);
*/
                //inky
                //          tuple.add(ghostDistance(rawData[i].inkyDist));
                tuple.add(parseMove(rawData[i].inkyDir));
    /*        if(ghostsEdible[2])
                tuple.add(DataTuple.DiscreteTag.EDIBLE_TRUE);
            else
                tuple.add(DataTuple.DiscreteTag.EDIBLE_FALSE);
*/

                //sue
                //        tuple.add(ghostDistance(rawData[i].sueDist));
                tuple.add(parseMove(rawData[i].sueDir));
      /*      if(ghostsEdible[3])
                tuple.add(DataTuple.DiscreteTag.EDIBLE_TRUE);
            else
                tuple.add(DataTuple.DiscreteTag.EDIBLE_FALSE);
*/
            }

            //blinky

            //closest ghost
            tuple.add(parseDistanceGhost(closestGhostDistance));
            tuple.add(parseMove(closestGhostDir));
         //   tuple.add(parseEdible(ghostsEdible[indexToClosestGhost]));

            //get class
            tuple.add(parseMove(rawData[i].DirectionChosen));

            addTuple(tuple);
        }

    }

    protected String parseEdible(boolean edible) {
        if(edible)
            return "true";
        return "false";
    }

    protected String parseMove(Constants.MOVE move) {
        String discreteMove="";
        switch (move.name().toUpperCase()){
            case "UP":
                discreteMove = "up";
                break;
            case "DOWN":
                discreteMove=  "down";
                break;
            case "LEFT":
                discreteMove = "left";
                break;
            case "RIGHT":
                discreteMove = "right";
                break;
            case "NEUTRAL":
                discreteMove = "neutral";
        }

        return discreteMove;
    }

    protected String parseDistanceGhost(int distance) {
        String discreteDistance;
        if(distance==-1)
            return "caged";
        if (distance < 20)
            discreteDistance =  "very_low";
        if (distance < 40)
            discreteDistance = "low";
        if (distance < 80)
            discreteDistance =  "medium";
        if (distance < 100)
            discreteDistance =  "medium";
        return discreteDistance = "high";
    }

    protected String parseDistancePill(int distance) {
        String discreteDistance;
        if (distance < 10)
            discreteDistance =  "very_low";
        if (distance < 20)
            discreteDistance = "low";
        if (distance < 50)
            discreteDistance =  "medium";
        return discreteDistance = "high";
    }

    /**Clones and returns a copy of this object.
     *
     * @return clone : DataTable<T>
     * @throws CloneNotSupportedException
     */
    protected DataTable clone() throws CloneNotSupportedException {
        DataTable cloned =  (DataTable) super.clone();
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>(table.size());
        ArrayList<ArrayList<String>> copy = new ArrayList<ArrayList<String>>(table.size());
        for(int i =0 ;i<table.size();i++){
            copy.add((ArrayList<String>) table.get(i).clone());
        }

        return cloned;
    }

    /**Returns all attributes in the table, i.e all column names except for the last one (CLASS).
     *
     * @return : LinkedList<T>
     */


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
     * @return classLabel : String TODO KOLLA OM DETTA BUGGAR!
     */
    protected String getClassLabel() {

        return table.get(table.size()-1).get(1);
    }

    /**Finds and returns all the unique values in a column (column name excluded).
     *
     * @param column : ArrayList<T>
     * @return uniqueVals : ArrayList<T>
     */
    protected ArrayList<String> getUniqueValsFromColumn(ArrayList<String> column){
        ArrayList<String> uniqueVals = new ArrayList<>();
        for(String value : column){
            if (!uniqueVals.contains(value)){
                uniqueVals.add(value);
            }
        }
        uniqueVals.remove(0); //column name
        return uniqueVals;
    }

    /**Checks if all the tuples belong to the same class.
     *
     * @return : boolean
     */
    protected static boolean everyTupleInSameClass(DataTable dataTable) {
        int last = dataTable.table.size()-1;
        if(last<0)
            last =0;

        ArrayList<String> col = dataTable.getUniqueValsFromColumn(dataTable.getColumn(dataTable, "class"));
        if(col.size()>1)
            return false;

        return true;
    }

    /**Traverses the table to find the classifier which occurs the most in the table. If more than 1 classifiers
     * share the first place, the first classifier found is returned.
     *
     * @return majorityValue : T
     */
    protected static String majorityClassValue(DataTable dataTable) {

        ArrayList<String> classCol = dataTable.getColumn(dataTable, "class");
        ArrayList<String> uniqueVals = dataTable.getUniqueValsFromColumn(classCol);

        int []indexOfHigherVals =new int[uniqueVals.size()];
        for(int i = 0; i<classCol.size(); i++){
            for(int j =0;j< uniqueVals.size();j++){
                if(classCol.get(i)==uniqueVals.get(j)){
                    indexOfHigherVals[j]++;
                }
            }
        }

        int highest = Integer.MIN_VALUE;
        int index = 0;
        for(int i = 0; i<indexOfHigherVals.length;i++){
            if(indexOfHigherVals[i]>highest){
                highest = indexOfHigherVals[i];
                index = i;
            }
        }
        return uniqueVals.get(index);

    }

    /**Returns the column with column name T attribute.
     *
     * @param dataSet : DataTable<T>
     * @param attribute : String <<MUST BE A COLUMN NAME>>
     * @return column : ArratList<String>
     */
    protected ArrayList<String> getColumn(DataTable dataSet,String attribute)  {
        ArrayList<String> column = new ArrayList<>();
        for (int i = 0; i <dataSet.table.size(); i++){
            ArrayList<String> a = dataSet.table.get(i);
            if(a.get(0).equalsIgnoreCase(attribute)){
                column = a;
            }
        }

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
                ArrayList<String>[] tuple = table.getTuple(i);
                if(i==0){
                    arr[0].addTuple(tuple[0]);
                    arr[1].addTuple(tuple[0]);
                }
                else if(i<= (int)rows*0.67){
                    arr[0].addTuple(tuple[1]);
                }else{
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

    protected DataTable[] partitionSetOnAttributeValue(String columnLabel, DataTable dataSet){

        //Find unique column values
        ArrayList<String > column = dataSet.getColumn(dataSet,columnLabel);
        int index=0;
        for(int i =0; i<dataSet.table.size(); i++){
            String  col = column.get(0);
            ArrayList<String > tab =  dataSet.table.get(i);
            if(column.get(0)==tab.get(0)){
                index=i;
                break;
            }
        }

        ArrayList<String > uniqueVals = getUniqueValsFromColumn(table.get(index));
        DataTable[] partitionedTable = new DataTable[uniqueVals.size()];
        for(int i = 0;i<partitionedTable.length;i++){
            partitionedTable[i]=new DataTable();
        }

        //for each partition, remove unvalid tuples
        for(int i = 0; i<partitionedTable.length; i++){
            //   partitionedSets[i] = (DataTable) dataSet;
            ArrayList<ArrayList<String >> table =  dataSet.table;
            int colSize = table.size();
            ArrayList<ArrayList<String >> clonedTable= new ArrayList<>();

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
                    String  val = table.get(j).get(k);
                    String  uniqueVal = uniqueVals.get(i);
                    if(val==uniqueVal){
                        //add tuple to the partition i
                        for(int l=0; l<table.size();l++){
                            String element = table.get(l).get(k);
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

            ArrayList<String > columnToRemove = partitionedTable[j].getColumn(dataSet,columnLabel);
            index=0;
            for(int i =0; i<dataSet.table.size(); i++){
                String  col = columnToRemove.get(0);
                ArrayList<String > tab =  dataSet.table.get(i);
                if(columnToRemove.get(0)==tab.get(0)){
                    index=i;
                    break;
                }
            }
            partitionedTable[j].table.remove(index);
        }

        return partitionedTable;
    }


    protected void loadExampleData(){
        ArrayList<String> tuple = new ArrayList<>();
        tuple.add("age");
        tuple.add("income");
        tuple.add("student");
        tuple.add("credit_rating");
        tuple.add("class");
        addTuple(tuple);
        tuple.clear();
        tuple.add("youth");
        tuple.add("high");
        tuple.add("no");
        tuple.add("fair");
        tuple.add("no");
        addTuple(tuple);
        tuple.clear();

        tuple.add("youth");
        tuple.add("high");
        tuple.add("no");
        tuple.add("excellent");
        tuple.add("no");
        addTuple(tuple);
        tuple.clear();

        tuple.add("senior");
        tuple.add("low");
        tuple.add("yes");
        tuple.add("excellent");
        tuple.add("no");
        addTuple(tuple);
        tuple.clear();

        tuple.add("middle_aged");
        tuple.add("high");
        tuple.add("no");
        tuple.add("fair");
        tuple.add("yes");
        addTuple(tuple);
        tuple.clear();

        tuple.add("senior");
        tuple.add("medium");
        tuple.add("no");
        tuple.add("fair");
        tuple.add("yes");
        addTuple(tuple);
        tuple.clear();

        tuple.add("senior");
        tuple.add("low");
        tuple.add("yes");
        tuple.add("fair");
        tuple.add("yes");
        addTuple(tuple);
        tuple.clear();



        tuple.add("middle_aged");
        tuple.add("low");
        tuple.add("yes");
        tuple.add("excellent");
        tuple.add("yes");
        addTuple(tuple);
        tuple.clear();

        tuple.add("youth");
        tuple.add("medium");
        tuple.add("yes");
        tuple.add("fair");
        tuple.add("no");
        addTuple(tuple);
        tuple.clear();

        tuple.add("youth");
        tuple.add("low");
        tuple.add("student");
        tuple.add("fair");
        tuple.add("yes");
        addTuple(tuple);
        tuple.clear();

        tuple.add("senior");
        tuple.add("medium");
        tuple.add("student");
        tuple.add("fair");
        tuple.add("yes");
        addTuple(tuple);
        tuple.clear();

        tuple.add("youth");
        tuple.add("medium");
        tuple.add("student");
        tuple.add("excellent");
        tuple.add("yes");
        addTuple(tuple);
        tuple.clear();

        tuple.add("middle_aged");
        tuple.add("medium");
        tuple.add("no");
        tuple.add("excellent");
        tuple.add("yes");
        addTuple(tuple);
        tuple.clear();

        tuple.add("middle_aged");
        tuple.add("high");
        tuple.add("yes");
        tuple.add("fair");
        tuple.add("yes");
        addTuple(tuple);
        tuple.clear();

        tuple.add("senior");
        tuple.add("medium");
        tuple.add("no");
        tuple.add("excellent");
        tuple.add("no");
        addTuple(tuple);
        tuple.clear();

    }

    public void printTable(){
        StringBuilder sb = new StringBuilder();
        int x =0;
        int y=0;
        int rows = getRowSize();
        int cols = getColSize();
        if(cols>20){
            cols=20;
        }

        sb.append("\b\b}\nrow size: "+rows+", column size:"+cols+"\n");

        while(true){
            sb.append(String.format("%-25s",table.get(x).get(y)));
            x++;
            if(x==rows && y<cols){
                x=0;
                sb.append("\n");

                y++;
                if(y==cols)
                    break;
            }
        }
        System.out.println(sb.toString()+"\n\t\trows: "+table.get(0).size()+"\tcols:"+rows);

    }



    protected int getRowSize() {
        return table.size();
    }
    protected int getColSize() {
        return table.get(0).size();
    }

    public static DataTable partition(DataTable dataSet, String selectedAttribute, String attributeValue){

        if(Utilities.LOG){
            System.out.println("<<<in partition>>> input: "+selectedAttribute+">>>");

        }
        DataTable partition = new DataTable();

        ArrayList<String> column = dataSet.getColumn(dataSet,selectedAttribute);
        ArrayList<String>[] tuple = dataSet.getTuple(0);

        partition.addTuple(tuple[0]);
        for(int i = 1;i<column.size();i++){

            if(column.get(i)==attributeValue){
                tuple= dataSet.getTuple(i);
                partition.addTuple(tuple[1]);
            }
        }

        int index=0;
        for(int i =0;i<partition.table.size();i++){
            if(partition.table.get(i).get(0).equalsIgnoreCase(selectedAttribute)){
                index=i;
                break;
            }
        }
        partition.table.remove(index);

        if(Utilities.LOG)
            System.out.println("<<<in partition>>> output\n<");
        return partition;
    }

    public static void main(String[] args) {
        new DataTable().test();
    }

    private void test() {
        DataTable table = new DataTable();
        table.loadExampleData();
        table.printTable();
    }

    public Attribute generateAttributeList() {
        LinkedList<String> list = new LinkedList<>();
        for(int i = 0;i<table.size();i++){
            list.add(table.get(i).get(0));
        }
        list.remove(list.size()-1);
        return new Attribute("",list);
    }
}
