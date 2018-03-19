package pacman.controllers.id3Controller;

import dataRecording.DataTuple;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.*;

import static pacman.game.Constants.MOVE.*;

/**AI controller based on ID3 decition tree algorithm
 *
 * Created by: Patrik Lind, 17-03-2018
 *
 * @param <T>
 */
public class MsPacmanID3  <T> extends Controller<Constants.MOVE>{

    private class GhostValues{
        T distanceTag;
        T ghostDirection;
        int distance;
        Constants.GHOST ghost;


        /**
         *
         * @param distance
         * @param ghost
         * @param ghostDirections
         */
        public GhostValues(int distance, Constants.GHOST ghost, T ghostDirections) {
            if(distance<0)
                distance=Integer.MAX_VALUE;
            this.distance= distance;
            distanceTag = (T) dataSet.ghostDistance(distance);
            this.ghost = ghost;
            this.ghostDirection = ghostDirections;
        }

        public String toString(){
            return ghost+", distance: "+distance+" = "+distanceTag+", move: "+ghostDirection;

        }

    }
    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {


        ArrayList<T>[] tuple = new ArrayList[2];
        ArrayList<T> columns = new ArrayList<>();
        ArrayList<T> vals= new ArrayList<>();

        //Create headers for the tuple
        columns.add((T) DataTuple.DiscreteTag.GHOST_DISTANCE);
        columns.add((T) DataTuple.DiscreteTag.GHOST_DIRECTION);
        columns.add((T) DataTuple.DiscreteTag.PILL_DISTANCE);
        columns.add((T) DataTuple.DiscreteTag.DIRECTION_TO_PILL);

        GhostValues ghostValues = closestGhost(game);
        System.out.println("\n\t\t--->Closest ghost: "+ghostValues.toString());
        T closestGhostDistance = ghostValues.distanceTag;
        T closestGhostDirection = (T) ghostValues.ghostDirection;


        //TODO debugg pillDistances- probably wrong dist/move
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int pillIndex = game.getClosestNodeIndexFromNodeIndex(pacmanIndex,
                game.getActivePillsIndices(), Constants.DM.PATH);
        int pillDist = game.getShortestPathDistance(pacmanIndex, pillIndex);
        Constants.MOVE pillMove = game.getNextMoveTowardsTarget(pacmanIndex,
                pillIndex, Constants.DM.PATH);

        vals.add(closestGhostDistance);
        vals.add(closestGhostDirection);
        vals.add((T) dataSet.pillDistance(pillDist));
        vals.add((T) dataSet.parseMove(pillMove));

        tuple[0] = columns;
        tuple[1] = vals;
    //    dataSet.printTuple(tuple);

        T move = (T) classify(tuple,tree,timeDue);
        System.out.println("\t\t>>>>>>> Classified as:" +move.toString());
        Constants.MOVE returnMove = null;
        if(move == DataTuple.DiscreteTag.UP)
            returnMove = UP;
        if(move == DataTuple.DiscreteTag.DOWN)
            returnMove = DOWN;
        if(move == DataTuple.DiscreteTag.LEFT)
            returnMove  = LEFT;
        if(move == DataTuple.DiscreteTag.RIGHT)
            returnMove = RIGHT;

        return Constants.MOVE.LEFT;
        //return returnMove;

    }

