package pacman.controllers.id3Controller;

import dataRecording.DataTuple;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.*;

import static pacman.game.Constants.MOVE.*;

/**AI controller based on ID3 decision tree algorithm.
 *
 * Created by: Patrik Lind, 17-03-2018
 *TODO: add a save tree function in order to skip building the tree every time the game is started
 * @param
 */
public class MsPacmanID3 extends Controller<Constants.MOVE>{


    private DataTable dataSet;
    private LinkedList<DataTuple.DiscreteTag> attributeList;
    private AttributeSelector selectionMethod;

    private boolean treeGenerated = false;
    private Node tree;
    private int nodeCount =0;


    /**Instantiate the controller, builds a data table on raw data, builds the decision tree on the data table.
     *
     */
    public MsPacmanID3(){
        dataSet = new DataTable();
        dataSet.loadRecordedData();
        DataTable[] splitTables = dataSet.splitTableForHoldout(dataSet);
        attributeList = dataSet.getAttributeList();

        selectionMethod = new AttributeSelector();
        tree = new Node();
        try {
            tree = tree.generateTree(splitTables[0],attributeList);
            treeGenerated =true;
            getAccuracyOfTree(splitTables[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //  Utilities.createGraph(tree);

    }



    /**Classifies the test data in order to calculate the accuracy of the tree.
     *
     * @param splittable : DataTable<T>
     * @return acc : double
     */
    private void getAccuracyOfTree(DataTable splittable) throws Exception {
        ArrayList<DataTuple.DiscreteTag> classifiedAs = new ArrayList<>();
        ArrayList<DataTuple.DiscreteTag> classColumn = splittable.getColumn(splittable, DataTuple.DiscreteTag.CLASS);

        splittable.table.remove(splittable.table.size()-1); //Remove the classifier column
        int size = classColumn.size();
        for(int i =1;i<size;i++){
            ArrayList<DataTuple.DiscreteTag>[] tuple =  splittable.getTuple(i);
            if(tuple!=null){
                classifiedAs.add(classifyTuple(tuple));
            }
        }

        int rightClass = 0;
        for(int i = 0; i<classifiedAs.size();i++){
            if(classifiedAs.get(i)==classColumn.get(i+1))
                rightClass++;
        }
        double accuracy =(double) rightClass/(size-1);
        System.out.println("Accuracy of tree: "+accuracy);

    }

    /**Classifies the input parameter and returns the class value. The input parameter consist of two lists where the
     * first holds the column names and the second holds the values in the column
     *
     * @param tuple : Double[ArrayList<DiscreteTag>][ArrayList<DiscreteTag>]
     * @return
     */
    private DataTuple.DiscreteTag classifyTuple(ArrayList<DataTuple.DiscreteTag>[] tuple){
        DataTuple.DiscreteTag classifiedAs = null;

        LinkedList<Node> stack = new LinkedList<>();
        stack.add(tree);
        while (!stack.isEmpty()){
            Node currentNode = stack.removeFirst();
            if(currentNode.isLeaf){
                classifiedAs = currentNode.label;
            }else{
                DataTuple.DiscreteTag nodeLabel = currentNode.label;
                //get value from tuple where col = label
                int index = tuple[0].indexOf(nodeLabel);
                DataTuple.DiscreteTag tupleValue= tuple[1].get(index);

                for(Node child : currentNode.children){
                    DataTuple.DiscreteTag edge = child.edge;
                    if (edge==tupleValue){
                        stack.addLast(child);
                    }
                }

            }
        }

        return classifiedAs;
    }

    /**A recursive alternative to above method.
     * TODO: acess the correctness of this method.
     *
     * @param tuple
     * @param node
     * @return
     */
    private DataTuple.DiscreteTag classify(ArrayList<DataTuple.DiscreteTag>[] tuple, Node node) {
        DataTuple.DiscreteTag toReturn = null;

        if(node.isLeaf)

            return node.label;
        else{
            DataTuple.DiscreteTag label = node.label;
            //get value from tuple where col = label
            int index = tuple[0].indexOf(label);
            DataTuple.DiscreteTag value = tuple[1].get(index);

            DataTuple.DiscreteTag val = null;
            //check which child node has edge = value
            for(int i = 0; i<node.children.size();i++) {

                if (node.children.get(i).edge == value) {
                    toReturn =  classify(tuple, node.children.get(i));
                    break;
                }
            }
            return toReturn;
        }
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {

        //create the data structure accepted by the classifyTuple(...)
        ArrayList<DataTuple.DiscreteTag>[] tuple = new ArrayList[2];
        ArrayList<DataTuple.DiscreteTag> columns = new ArrayList<>();
        ArrayList<DataTuple.DiscreteTag> vals= new ArrayList<>();

        /*Create headers for the tuple. Need to correspond to the headers in DataTable's loadRecordedData()
        method, CLASS column excluded*/
        columns.add(DataTuple.DiscreteTag.PILL_DISTANCE);
        columns.add(DataTuple.DiscreteTag.DIRECTION_TO_PILL);
        columns.add(DataTuple.DiscreteTag.GHOST_DISTANCE);
        columns.add(DataTuple.DiscreteTag.GHOST_DIRECTION);
        columns.add(DataTuple.DiscreteTag.GHOST_EDIBLE);

        //Get values for closest ghost
        GhostValues ghostValues = closestGhost(game);
        DataTuple.DiscreteTag closestGhostDistance = ghostValues.distanceTag;
        DataTuple.DiscreteTag closestGhostDirection = ghostValues.ghostDirection;
        DataTuple.DiscreteTag ghostEdible = ghostValues.ghostEdible;

        //get values for closest pill
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int pillIndex = game.getClosestNodeIndexFromNodeIndex(pacmanIndex,
                game.getActivePillsIndices(), Constants.DM.PATH);
        int pillDist = game.getShortestPathDistance(pacmanIndex, pillIndex);
        Constants.MOVE pillMove = game.getNextMoveTowardsTarget(pacmanIndex,
                pillIndex, Constants.DM.PATH);

        DataTuple.DiscreteTag closestPillDistance = dataSet.pillDistance(pillDist);
        DataTuple.DiscreteTag directionToPill = dataSet.parseMoveToPill(pillMove);



        //Add vals to tuple, need to have as many as headers
        vals.add(closestPillDistance);
        vals.add(directionToPill);
        vals.add(closestGhostDistance);
        vals.add(closestGhostDirection);
        vals.add(ghostEdible);

        tuple[0] = columns;
        tuple[1] = vals;


        DataTuple.DiscreteTag move =  classifyTuple(tuple);
        //DataTuple.DiscreteTag move =  classifyTuple(tuple);
        Constants.MOVE returnMove = null;
        if(move == DataTuple.DiscreteTag.CLASS_UP)
            returnMove = UP;
        if(move == DataTuple.DiscreteTag.CLASS_DOWN)
            returnMove = DOWN;
        if(move == DataTuple.DiscreteTag.CLASS_LEFT)
            returnMove  = LEFT;
        if(move == DataTuple.DiscreteTag.CLASS_RIGHT)
            returnMove = RIGHT;

        if(Utilities.log){
            System.out.println("\n--->Closest ghost: "+ghostValues.toString());
            System.out.println("\t===>Closest pill distance: "+closestPillDistance+", direction to: "+directionToPill);
            System.out.println("\t\t>>>>>>> Classified as:" +move.toString());
        }

        return returnMove;

    }

    /**Calculates the closest ghost its values: distance, direction, isEdible. Creates and returns a pojo object holding
     * the values.
     *
     * @param game : Game
     * @return  ghost : GhostValues()
     */
    private GhostValues closestGhost(Game game){
        ArrayList<GhostValues> ghosts = new ArrayList<>();
        int[] distances = new int[4];
        ArrayList<DataTuple.DiscreteTag> moves = new ArrayList();
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY);
        distances[0] = game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        moves.add(dataSet.parseMoveGhost( game.getGhostLastMoveMade(Constants.GHOST.BLINKY)));
        ghosts.add(new GhostValues(distances[0], Constants.GHOST.BLINKY,moves.get(0),game.isGhostEdible(Constants.GHOST.BLINKY)));


        ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.INKY);
        distances[1] = game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        moves.add( dataSet.parseMoveGhost( game.getGhostLastMoveMade(Constants.GHOST.INKY)));
        ghosts.add(new GhostValues(distances[1], Constants.GHOST.INKY,moves.get(1), game.isGhostEdible(Constants.GHOST.INKY)));


        ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.PINKY);
        distances[2]= game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        moves.add( dataSet.parseMoveGhost( game.getGhostLastMoveMade(Constants.GHOST.PINKY)));
        ghosts.add(new GhostValues(distances[2], Constants.GHOST.PINKY,moves.get(2), game.isGhostEdible(Constants.GHOST.PINKY)));

        ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.SUE);
        distances[3]= game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        moves.add( dataSet.parseMoveGhost( game.getGhostLastMoveMade(Constants.GHOST.SUE)));
        ghosts.add(new GhostValues(distances[3], Constants.GHOST.SUE,moves.get(3), game.isGhostEdible(Constants.GHOST.SUE)));

        //sort ghosts
        Collections.sort(ghosts, new Comparator<GhostValues>() {

            @Override
            public int compare(GhostValues o1, GhostValues o2) {
                return Integer.compare(o1.distance, o2.distance);
            }
        });

        return ghosts.get(0);
    }


