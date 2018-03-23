package pacman.controllers.id3Controller;

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
public class ID3Controller extends Controller<Constants.MOVE>{

    DecisionTree tree;
    DataTable dataSet;

    /**Instantiate the controller, builds a data table on raw data, builds the decision tree on the data table.
     *
     */
    public ID3Controller(){
       dataSet= new DataTable();
       dataSet.loadRecordedData();
      // dataSet.loadExampleData();
       tree = new DecisionTree(dataSet,new AttributeSelector());

    }


    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {

        //create the data structure accepted by the classifyTuple(...)
        ArrayList<String>[] tuple = new ArrayList[2];
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> vals= new ArrayList<>();

        /*Create headers for the tuple. Need to correspond to the headers in DataTable's loadRecordedData()
        method, CLASS column excluded*/

        if(Utilities.POWER_PILLS){
            columns.add("power_pill_distance");
            columns.add( "direction_to_power_pill");

        }

        columns.add("pill_distance");
        columns.add("direction_to_pill");

        if(Utilities.ALL_GHOSTS){
            //Blinky
            //columns.add(DataTuple.DiscreteTag.BLINKY_DISTANCE);
            columns.add("bliky_direction");
            //columns.add(DataTuple.DiscreteTag.BLINKY_EDIBLE);

            //Pinky
            //columns.add(DataTuple.DiscreteTag.PINKY_DISTANCE);
            columns.add("pinky_direction");
            //columns.add(DataTuple.DiscreteTag.PINKY_EDIBLE);

            //Inky
            //columns.add(DataTuple.DiscreteTag.INKY_DISTANCE);
            columns.add("inky_direction");
            //columns.add(DataTuple.DiscreteTag.INKY_EDIBLE);

            //Sue
            //columns.add(DataTuple.DiscreteTag.SUE_DISTANCE);
            columns.add("sue_direction");
            //columns.add(DataTuple.DiscreteTag.SUE_EDIBLE);

        }

        //closest ghost
        columns.add("closest_ghost_distance");
        columns.add("closest_ghost_direction");
     //   columns.add("closest_ghost_edible");

      //get values for closest pill
        int pacmanIndex = game.getPacmanCurrentNodeIndex();
        int pillIndex = game.getClosestNodeIndexFromNodeIndex(pacmanIndex,
                game.getActivePillsIndices(), Constants.DM.PATH);
        int pillDist = game.getShortestPathDistance(pacmanIndex, pillIndex);
        Constants.MOVE pillMove = game.getNextMoveTowardsTarget(pacmanIndex,
                pillIndex, Constants.DM.PATH);

        String closestPillDistance = dataSet.parseDistancePill(pillDist);
        String directionToPill = dataSet.parseMove(pillMove);

        if(Utilities.POWER_PILLS){
            //get values for closest power pill
            int powerPillIndex = game.getClosestNodeIndexFromNodeIndex(pacmanIndex,
                    game.getPowerPillIndices(), Constants.DM.PATH);
            int powerPillDist = game.getShortestPathDistance(pacmanIndex, powerPillIndex);
            Constants.MOVE powerPillMove = game.getNextMoveTowardsTarget(pacmanIndex,
                    powerPillIndex, Constants.DM.PATH);

            String closestPowerPillDistance = dataSet.parseDistancePill(powerPillDist);
            String directionToPowerPill = dataSet.parseMove(powerPillMove);

            //Add vals to tuple, need to have as many as headers
            vals.add(closestPowerPillDistance);
            vals.add(directionToPowerPill);
        }

        vals.add(closestPillDistance);
        vals.add(directionToPill);

        ArrayList<GhostValues> ghosts = getGhosts(game);
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
     //   vals.add(ghosts.get(0).ghostEdibleTag);

        tuple[0] = columns;
        tuple[1] = vals;


        String move =  tree.classifyTuple(tuple);
        //DataTuple.DiscreteTag move =  classifyTuple(tuple);
        Constants.MOVE returnMove = null;
        if(move.equalsIgnoreCase("up"))
            returnMove = UP;
        if(move.equalsIgnoreCase("down"))
            returnMove = DOWN;
        if(move.equalsIgnoreCase("left"));
            returnMove  = LEFT;
        if(move.equalsIgnoreCase("up"))
            returnMove = RIGHT;

        if(LOG){
            System.out.println("\n--->Closest ghost: "+ghosts.get(0).toString());
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
    private ArrayList<GhostValues> getGhosts(Game game){
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

    /**POJO to store ghost values.
     *
     */
    private class GhostValues{


        protected boolean edible;
        protected Constants.MOVE move;
        protected String distanceTag;
        protected String ghostDirectionTag;
        protected String ghostEdibleTag;
        protected Constants.GHOST ghost;
        protected int distance;


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
            this.move=move;
            this.edible=ghostEdibleTag;

                distanceTag = dataSet.parseDistanceGhost(this.distance);
                ghostDirectionTag = dataSet.parseMove(move);
                this.ghostEdibleTag = dataSet.parseEdible(ghostEdibleTag);
                if(ghostDirectionTag.equalsIgnoreCase("neutral")) //TODO check if still buggy
                    ghostDirectionTag= "left";
        }


        public String toString(){
            return ghost+", distance: "+distanceTag+", move: "+ ghostDirectionTag+",is edible"+ghostEdibleTag;

        }

    }

    //Temp to debug
    public static void main(String[] args) {
        new ID3Controller();

    }
}
