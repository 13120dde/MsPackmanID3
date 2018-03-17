package pacman.controllers.dataMiningController;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.LinkedList;

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
        dataSet.loadExampleData();

        attributeList = dataSet.getAttributeList();
        //attributeList = dataSet.getAttributeListTest();
        selectionMethod = new AttributeSelection();
        tree = new Node().generateTree(dataSet,attributeList);
        System.out.println("Size of tree: "+nodeCount);
        Utilities.breadth(tree);

    }




    public static void main(String[] args) {
        new MsPacmanID3();

    }



    public class Node<T>{

        protected T label, edge;
        protected boolean isLeaf = false;
        protected Node parent = null;
        protected ArrayList<Node> children = new ArrayList<>();
        protected int edgeNbr =0;

        private Node generateTree(DataTable dataSet, LinkedList<T> attributeList){
            Node node = new Node();

            nodeCount++;
            if(dataSet.everyTupleInSameClass()){
                node.isLeaf=true;
                node.label= dataSet.getClassLabel(1);
                return node;
            }
            else if(attributeList.isEmpty()){
                node.isLeaf = true;
                node.label = dataSet.majorityClassValue();
                return node;
            }else{
                //T  attribute = (T) selectionMethod.id3(dataSet,attributeList);
                T  attribute = (T) selectionMethod.getSimpleAttribute(dataSet,attributeList);
                node.label=attribute;
                attributeList.remove(attribute);

                DataTable[] partitionedSets = dataSet.partitionSetOnAttributeValue(attribute, dataSet);

                for(int i = 0; i<partitionedSets.length;i++){
                    if(partitionedSets[i].table.isEmpty()){
                        Node child = new Node();
                        child.parent=node;
                        T classValue = (T) dataSet.majorityClassValue();
                        child.label=classValue;
                        child.isLeaf=true;
                        node.children.add(child);

                    }else {
                        Node child = generateTree(partitionedSets[i],attributeList);
                        child.parent=node;
                        node.children.add(child);

                    }

                }

                System.out.println();
                return node;
            }

        }

    }
}
