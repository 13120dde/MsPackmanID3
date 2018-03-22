package pacman.controllers.id3Controller;

import dataRecording.DataTuple;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**Utility methods for id3Controller
 *
 *TODO: build a grapgh to visualize the tree
 */
public class Utilities {

    private static Graph graph;
    public static final boolean LOG = true;
    public static final boolean ALL_GHOSTS = false;
    public static final boolean POWER_PILLS = false;

    protected static Queue<MsPacmanID3.Node> queue = new LinkedList<>() ;


    protected static void printTuple(ArrayList<DataTuple.DiscreteTag>[] tuple) {
        for(int i =0;i<tuple.length;i++){
            for(DataTuple.DiscreteTag val : tuple[i]){
                System.out.print(val+"\t\t");
            }
            System.out.println();

        }
    }

    protected static void shuffleArray(DataTuple[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            DataTuple a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static void visualizeTree(MsPacmanID3.Node tree) {
        LinkedList<MsPacmanID3.Node> stack = new LinkedList<>();
        LinkedList<MsPacmanID3.Node> nodesToAddToGraph = new LinkedList<>();

        //BFS add to a list
        stack.add(tree);
        int depth =0;
        while (!stack.isEmpty()){
            MsPacmanID3.Node currentNode = stack.removeFirst();
            nodesToAddToGraph.addLast(currentNode);
            for(MsPacmanID3.Node child : currentNode.children){
                stack.addLast(child);

            }

        }

        int edgeCount = 0;
        Graph graph = new MultiGraph("Decision Tree ID3");

        graph.setStrict(false);
        graph.setAutoCreate(true);

        //Coords
        int x=1;
        //Create nodes
        for(int i = 0;i< nodesToAddToGraph.size();i++){
            MsPacmanID3.Node node = nodesToAddToGraph.get(i);

            graph.addNode(node.label+"-"+node.id);
            Node n = graph.getNode(i);

            if(node.parent==null)
                n.setAttribute("ui.label","<ROOT>"+node.label);

            else if(node.isLeaf)
                n.setAttribute("ui.label", "<LEAF>"+node.label);
            else
                n.setAttribute("ui.label",node.label);


            if(node.parent==null){
                node.xPos=x;
            }else{
                x = node.parent.xPos;
                int offset =  node.parent.children.size()/2;
                int indexOfNode = node.parent.children.indexOf(node);
                x += -offset+indexOfNode;
                node.xPos=x;
                //kolla -antal barn /-> offset

            }



            n.setAttribute("xyz",x,-node.depthOfNode,0);

        }

        //Create edges
        for(MsPacmanID3.Node node: nodesToAddToGraph ){
            if(node.parent!=null){
                graph.addEdge(
                        node.edge+"-"+edgeCount,
                        node.label+"-"+node.id,
                        node.parent.label+"-"+node.parent.id);
                graph.getEdge(node.edge+"-"+edgeCount).setAttribute("ui.label",node.edge);
            }
            edgeCount++;
        }

         Viewer v = graph.display();
        v.disableAutoLayout();
    }
}
