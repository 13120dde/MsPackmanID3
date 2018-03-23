package pacman.controllers.id3Controller;

import dataRecording.DataTuple;

import java.util.ArrayList;
import java.util.LinkedList;

/**This class is responsible for selecting the best attribute from a data set, where the best attribute is the one
 * with purest classifiers.
 *
 * This class currently provides ID3 selection method.
 *TODO: implement CART, C4.5
 * Created by: Patrik Lind, 17-03-2018
 *
 * @param
 */
public class AttributeSelector {

    private ArrayList<String> uniqueClasses;
    private double probabilityDenominator;


    /**Calculates the entropy for each attribute (column in the data set) in the provided list and returns
     * the attribute with highest entropy, ie attributes that will lead to purest data sets. Following the
     * ID3 approach the definition of pureness is how homogeneous the values in the table classify as.
     *
     *
     * @param dataTable : DataTable<T>
     * @param attributeList : LinkedList
     * @return attribute : T
     */
    protected Attribute id3(DataTable dataTable, Attribute attributeList) {

        double averageEntropy;

        probabilityDenominator = dataTable.table.get(0).size()-1;
         uniqueClasses = dataTable.getUniqueValsFromColumn(
                 dataTable.getColumn(dataTable, "class"));

        //calculate Info(D) for the whole table
        averageEntropy = generalEntropy(dataTable);


        double[] entropies = new double[attributeList.list.size()];

        //Calculate entropy for every attribute in the list
        for(int i= 0; i<attributeList.list.size();i++){

            //split tables on element in attribute's column
            ArrayList<String> columnToCheck = dataTable.getColumn(dataTable,attributeList.list.get(i));
            ArrayList<String> uniqueValsInColumn = dataTable.getUniqueValsFromColumn(columnToCheck);

            DataTable[] splitTables = new DataTable[uniqueValsInColumn.size()];
            ArrayList<String>[] tuple = dataTable.getTuple(0);
            for(int k =0; k<splitTables.length; k++){
                splitTables[k] = new DataTable();
                splitTables[k].addTuple(tuple[0]);
            }
            //Fill splitTables on Dj
            for(int j = 1;j<columnToCheck.size();j++){
                for (int k = 0; k<uniqueValsInColumn.size();k++){
                    if (columnToCheck.get(j)==uniqueValsInColumn.get(k)){
                        tuple = dataTable.getTuple(j);
                        splitTables[k].addTuple(tuple[1]);
                    }
                }
            }

            //Info(D) for each splitTable
            double results = 0;
            double denominator=0;
            for(int k = 0; k<splitTables.length;k++){


                ArrayList<String> classColumn = splitTables[k].getColumn(splitTables[k], "class");
                ArrayList<String> uniqueClassesInSplit = splitTables[k].getUniqueValsFromColumn(classColumn);
                denominator = splitTables[k].table.get(0).size()-1; //Disregard column name
                double[] numerators= new double[uniqueClassesInSplit.size()];

                //Get numerators in split
                ArrayList<String> splitTableClassCol = splitTables[k].getColumn(splitTables[k], "class");
                for(int m = 1; m<splitTableClassCol.size();m++){
                    for(int n = 0;n<uniqueClassesInSplit.size();n++){
                        if(splitTableClassCol.get(m)==uniqueClassesInSplit.get(n)){
                            numerators[n]++;
                        }
                    }
                }
                //-pi log2(pi) for split
                double entropy=0;
                for(int m =0; m<numerators.length;m++){
                    if(numerators[m]>0){
                        double probability = (double)numerators[m]/denominator;
                        entropy += -probability * (Math.log(probability)/Math.log(2));
                    }

                }

                results+=((denominator/probabilityDenominator)*entropy);
            }
            entropies[i]=results;
        }

        for(int i = 0; i<entropies.length;i++){
            entropies[i]=averageEntropy-entropies[i];
        }

        //Select highest attribute
        int indexToHighest =0;
        double highest= Double.MIN_VALUE;
        for(int i=0;i<entropies.length;i++){
            if(entropies[i]>highest){
                indexToHighest=i;
                highest=entropies[i];
            }
        }

        String highestAttribute = attributeList.list.get(indexToHighest);
        LinkedList<String> uniqueElements = new LinkedList<>(dataTable.getUniqueValsFromColumn(
                dataTable.getColumn(dataTable,highestAttribute)));

        Attribute attribute = new Attribute(highestAttribute,uniqueElements);
        if(Utilities.LOG){
            printResult(averageEntropy,entropies,attributeList, attribute);
        }

        return attribute;

    }

    /**Calculates entropy for the whole table
     *
     * @param dataTable : DataTable
     * @return general entropy: Double
     */
    private double generalEntropy(DataTable dataTable) {

        double entropy=0;
        double denominator = dataTable.table.get(0).size()-1;
        ArrayList<String> columnToCheck= dataTable.table.get(dataTable.table.size()-1);

        int[] probabilityNumerators = new int[uniqueClasses.size()];

        //Info(D)
        for(int i = 0; i<uniqueClasses.size();i++){
            //calculate each numerator
            for(int j = 1; j<columnToCheck.size();j++){
                if(columnToCheck.get(j)==uniqueClasses.get(i)){
                    probabilityNumerators[i]++;
                }
            }
            //-pi log2(pi)
            double probability = (double)probabilityNumerators[i]/denominator;
            entropy += -probability * (Math.log(probability)/Math.log(2));

        }
        return entropy;
    }


    private void printResult(double averageEntropy, double[] entropies, Attribute attributeList, Attribute selected) {
        StringBuilder sb = new StringBuilder();
        sb.append("### IN id3 ###\n");
        sb.append("\tAverage entropy of D:"+averageEntropy+"\n");
        String list ="";
        for(int i = 0;i<entropies.length;i++){
            list+="\t"+attributeList.list.get(i)+" - "+entropies[i]+"\n";
        }
        sb.append(list);
        sb.append("\tSelected "+selected.toString());
        System.out.println(sb.toString());
    }


    //Just for testing
    public static void main(String[] args) {
        new AttributeSelector().test();
    }

    private void test() {
        DataTable table = new DataTable();
        table.loadExampleData();

      //  Attribute attribute= id3(table,attributeList);

    }
}
