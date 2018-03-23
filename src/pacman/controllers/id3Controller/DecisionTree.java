package pacman.controllers.id3Controller;

import java.util.ArrayList;
import java.util.LinkedList;

import static pacman.controllers.id3Controller.Utilities.LOG;

public class DecisionTree {

    protected class TreeNode {

        protected String label, edge;
        protected boolean isLeaf = false;
        protected TreeNode parent = null;
        protected ArrayList<TreeNode> children = new ArrayList<>();
        protected int id =0, depthOfNode=0, xPos;
    }

    private int nodeCount =0;
    private TreeNode root;
    private DataTable dataSet;
    private AttributeSelector attributeSelector;
    private double accuracy;
    private ArrayList<Attribute> attributeArray = new ArrayList<>();
    private Attribute attributeList;


    boolean sameClass = false, aEmpty = false;


    public DecisionTree(DataTable dataSet, AttributeSelector selectionMethod) {

        DataTable[] splitTables = dataSet.splitTableForHoldout(dataSet);
        this.dataSet = splitTables[0];
        attributeList = dataSet.generateAttributeList();
        attributeArray.add(0,this.dataSet.generateAttributeList());
        attributeSelector=selectionMethod;

        root = generateTree(this.dataSet,0);

//        accuracy = getAccuracyOfTree(splitTables[1]);

        System.out.println("Accuracy of tree: "+accuracy);
        Utilities.visualizeTreeDFS(root);
        System.out.println();

    }



    /**A recursive method to generate a decision tree. Input parameters are the data table on which the tree builds
     * itself upon and a list with attributes on which it decides the best attribute to select for the next node. The
     * selection is based on ID3.
     *
     * @param data : DataTable
     * @return
     */
    private TreeNode generateTree(DataTable data,int depth){


        TreeNode treeNode = new TreeNode();

        nodeCount++;
        treeNode.id =nodeCount;
        treeNode.depthOfNode=depth;

        if(DataTable.everyTupleInSameClass(data)){
            sameClass = true;
            treeNode.isLeaf=true;
            treeNode.label= data.getClassLabel(); // at index 0 is column header value
            System.out.println("\t Every in same class: "+ treeNode.label.toString());
            return treeNode;
        }

        else if(attributeList.isEmpty()){

            aEmpty = true;
            treeNode.isLeaf = true;
            treeNode.label = DataTable.majorityClassValue(this.dataSet);

            System.out.println("\t Attribute list is empty: ");
            return treeNode;
        }
        else{

            Attribute attributeHighestEntropy = attributeSelector.id3(data,attributeList);
            treeNode.label=attributeHighestEntropy.selectedAttribute;
            //REMOVE THE FUCKING ATTR FOR ALL SIBLINGS!!!!"
          //z   attributeList.list.remove(attributeHighestEntropy.selectedAttribute);
            attributeList.selectedAttribute=attributeHighestEntropy.selectedAttribute;

            if(LOG){

                System.out.println("\n\n### in generateTree #################################################################");
                System.out.println("\t\tDepth of node: "+depth+"\n\t\tNode count: "+nodeCount);
                data.printTable();
                System.out.println("\t-->Selected attributeHighestEntropy: "+treeNode.label);

            }

            //label edges TODO ändra till dataSet
            ArrayList<String> column = data.getColumn(data,attributeHighestEntropy.selectedAttribute);
            LinkedList<String> edges = new LinkedList<>(data.getUniqueValsFromColumn(column));


            for(int i = 0; i<attributeHighestEntropy.list.size(); i++){
                String attributeValue = attributeHighestEntropy.list.get(i);

                DataTable partition = DataTable.partition(data,attributeHighestEntropy.selectedAttribute,attributeValue); //TODO
                if(partitionIsEmpty(partition)){
                    TreeNode child = new TreeNode();
                    child.parent= treeNode;
                    child.edge=edges.removeFirst();
                    String classValue =  data.majorityClassValue(this.dataSet);
                    child.label=classValue;
                    child.isLeaf=true;
                    treeNode.children.add(child);

                }else {
                    depth++;
                    //        attributeArray.add(depth,attributeList);
                    TreeNode child = generateTree(partition,depth);
                    child.parent= treeNode;
                    child.edge=edges.removeFirst();
                    treeNode.children.add(child);


                }
                depth--;
            }

            System.out.println();
            return treeNode;

        }
    /*        while (!attributeHighestEntropy.list.isEmpty()){
                String attributeValue = attributeHighestEntropy.list.removeFirst();

                DataTable partition = DataTable.partition(data,attributeHighestEntropy.selectedAttribute,attributeValue); //TODO
                if(partitionIsEmpty(partition)){
                    TreeNode child = new TreeNode();
                    child.parent= treeNode;
                    child.edge=edges.removeFirst();
                    String classValue =  data.majorityClassValue(this.dataSet);
                    child.label=classValue;
                    child.isLeaf=true;
                    treeNode.children.add(child);

                }else {
                    depth++;
            //        attributeArray.add(depth,attributeList);
                    TreeNode child = generateTree(partition,depth);
                    child.parent= treeNode;
                    child.edge=edges.removeFirst();
                    treeNode.children.add(child);


                }
                depth--;
            }
*/

    }

    private boolean partitionIsEmpty(DataTable partition) {
        if(partition.table.size()<=1)
            return true;
        return false;
    }





    /**Classifies the test data in order to calculate the accuracy of the tree.
     *
     * @param splittable : DataTable<T>
     * @return acc : double
     */
    private double getAccuracyOfTree(DataTable splittable)  {
        ArrayList<String> classifiedAs = new ArrayList<>();
        ArrayList<String> classColumn = splittable.getColumn(splittable, "class");

        splittable.table.remove(splittable.table.size()-1); //Remove the classifier column
        int size = classColumn.size();
        for(int i =1;i<size;i++){
            ArrayList<String>[] tuple =  splittable.getTuple(i);
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
    protected String classifyTuple(ArrayList<String>[] tuple){
        String classifiedAs = null;

        LinkedList<TreeNode> stack = new LinkedList<>();
        stack.add(root);
        while (!stack.isEmpty()){
            TreeNode currentTreeNode = stack.removeFirst();
            if(currentTreeNode.isLeaf){
                classifiedAs = currentTreeNode.label;
            }else{
                String nodeLabel = currentTreeNode.label;
                //get value from tuple where col = label
                int index = tuple[0].indexOf(nodeLabel);
                String tupleValue= tuple[1].get(index);

                for(TreeNode child : currentTreeNode.children){
                    String edge = child.edge;
                    if (edge.equalsIgnoreCase(tupleValue)){
                        stack.addLast(child);
                    }
                }

            }
        }

        return classifiedAs;
    }

}