    private GhostValues closestGhost(Game game){
        ArrayList<GhostValues> ghosts = new ArrayList<>();
        int[] distances = new int[4];
        ArrayList<T> moves = new ArrayList();
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY);
        distances[0] = game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        moves.add((T) dataSet.parseMove( game.getGhostLastMoveMade(Constants.GHOST.BLINKY)));
        ghosts.add(new GhostValues(distances[0], Constants.GHOST.BLINKY,moves.get(0)));

        ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.INKY);
        distances[1] = game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        moves.add((T) dataSet.parseMove( game.getGhostLastMoveMade(Constants.GHOST.INKY)));
        ghosts.add(new GhostValues(distances[1], Constants.GHOST.INKY,moves.get(1)));


        ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.PINKY);
        distances[2]= game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        moves.add((T) dataSet.parseMove( game.getGhostLastMoveMade(Constants.GHOST.PINKY)));
        ghosts.add(new GhostValues(distances[2], Constants.GHOST.PINKY,moves.get(2)));

        ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.SUE);
        distances[3]= game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        moves.add((T) dataSet.parseMove( game.getGhostLastMoveMade(Constants.GHOST.SUE)));
        ghosts.add(new GhostValues(distances[3], Constants.GHOST.SUE,moves.get(3)));

        for (int i = 0;i<ghosts.size();i++){
            System.out.println(ghosts.get(i).toString());
        }

        //sort ghosts
        Collections.sort(ghosts, new Comparator<GhostValues>() {

            @Override
            public int compare(GhostValues o1, GhostValues o2) {
                return Integer.compare(o1.distance, o2.distance);
            }
        });

        return ghosts.get(0);
    }


    private DataTable dataSet;
    private LinkedList<T> attributeList;
    private AttributeSelection selectionMethod;

    private boolean gameOff = false;
    private Node tree;
    private int nodeCount =0;


    public MsPacmanID3(){
        dataSet = new DataTable();
        //dataSet.loadExampleData(); //Hard coded example data in class DataTable
        dataSet.loadRecordedData();
        DataTable<T>[] splitTables = dataSet.splitTableForHoldout(dataSet);
        attributeList = dataSet.getAttributeList();
       // dataSet = splitTables[0];
        selectionMethod = new AttributeSelection();
        tree = new Node().generateTree(splitTables[0],attributeList);
        System.out.println("### TREE BUILT\n\tAccuracy of tree:  "+getAccuracyOfTree(splitTables[1]));
        gameOff =true;
        //  Utilities.createGraph(tree);

    }

    /**Classifies the test data in order to calculate the accuracy of the tree.
     *
     * @param splittable : DataTable<T>
     * @return acc : double
     */
    private double getAccuracyOfTree(DataTable<T> splittable) {
        ArrayList<T> classifiedAs = new ArrayList<>();
        ArrayList<T> classColumn = splittable.getColumn(splittable, (T) DataTuple.DiscreteTag.CLASS);

        splittable.table.remove(splittable.table.size()-1); //Remove the classifier column
        int size = classColumn.size();
        for(int i =1;i<size;i++){
            ArrayList<T>[] tuple =  splittable.getTuple(i);
            if(tuple!=null){
                classifiedAs.add((T)  classify(tuple,tree,0));
            }
        }

        int rightClass = 0;
        for(int i = 0; i<classifiedAs.size();i++){
            if(classifiedAs.get(i)==classColumn.get(i+1))
                rightClass++;
        }
        return (double) rightClass/(size-1); //-1 to exclude column name
    }


    public T classify(ArrayList<T>[] tuple, Node<T> node, long timeDue) {
        T toReturn = null;

        if(node.isLeaf)

            return (T) node.label;
        else{
            T label = (T) node.label;
            //get value from tuple where col = label
            int index = tuple[0].indexOf(label);
            T value = tuple[1].get(index);

            //check which child node has edge = value
            for(int i = 0; i<node.children.size();i++){
                long currentTime = System.currentTimeMillis();
                /*if( !gameOff && currentTime>=timeDue){
                    System.out.println("\t\t>>>>>time limit reached, returned: "+toReturn.toString());
                    break;
                }*/
                if(node.children.get(i).edge==value){
                   toReturn= (T) classify(tuple, node.children.get(i), timeDue);
                }
            }
            return toReturn;
        }
    }

    /**Assert the accuracy of the try by performing 'Holdout'. The data table is split in two parts
     * TODO
     * @param trainData : DataSet
     * @return accuracy : double
     */

    /**Class for creating the tree
     *
     * @param <T>
     */
    public class Node<T>{

        protected T label, edge; //TODO fix edge for viewing the tree
        protected boolean isLeaf = false;
        protected Node parent = null;
        protected ArrayList<Node> children = new ArrayList<>();

        /**
         *
         * @param dataSet : DataTable
         * @param attributeList : LinkedList<T>
         * @return
         */
        private Node generateTree(DataTable dataSet, LinkedList<T> attributeList){
            System.out.println("### in generateTree ###");
            Node node = new Node();

            nodeCount++;

            if(dataSet.everyTupleInSameClass()){
                node.isLeaf=true;
                node.label= dataSet.getClassLabel(1); // at index 0 is column header value
                System.out.println("\t Every in same class: "+node.label.toString());
                return node;
            }
            else if(attributeList.isEmpty()){
                node.isLeaf = true;
                node.label = dataSet.majorityClassValue();

                System.out.println("\t Attribute list is empty: "+node.label.toString());
                return node;
            }else{
                T  attribute = (T) selectionMethod.id3(dataSet,attributeList);
                node.label=attribute;
                attributeList.remove(attribute);

                //label edges
                ArrayList<T> column = dataSet.getColumn(dataSet,attribute);
                ArrayList<T> edges = dataSet.getUniqueValsFromColumn(column);
                //Partition table based on value of attribute T
                DataTable[] partitionedSets = dataSet.partitionSetOnAttributeValue(attribute, dataSet);


                for(int i = 0; i<partitionedSets.length;i++){
                    if(partitionedSets[i].table.isEmpty()){
                        Node child = new Node();
                        child.parent=node;
                        child.edge=edges.get(i);
                        T classValue = (T) dataSet.majorityClassValue();
                        child.label=classValue;
                        child.isLeaf=true;
                        node.children.add(child);

                    }else {
                        Node child = generateTree(partitionedSets[i],attributeList);
                        child.parent=node;
                        child.edge=edges.get(i);
                        node.children.add(child);

                    }

                }

                System.out.println();
                return node;
            }

        }

    }

    //Temp to debug
    public static void main(String[] args) {
        new MsPacmanID3();

    }
}
