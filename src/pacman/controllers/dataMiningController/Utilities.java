package pacman.controllers.dataMiningController;

import dataRecording.DataTuple;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Utilities {

    private static Graph graph;
    private static int nodeCount, edgeCount;
    protected static Queue<MsPacmanID3.Node> queue = new LinkedList<>() ;



    public static void breadth(MsPacmanID3.Node root) {
        if (root == null)
            return;

        graph = new MultiGraph("ID3");
        graph.setStrict(false);
        graph.setAutoCreate(true);

        queue.clear();
        queue.add(root);
        while(!queue.isEmpty()){
            MsPacmanID3.Node node = queue.remove();
            graph.addNode(node.label.toString()+ ++nodeCount);

            if(node.parent!=null){
               // graph.addEdge();
                edgeCount++;
            }
            System.out.print(node.label+ " ");
            if(node.children!= null){
                for(int i = 0 ; i<node.children.size();i++){
                    queue.add((MsPacmanID3.Node) node.children.get(i));

                }
            }

        }
        graph.display();

    }
}
