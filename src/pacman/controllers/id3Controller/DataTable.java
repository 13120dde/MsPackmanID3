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



    //All the discrete values that can be stored in the table, both values and column names.
    protected enum DiscreteValues {
        //temp values for testing
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


        //Column names
        PILLS_IN_LEVEL,
        PILLS_LEFT,
        POWER_PILLS_IN_LEVEL,
        POWER_PILLS_LEFT,


        BLINKY_EDIBLE,
        BLINKY_DISTANCE,
        BLINKY_DIRECTION,
        INKY_EDIBLE,
        INKY_DISTANCE,
        INKY_DIRECTION,
        PINKY_EDIBLE,
        PINKY_DISTANCE,
        PINKY_DIRECTION,
        SUE_EDIBLE,
        SUE_DISTANCE,
        SUE_DIRECTION,

        CLASS,

        //Values
        TRUE,
        FALSE,
        UP,
        DOWN,
        LEFT,
        RIGHT


    }

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
    protected  <T> ArrayList<T>[] getTuple(Game game) {

        ArrayList<T>[] tuple = new ArrayList[2];
        ArrayList<T> columns = new ArrayList<>();
        ArrayList<T> vals= new ArrayList<>();

        columns.add((T) DiscreteValues.PILLS_IN_LEVEL);
        columns.add((T) DiscreteValues.PILLS_LEFT);
        columns.add((T) DiscreteValues.POWER_PILLS_IN_LEVEL);
        columns.add((T) DiscreteValues.POWER_PILLS_LEFT);
        columns.add((T) DiscreteValues.BLINKY_EDIBLE);
        columns.add((T) DiscreteValues.BLINKY_DISTANCE);
        columns.add((T) DiscreteValues.BLINKY_DIRECTION);
        columns.add((T) DiscreteValues.INKY_EDIBLE);
        columns.add((T) DiscreteValues.INKY_DISTANCE);
        columns.add((T) DiscreteValues.INKY_DIRECTION);
        columns.add((T) DiscreteValues.PINKY_EDIBLE);
        columns.add((T) DiscreteValues.PINKY_DISTANCE);
        columns.add((T) DiscreteValues.PINKY_DIRECTION);
        columns.add((T) DiscreteValues.SUE_EDIBLE);
        columns.add((T) DiscreteValues.SUE_DISTANCE);
        columns.add((T) DiscreteValues.SUE_DIRECTION);
        columns.add((T) DiscreteValues.CLASS);         //MUST BE LAST!!
        DataTuple t = new DataTuple(game,Constants.MOVE.UP);

       vals.add((T) t.discretizeNumberOfPills(game.getNumberOfPills()));
       vals.add((T) t.discretizeNumberOfPills(game.getNumberOfActivePills()));
       vals.add((T) t.discretizeNumberOfPills(game.getNumberOfPowerPills()));
       vals.add((T) t.discretizeNumberOfPills(game.getNumberOfActivePowerPills()));

       //BLINKY
       if(game.isGhostEdible(Constants.GHOST.BLINKY))
           vals.add((T) DiscreteValues.TRUE);
       else
           vals.add((T) DiscreteValues.FALSE);
       vals.add(
               (T) t.discretizeDistance(
                       game.getShortestPathDistance(
                               game.getPacmanCurrentNodeIndex()
                               ,game.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY)))
       );
       vals.add((T) parseMove(game.getGhostLastMoveMade(Constants.GHOST.BLINKY)));

        //INKY
        if(game.isGhostEdible(Constants.GHOST.INKY))
            vals.add((T) DiscreteValues.TRUE);
        else
            vals.add((T) DiscreteValues.FALSE);
        vals.add(
                (T) t.discretizeDistance(
                        game.getShortestPathDistance(
                                game.getPacmanCurrentNodeIndex()
                                ,game.getGhostCurrentNodeIndex(Constants.GHOST.INKY)))
        );
        vals.add((T) parseMove(game.getGhostLastMoveMade(Constants.GHOST.INKY)));

        //PINKY
        if(game.isGhostEdible(Constants.GHOST.PINKY))
            vals.add((T) DiscreteValues.TRUE);
        else
            vals.add((T) DiscreteValues.FALSE);
        vals.add(
                (T) t.discretizeDistance(
                        game.getShortestPathDistance(
                                game.getPacmanCurrentNodeIndex()
                                ,game.getGhostCurrentNodeIndex(Constants.GHOST.PINKY)))
        );
        vals.add((T) parseMove(game.getGhostLastMoveMade(Constants.GHOST.PINKY)));

        //SUE
        if(game.isGhostEdible(Constants.GHOST.SUE))
            vals.add((T) DiscreteValues.TRUE);
        else
            vals.add((T) DiscreteValues.FALSE);
        vals.add(
                (T) t.discretizeDistance(
                        game.getShortestPathDistance(
                                game.getPacmanCurrentNodeIndex()
                                ,game.getGhostCurrentNodeIndex(Constants.GHOST.SUE)))
        );
        vals.add((T) parseMove(game.getGhostLastMoveMade(Constants.GHOST.SUE)));

        vals.add((T) DiscreteValues.DOWN); //dummy value



        game.getNumberOfPowerPills();
        game.getNumberOfActivePowerPills();

        tuple[0] = columns;
        tuple[1] = vals;

        System.out.println();
        return tuple;
    }

    protected void loadRecordedData() {
        DataTuple[] pacManData= DataSaverLoader.LoadPacManData();
        //Create headers for columns
        ArrayList<T> tuple = new ArrayList<>();
        tuple.add((T) DiscreteValues.PILLS_IN_LEVEL);
        tuple.add((T) DiscreteValues.PILLS_LEFT);
        tuple.add((T) DiscreteValues.POWER_PILLS_IN_LEVEL);
        tuple.add((T) DiscreteValues.POWER_PILLS_LEFT);
        tuple.add((T) DiscreteValues.BLINKY_EDIBLE);
        tuple.add((T) DiscreteValues.BLINKY_DISTANCE);
        tuple.add((T) DiscreteValues.BLINKY_DIRECTION);
        tuple.add((T) DiscreteValues.INKY_EDIBLE);
        tuple.add((T) DiscreteValues.INKY_DISTANCE);
        tuple.add((T) DiscreteValues.INKY_DIRECTION);
        tuple.add((T) DiscreteValues.PINKY_EDIBLE);
        tuple.add((T) DiscreteValues.PINKY_DISTANCE);
        tuple.add((T) DiscreteValues.PINKY_DIRECTION);
        tuple.add((T) DiscreteValues.SUE_EDIBLE);
        tuple.add((T) DiscreteValues.SUE_DISTANCE);
        tuple.add((T) DiscreteValues.SUE_DIRECTION);
        tuple.add((T) DiscreteValues.CLASS);        //MUST BE LAST!!


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
            T data = (T) pacManData[i].discretizeNumberOfPills(pacManData[i].numberOfTotalPillsInLevel);
            tuple.add(data);
            data = (T) pacManData[i].discretizeNumberOfPills(pacManData[i].numOfPillsLeft);
            tuple.add(data);
            data = (T) pacManData[i].discretizeNumberOfPills(pacManData[i].numberOfTotalPowerPillsInLevel);
            tuple.add(data);
            data = (T) pacManData[i].discretizeNumberOfPills(pacManData[i].numOfPowerPillsLeft);
            tuple.add(data);

            //BLINKY
            if(pacManData[i].isBlinkyEdible)
                data = (T) DiscreteValues.TRUE;
            else
                data = (T) DiscreteValues.FALSE;
            tuple.add(data);
            data = (T) pacManData[i].discretizeDistance(pacManData[i].blinkyDist);
            tuple.add(data);
            data = parseMove(pacManData[i].blinkyDir);
            tuple.add(data);

            //INKY
            if(pacManData[i].isInkyEdible)
                data = (T) DiscreteValues.TRUE;
            else
                data = (T) DiscreteValues.FALSE;
            tuple.add(data);
            data = (T) pacManData[i].discretizeDistance(pacManData[i].inkyDist);
            tuple.add(data);
            data = parseMove(pacManData[i].inkyDir) ;
            tuple.add(data);

            //PINKY
            if(pacManData[i].isPinkyEdible)
                data = (T) DiscreteValues.TRUE;
            else
                data = (T) DiscreteValues.FALSE;
            tuple.add(data);
            data = (T) pacManData[i].discretizeDistance(pacManData[i].pinkyDist);
            tuple.add(data);
            data = parseMove(pacManData[i].pinkyDir);
            tuple.add(data);

            //INKY
            if(pacManData[i].isSueEdible)
                data = (T) DiscreteValues.TRUE;
            else
                data = (T) DiscreteValues.FALSE;
            tuple.add(data);
            data = (T) pacManData[i].discretizeDistance(pacManData[i].sueDist);
            tuple.add(data);
            data =  parseMove(pacManData[i].sueDir);
            tuple.add(data);

            //Class last
            data  = parseMove(pacManData[i].DirectionChosen);
            tuple.add(data);

            addTuple(tuple);
        }

    }

    /**Parse the input to enum of the type of this class and returns it as T.
     *
     * @param directionChosen : Constants.MOVE
     * @return direction : T
     */
    private T parseMove(Constants.MOVE directionChosen) {
        final String s = directionChosen.name().toUpperCase();
        T direction= null;
        switch (s){
            case "UP":
                direction = (T) DiscreteValues.UP;
                break;
            case "DOWN":
                direction = (T) DiscreteValues.DOWN;
                break;
            case "LEFT":
                direction = (T) DiscreteValues.LEFT;
                break;
            case "RIGHT":
                direction = (T) DiscreteValues.RIGHT;
                break;
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
        tuple =getTuple(new Game());
        DataTable<T>[] tables = splitTableForHoldout(this);
        //T label = getClassLabel(13);
     //   ArrayList<T> col = getColumn(this, (T) DiscreteValues.CREDIT_RATING);
       // DataTable[] tables =  partitionSetOnAttributeValue((T) DiscreteValues.INCOME,this);

    }



    //Temp for testing
    protected void loadExampleData() {
        ArrayList a1 = new ArrayList();
        a1.add(DiscreteValues.AGE);
        a1.add(DiscreteValues.YOUTH);
        a1.add(DiscreteValues.YOUTH);
        a1.add(DiscreteValues.MIDDLE_AGED);
        a1.add(DiscreteValues.SENIOR);
        a1.add(DiscreteValues.SENIOR);
        a1.add(DiscreteValues.SENIOR);
        a1.add(DiscreteValues.MIDDLE_AGED);
        a1.add(DiscreteValues.YOUTH);
        a1.add(DiscreteValues.YOUTH);
        a1.add(DiscreteValues.SENIOR);
        a1.add(DiscreteValues.MIDDLE_AGED);
        a1.add(DiscreteValues.YOUTH);
        a1.add(DiscreteValues.MIDDLE_AGED);
        a1.add(DiscreteValues.SENIOR);

        ArrayList a2 = new ArrayList();
        a2.add(DiscreteValues.INCOME);
        a2.add(DiscreteValues.HIGH);
        a2.add(DiscreteValues.HIGH);
        a2.add(DiscreteValues.HIGH);
        a2.add(DiscreteValues.MEDIUM);
        a2.add(DiscreteValues.LOW);
        a2.add(DiscreteValues.LOW);
        a2.add(DiscreteValues.LOW);
        a2.add(DiscreteValues.MEDIUM);
        a2.add(DiscreteValues.LOW);
        a2.add(DiscreteValues.MEDIUM);
        a2.add(DiscreteValues.MEDIUM);
        a2.add(DiscreteValues.MEDIUM);
        a2.add(DiscreteValues.HIGH);
        a2.add(DiscreteValues.MEDIUM);

        ArrayList a3 = new ArrayList();
        a3.add(DiscreteValues.STUDENT);
        a3.add(DiscreteValues.NO);
        a3.add(DiscreteValues.NO);
        a3.add(DiscreteValues.NO);
        a3.add(DiscreteValues.NO);
        a3.add(DiscreteValues.YES);
        a3.add(DiscreteValues.YES);
        a3.add(DiscreteValues.YES);
        a3.add(DiscreteValues.NO);
        a3.add(DiscreteValues.YES);
        a3.add(DiscreteValues.YES);
        a3.add(DiscreteValues.YES);
        a3.add(DiscreteValues.NO);
        a3.add(DiscreteValues.YES);
        a3.add(DiscreteValues.NO);

        ArrayList a4 = new ArrayList();
        a4.add(DiscreteValues.CREDIT_RATING);
        a4.add(DiscreteValues.FAIR);
        a4.add(DiscreteValues.EXCELLENT);
        a4.add(DiscreteValues.FAIR);
        a4.add(DiscreteValues.FAIR);
        a4.add(DiscreteValues.FAIR);
        a4.add(DiscreteValues.EXCELLENT);
        a4.add(DiscreteValues.EXCELLENT);
        a4.add(DiscreteValues.FAIR);
        a4.add(DiscreteValues.FAIR);
        a4.add(DiscreteValues.FAIR);
        a4.add(DiscreteValues.EXCELLENT);
        a4.add(DiscreteValues.EXCELLENT);
        a4.add(DiscreteValues.FAIR);
        a4.add(DiscreteValues.EXCELLENT);

        ArrayList a5 = new ArrayList();
        a5.add(DiscreteValues.CLASS);
        a5.add(DiscreteValues.NO);
        a5.add(DiscreteValues.NO);
        a5.add(DiscreteValues.YES);
        a5.add(DiscreteValues.YES);
        a5.add(DiscreteValues.YES);
        a5.add(DiscreteValues.NO);
        a5.add(DiscreteValues.YES);
        a5.add(DiscreteValues.NO);
        a5.add(DiscreteValues.YES);
        a5.add(DiscreteValues.YES);
        a5.add(DiscreteValues.YES);
        a5.add(DiscreteValues.YES);
        a5.add(DiscreteValues.YES);
        a5.add(DiscreteValues.NO);


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



}