    /**The node object on which the tree is being build with.
     */
    private class Node{

        protected DataTuple.DiscreteTag label, edge;
        protected boolean isLeaf = false;
        protected Node parent = null;
        protected ArrayList<Node> children = new ArrayList<>();

        /**A recursive method to generate a decision tree. Input parameters are the data table on which the tree builds
         * itself upon and a list with attributes on which it decides the best attribute to select for the next node. The
         * selection is based on ID3.
         *
         * @param dataSet : DataTable
         * @param attributeList : LinkedList<T>
         * @return
         */
        private Node generateTree(DataTable dataSet, LinkedList<DataTuple.DiscreteTag> attributeList) throws Exception {
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
                DataTuple.DiscreteTag  attribute = selectionMethod.id3(dataSet,attributeList);
                node.label=attribute;
                attributeList.remove(attribute);

                //label edges
                ArrayList<DataTuple.DiscreteTag> column = dataSet.getColumn(dataSet,attribute);
                ArrayList<DataTuple.DiscreteTag> edges = dataSet.getUniqueValsFromColumn(column);
                //Partition table based on value of attribute T
                DataTable[] partitionedSets = dataSet.partitionSetOnAttributeValue(attribute, dataSet);


                for(int i = 0; i<partitionedSets.length;i++){
                    if(partitionedSets[i].table.isEmpty()){
                        Node child = new Node();
                        child.parent=node;
                        child.edge=edges.get(i);
                        DataTuple.DiscreteTag classValue =  dataSet.majorityClassValue();
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

    /**POJO to store ghost values.
     *
     */
    private class GhostValues{
        DataTuple.DiscreteTag distanceTag;
        DataTuple.DiscreteTag ghostDirection;
        DataTuple.DiscreteTag ghostEdible;
        int distance;
        Constants.GHOST ghost;


        /**Instantiates this object and discretize the values.
         *  @param distance : int
         * @param ghost : Constants.Ghost
         * @param ghostDirections : DiscreteTag
         * @param ghostEdible : boolean
         */
        public GhostValues(int distance, Constants.GHOST ghost, DataTuple.DiscreteTag ghostDirections, boolean ghostEdible) {
            if(distance<0)
                distance=Integer.MAX_VALUE;
            this.distance= distance;
            distanceTag = dataSet.ghostDistance(distance);
            this.ghost = ghost;
            if(ghostDirections == DataTuple.DiscreteTag.GHOST_MOVE_NEUTRAL)
                this.ghostDirection= DataTuple.DiscreteTag.GHOST_MOVE_RIGHT;
            else
                this.ghostDirection = ghostDirections;
            if(ghostEdible)
                this.ghostEdible = DataTuple.DiscreteTag.EDIBLE_TRUE;
            else
                this.ghostEdible = DataTuple.DiscreteTag.EDIBLE_FALSE;

        }

        public String toString(){
            return ghost+", distance: "+distance+" = "+distanceTag+", move: "+ghostDirection;

        }

    }

    //Temp to debug
    public static void main(String[] args) {
        new MsPacmanID3();

    }
}
