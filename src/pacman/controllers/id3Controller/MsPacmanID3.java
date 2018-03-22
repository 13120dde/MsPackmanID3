package pacman.controllers.id3Controller;

import dataRecording.DataTuple;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.*;

import static pacman.controllers.id3Controller.Utilities.LOG;
import static pacman.game.Constants.MOVE.*;

/**AI controller based on ID3 decision tree algorithm.
 *
 * Created by: Patrik Lind, 17-03-2018
 *TODO: add a save tree function in order to skip building the tree every time the game is started
 * @param
 */
public class MsPacmanID3 extends Controller<Constants.MOVE>{


    private DataTable dataSet;
    private Attribute attributeList;
    private AttributeSelector selectionMethod;

    private Node tree;
    private int nodeCount =0;


    /**Instantiate the controller, builds a data table on raw data, builds the decision tree on the data table.
     *
     */
    public MsPacmanID3(){
        dataSet = new DataTable();
        dataSet.loadRecordedData();
        //dataSet.loadExampleData();
        DataTable[] splitTables = dataSet.splitTableForHoldout(dataSet);
        attributeList = new Attribute(null,dataSet.getAttributeList());
        selectionMethod = new AttributeSelector();
        tree = new Node();
        try {
            tree = generateTree(splitTables[0],attributeList,0);
            tree.accuracy = getAccuracyOfTree(splitTables[1]);
            System.out.println("Accuracy of tree: "+tree.accuracy);
            Utilities.visualizeTree(tree);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  Utilities.createGraph(tree);

    }


    /**A recursive method to generate a decision tree. Input parameters are the data table on which the tree builds
     * itself upon and a list with attributes on which it decides the best attribute to select for the next node. The
     * selection is based on ID3.
     *
     * @param dataSet : DataTable
     * @param attributeList : LinkedList<T>
     * @return
     */
    private Node generateTree(DataTable dataSet, Attribute attributeList, int depth) throws Exception {
        System.out.println("### in generateTree ###");
        Node node = new Node();
        node.attribute=attributeList;


        nodeCount++;
        node.id =nodeCount;
        node.depthOfNode=depth;



        if(dataSet.everyTupleInSameClass()){
            node.isLeaf=true;
            node.label= dataSet.getClassLabel(1); // at index 0 is column header value
            System.out.println("\t Every in same class: "+node.label.toString());
            return node;
        }
        if(attributeList.isEmpty()){
            node.isLeaf = true;
            node.label = dataSet.majorityClassValue();

            System.out.println("\t Attribute list is empty: "+attributeList.toString());
            return node;
        }

        Attribute attribute = selectionMethod.id3(dataSet,attributeList);
        if(LOG){
            System.out.println("\t-->Selected attribute: "+attribute.toString());

        }

        node.label=attribute.selectedAttribute;
        node.attribute.list.remove(node.label);
        // attributeList.remove(attribute.selectedAttribute);

        //label edges
        ArrayList<DataTuple.DiscreteTag> column = dataSet.getColumn(dataSet,attribute.selectedAttribute);
        ArrayList<DataTuple.DiscreteTag> edges = dataSet.getUniqueValsFromColumn(column);
        //Partition table based on value of attribute T
        //  DataTable[] partitionedSets = dataSet.partitionSetOnAttributeValue(attribute.selectedAttribute, dataSet);

        for(int i = 0; i<attribute.list.size(); i++){
            DataTable partition = DataTable.partition(dataSet,attribute.selectedAttribute,attribute.list.get(i));
            depth++;

            if(partitionIsEmpty(partition)){
                Node child = new Node();
                child.parent=node;

                child.edge=edges.get(i);
                DataTuple.DiscreteTag classValue =  dataSet.majorityClassValue();
                child.label=classValue;
                child.isLeaf=true;
                node.children.add(child);

            }else {

                Node child = generateTree(partition,node.attribute,depth);
                child.parent=node;

                child.edge=edges.get(i);
                node.children.add(child);

            }
            depth--;
        }

        System.out.println();
        return node;


    }

    private boolean partitionIsEmpty(DataTable partition) {
        if(partition.table.isEmpty())
            return true;
        if(partition.table.get(0).size()<=1)
            return true;
        return false;
    }


    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {

        //create the data structure accepted by the classifyTuple(...)
        ArrayList<DataTuple.DiscreteTag>[] tuple = new ArrayList[2];
        ArrayList<DataTuple.DiscreteTag> columns = new ArrayList<>();
        ArrayList<DataTuple.DiscreteTag> vals= new ArrayList<>();

        /*Create headers for the tuple. Need to correspond to the headers in DataTable's loadRecordedData()
        method, CLASS column excluded*/

        if(Utilities.POWER_PILLS){
            columns.add( DataTuple.DiscreteTag.POWER_PILL_DISTANCE);
            columns.add( DataTuple.DiscreteTag.DIRECTION_TO_POWER_PILL);

        }

        columns.add(DataTuple.DiscreteTag.PILL_DISTANCE);
        columns.add(DataTuple.DiscreteTag.DIRECTION_TO_PILL);

        if(Utilities.ALL_GHOSTS){
            //Blinky
            //columns.add(DataTuple.DiscreteTag.BLINKY_DISTANCE);
            columns.add(DataTuple.DiscreteTag.BLINKY_DIRECTION);
            //columns.add(DataTuple.DiscreteTag.BLINKY_EDIBLE);

            //Pinky
            //columns.add(DataTuple.DiscreteTag.PINKY_DISTANCE);
            columns.add(DataTuple.DiscreteTag.PINKY_DIRECTION);
            //columns.add(DataTuple.DiscreteTag.PINKY_EDIBLE);

            //Inky
            //columns.add(DataTuple.DiscreteTag.INKY_DISTANCE);
            columns.add(DataTuple.DiscreteTag.INKY_DIRECTION);
            //columns.add(DataTuple.DiscreteTag.INKY_EDIBLE);

            //Sue
            //columns.add(DataTuple.DiscreteTag.SUE_DISTANCE);
            columns.add(DataTuple.DiscreteTag.SUE_DIRECTION);
            //columns.add(DataTuple.DiscreteTag.SUE_EDIBLE);

        }

        //closest ghost
        columns.add(DataTuple.DiscreteTag.CLOSEST_GHOST_DISTANCE);
        columns.add(DataTuple.DiscreteTag.CLOSEST_GHOST_DIRECTION);
        columns.add(DataTuple.DiscreteTag.CLOSEST_GHOST_EDIBLE);

      //get values for closest pill
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int pillIndex = game.getClosestNodeIndexFromNodeIndex(pacmanIndex,
                game.getActivePillsIndices(), Constants.DM.PATH);
        int pillDist = game.getShortestPathDistance(pacmanIndex, pillIndex);
        Constants.MOVE pillMove = game.getNextMoveTowardsTarget(pacmanIndex,
                pillIndex, Constants.DM.PATH);

        DataTuple.DiscreteTag closestPillDistance = dataSet.parseRawDistance(DataTuple.DiscreteTag.PILL_DISTANCE,pillDist);
        DataTuple.DiscreteTag directionToPill = dataSet.parseRawDirection(DataTuple.DiscreteTag.DIRECTION_TO_PILL,pillMove);

        if(Utilities.POWER_PILLS){
            //get values for closest power pill
            int powerPillIndex = game.getClosestNodeIndexFromNodeIndex(pacmanIndex,
                    game.getPowerPillIndices(), Constants.DM.PATH);
            int powerPillDist = game.getShortestPathDistance(pacmanIndex, powerPillIndex);
            Constants.MOVE powerPillMove = game.getNextMoveTowardsTarget(pacmanIndex,
                    powerPillIndex, Constants.DM.PATH);

            DataTuple.DiscreteTag closestPowerPillDistance = dataSet.parseRawDistance(DataTuple.DiscreteTag.POWER_PILL_DISTANCE,powerPillDist);
            DataTuple.DiscreteTag directionToPowerPill = dataSet.parseRawDirection(DataTuple.DiscreteTag.DIRECTION_TO_POWER_PILL,powerPillMove);

            //Add vals to tuple, need to have as many as headers
            vals.add(closestPowerPillDistance);
            vals.add(directionToPowerPill);
        }

        vals.add(closestPillDistance);
        vals.add(directionToPill);

        ArrayList<GhostValues> ghosts = closestGhost(game);
        if(Utilities.ALL_GHOSTS){

            //add blinky
            //vals.add(ghosts.get(0).distanceTag);
            vals.add(ghosts.get(0).ghostDirectionTag);
            //vals.add(ghosts.get(0).ghostEdibleTag);

            //add pinky
            //vals.add(ghosts.get(1).distanceTag);
            vals.add(ghosts.get(1).ghostDirectionTag);
            //vals.add(ghosts.get(1).ghostEdibleTag);

            //add inky
            //vals.add(ghosts.get(2).distanceTag);
            vals.add(ghosts.get(2).ghostDirectionTag);
            //vals.add(ghosts.get(2).ghostEdibleTag);

            //add sue
            //vals.add(ghosts.get(3).distanceTag);
            vals.add(ghosts.get(3).ghostDirectionTag);
            //  vals.add(ghosts.get(3).ghostEdibleTag);

        }

        //sort ghosts
        Collections.sort(ghosts, new Comparator<GhostValues>() {

            @Override
            public int compare(GhostValues o1, GhostValues o2) {
                return Integer.compare(o1.distance, o2.distance);
            }
        });

        //add closest ghost
        vals.add(ghosts.get(0).distanceTag);
        vals.add(ghosts.get(0).ghostDirectionTag);
        vals.add(ghosts.get(0).ghostEdibleTag);

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

        if(LOG){
            System.out.println("\n--->Closest ghost: "+ghosts.get(0).toString());
            System.out.println("\t===>Closest pill distance: "+closestPillDistance+", direction to: "+directionToPill);
            System.out.println("\t\t>>>>>>> Classified as:" +move.toString());
        }

        return returnMove;

    }




    /**Classifies the test data in order to calculate the accuracy of the tree.
     *
     * @param splittable : DataTable<T>
     * @return acc : double
     */
    private double getAccuracyOfTree(DataTable splittable) throws Exception {
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
        return (double) rightClass/(size-1);

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

    /**Calculates the closest ghost its values: distance, direction, isEdible. Creates and returns a pojo object holding
     * the values.
     *
     * @param game : Game
     * @return  ghost : GhostValues()
     */
    private ArrayList<GhostValues> closestGhost(Game game){
        ArrayList<GhostValues> ghosts = new ArrayList<>();
        int distance;

        int pacmanIndex = game.getPacmanCurrentNodeIndex();

        int ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY);
        distance = game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        ghosts.add(new GhostValues(
                distance,
                Constants.GHOST.BLINKY,
                game.getGhostLastMoveMade(Constants.GHOST.BLINKY),
                game.isGhostEdible(Constants.GHOST.BLINKY))
        );


        ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.PINKY);
        distance = game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        ghosts.add(new GhostValues(
                distance,
                Constants.GHOST.PINKY,
                game.getGhostLastMoveMade(Constants.GHOST.PINKY),
                game.isGhostEdible(Constants.GHOST.PINKY))
        );

        ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.INKY);
        distance = game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        ghosts.add(new GhostValues(
                distance,
                Constants.GHOST.INKY,
                game.getGhostLastMoveMade(Constants.GHOST.INKY),
                game.isGhostEdible(Constants.GHOST.INKY))
        );

        ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.SUE);
        distance = game.getShortestPathDistance(pacmanIndex,ghostCurrentNodeIndex);
        ghosts.add(new GhostValues(
                distance,
                Constants.GHOST.SUE,
                game.getGhostLastMoveMade(Constants.GHOST.SUE),
                game.isGhostEdible(Constants.GHOST.SUE))
        );

        return ghosts;
    }


    /**The node object on which the tree is being build with.
     */
    protected class Node{

        protected DataTuple.DiscreteTag label, edge;
        protected boolean isLeaf = false;
        protected Node parent = null;
        protected ArrayList<Node> children = new ArrayList<>();
        protected  double accuracy;
        protected int id =0, depthOfNode=0, xPos;
        protected Attribute attribute;

    }

    /**POJO to store ghost values.
     *
     */
    private class GhostValues{
        DataTuple.DiscreteTag distanceTag;
        DataTuple.DiscreteTag ghostDirectionTag;
        DataTuple.DiscreteTag ghostEdibleTag;
        Constants.GHOST ghost;
        int distance;


        /**Instantiates this object and discretize the values.
         *  @param distance : int
         * @param ghost : Constants.Ghost
         * @param ghostEdibleTag : boolean
         */


        public GhostValues(int distance, Constants.GHOST ghost, Constants.MOVE move, boolean ghostEdibleTag) {
            if(distance<0)
                distance=Integer.MAX_VALUE;
            this.distance= distance;
            this.ghost=ghost;

            switch (ghost){
                case BLINKY:
                    distanceTag = dataSet.parseRawDistance(DataTuple.DiscreteTag.BLINKY_DISTANCE,this.distance);
                    ghostDirectionTag = dataSet.parseRawDirection(DataTuple.DiscreteTag.BLINKY_DIRECTION,move);
                    this.ghostEdibleTag = dataSet.parseRawEdible(DataTuple.DiscreteTag.BLINKY_EDIBLE, ghostEdibleTag);
                    if(ghostDirectionTag== DataTuple.DiscreteTag.BLINKY_MOVE_NEUTRAL)
                        ghostDirectionTag= DataTuple.DiscreteTag.BLINKY_MOVE_LEFT;

                    break;

                case INKY:
                    distanceTag = dataSet.parseRawDistance(DataTuple.DiscreteTag.INKY_DISTANCE,this.distance);
                    ghostDirectionTag = dataSet.parseRawDirection(DataTuple.DiscreteTag.INKY_DIRECTION,move);
                    this.ghostEdibleTag = dataSet.parseRawEdible(DataTuple.DiscreteTag.INKY_EDIBLE, ghostEdibleTag);
                    if(ghostDirectionTag== DataTuple.DiscreteTag.INKY_MOVE_NEUTRAL)
                        ghostDirectionTag= DataTuple.DiscreteTag.INKY_MOVE_LEFT;

                    break;

                case PINKY:
                    distanceTag = dataSet.parseRawDistance(DataTuple.DiscreteTag.PINKY_DISTANCE,this.distance);
                    ghostDirectionTag = dataSet.parseRawDirection(DataTuple.DiscreteTag.PINKY_DIRECTION,move);
                    this.ghostEdibleTag = dataSet.parseRawEdible(DataTuple.DiscreteTag.PINKY_EDIBLE, ghostEdibleTag);
                    if(ghostDirectionTag== DataTuple.DiscreteTag.PINKY_MOVE_NEUTRAL)
                        ghostDirectionTag= DataTuple.DiscreteTag.PINKY_MOVE_LEFT;

                    break;

                case SUE:
                    distanceTag = dataSet.parseRawDistance(DataTuple.DiscreteTag.SUE_DISTANCE,this.distance);
                    ghostDirectionTag = dataSet.parseRawDirection(DataTuple.DiscreteTag.SUE_DIRECTION,move);
                    this.ghostEdibleTag = dataSet.parseRawEdible(DataTuple.DiscreteTag.SUE_EDIBLE, ghostEdibleTag);
                    if(ghostDirectionTag== DataTuple.DiscreteTag.SUE_MOVE_NEUTRAL)
                        ghostDirectionTag= DataTuple.DiscreteTag.SUE_MOVE_LEFT;

                    break;


            }
        }


        public String toString(){
            return ghost+", distance: "+distanceTag+", move: "+ ghostDirectionTag+",is edible"+ghostEdibleTag;

        }

    }

    //Temp to debug
    public static void main(String[] args) {
        new MsPacmanID3();

    }
}
