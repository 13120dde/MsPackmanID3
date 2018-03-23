package pacman.controllers.id3Controller;

import dataRecording.DataTuple;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

import javax.swing.tree.TreeNode;
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

    public static final boolean LOG = true;
    public static final boolean ALL_GHOSTS = false;
    public static final boolean POWER_PILLS = false;
    static Graph graph = new MultiGraph("Decision Tree ID3");



    public static void visualizeTreeDFS(MsPacmanID3.Node tree) {
        LinkedList<MsPacmanID3.Node> stack = new LinkedList<>();
        LinkedList<MsPacmanID3.Node> nodesToAddToGraph = new LinkedList<>();

        graph.setStrict(false);
        graph.setAutoCreate(true);
        dfs(tree);
        Viewer v = graph.display();
        v.disableAutoLayout();/*
        for(int i=2;i>0;i--)
            printKDistant(tree, i);*/

    }

    static void printKDistant(MsPacmanID3.Node node, int k)
    {
        if (node == null)
            return;
        if (k == 0)
        {
            System.out.println(node.label + " - "+node.depthOfNode);
            return;
        }
        else
        {
            for(MsPacmanID3.Node child : node.children){
                printKDistant(child, k - 1);
            }
        }
    }

    static int x=0;

    private static void dfs(MsPacmanID3.Node node) {
        if (node==null){
            return;
        }
        printKDistant(node,x);
        x++;



        //process node
        graph.addNode(node.label+"-"+node.id);
        Node n = graph.getNode(node.label+"-"+node.id);

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
            x += -offset+indexOfNode-node.children.size();
            node.xPos=x;
            //kolla -antal barn /-> offset

        }
        if(node.parent!=null){
            graph.addEdge(
                    node.id+"-"+node.parent.id,
                    node.label+"-"+node.id,
                    node.parent.label+"-"+node.parent.id);
            graph.getEdge(node.id+"-"+node.parent.id).setAttribute("ui.label",node.edge);
        }


        n.setAttribute("xyz",x,-node.depthOfNode,0);

        ArrayList<MsPacmanID3.Node> children = node.children;
        for(MsPacmanID3.Node next : children){

            dfs(next);

        }
    }

    public static void visualizeTreeBFS(MsPacmanID3.Node tree) {
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