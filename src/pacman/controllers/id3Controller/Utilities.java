package pacman.controllers.id3Controller;

import dataRecording.DataTuple;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**Utility methods for id3Controller
 *
 *TODO: build a grapgh to visualize the tree
 */
public class Utilities {

    private static Graph graph;
    public static final boolean log = false;

    protected static Queue<MsPacmanID3.Node> queue = new LinkedList<>() ;



    public static void printTuple(ArrayList<DataTuple.DiscreteTag>[] tuple) {
        for(int i =0;i<tuple.length;i++){
            for(DataTuple.DiscreteTag val : tuple[i]){
                System.out.print(val+"\t\t");
            }
            System.out.println();

        }
    }
}
