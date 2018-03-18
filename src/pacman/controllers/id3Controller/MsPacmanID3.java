package pacman.controllers.id3Controller;

import org.graphstream.graph.implementations.MultiGraph;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**AI controller based on ID3 decition tree algorithm
 *
 * Created by: Patrik Lind, 17-03-2018
 *
 * @param <T>
 */
public class MsPacmanID3  <T> extends Controller<Constants.MOVE>{

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return null;
    }

    private DataTable dataSet;
    private LinkedList<T> attributeList;
    private AttributeSelection selectionMethod;

    private Node tree;
    private int nodeCount =0;


    public MsPacmanID3(){
        dataSet = new DataTable();
        dataSet.loadExampleData(); //TODO call on correct method when rdy
        DataTable<T>[] splitTables = dataSet.splitTableForHoldout(dataSet);
        attributeList = dataSet.getAttributeList();
       // dataSet = splitTables[0];
        selectionMethod = new AttributeSelection();
        tree = new Node().generateTree(splitTables[0],attributeList);
        System.out.println("### TREE BUILT\n\tAccuracy of tree:  "+getAccuracyOfTree(splitTables[1]));

        //  Utilities.createGraph(tree);

    }

    /**Classifies the test data in order to calculate the accuracy of the tree.
     *
     * @param splittable : DataTable<T>
     * @return acc : double
     */
    private double getAccuracyOfTree(DataTable<T> splittable) {
        ArrayList<T> classifiedAs = new ArrayList<>();
        ArrayList<T> classColumn = splittable.getColumn(splittable, (T) DataTable.DiscreteValues.CLASS);

        splittable.table.remove(splittable.table.size()-1); //Remove the classifier column
        int size = classColumn.size();
        for(int i =1;i<size;i++){
            ArrayList<T>[] tuple =  splittable.getTuple(i);
            if(tuple!=null){
                classifiedAs.add((T)  classify(tuple,tree));
            }
        }

        int rightClass = 0;
        for(int i = 0; i<classifiedAs.size();i++){
            if(classifiedAs.get(i)==classColumn.get(i+1))
                rightClass++;
        }
        return (double) rightClass/(size-1); //-1 to exclude column name
    }


    public T classify(ArrayList<T>[] tuple, Node<T> node) {
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
                if(node.children.get(i).edge==value){
                   toReturn= (T) classify(tuple, node.children.get(i));
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
        protected int edgeNbr =0;


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
