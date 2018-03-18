package pacman.controllers.id3Controller;

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
 *
 */
public class Utilities {

    private static Graph graph;
    private static int nodeCount, edgeCount;
    private static double accuracy;
    protected static Queue<MsPacmanID3.Node> queue = new LinkedList<>() ;

    /**Overloaded method for createGraph(...). Besides showing the structure it also shows tha acc value passed in as
     * argument.
     * TODO
     * @param root : MsPacmanID3.Node
     * @param acc : double
     */
    public static void createGraph(MsPacmanID3.Node root, double acc) {
        accuracy=acc;
        /**
         * TODO show acc in some fashion
         */
        createGraph(root);

    }


    /**Displays the tree visually in a separate window.
     * TODO
     * @param root : MsPacmanID3.Node
     */
    public static void createGraph(MsPacmanID3.Node root) {

        if (root==null)
            return;

        int nodeId =0, edgeId=0;
        graph = new SingleGraph("ID3");
        graph.setStrict(false);
        graph.setAutoCreate(true);

        queue.clear();
        queue.add(root);
        while(!queue.isEmpty()){
            MsPacmanID3.Node node = queue.remove();
            Node n = graph.addNode(node.label.toString()+ ++nodeId);
            n.addAttribute(node.label.toString(),node.label.toString()+ nodeId);

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

    public static void main(String[] args) {
        graph = new MultiGraph("ID3");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.setAutoCreate( true );
        graph.addEdge( "AB", "A", "B" );
        graph.addEdge( "BC", "B", "C" );
        graph.addEdge( "CA", "C", "A" );
        graph.display();
        System.out.println();
    }


}
