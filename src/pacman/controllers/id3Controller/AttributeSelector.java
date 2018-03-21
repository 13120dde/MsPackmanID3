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



    /**Calculates the entropy for each attribute (column in the data set) in the provided list and returns
     * the attribute with highest entropy, ie attributes that will lead to purest data sets. Following the
     * ID3 approach the definition of pureness is how homogeneous the values in the table classify as.
     *
     *
     * @param dataTable : DataTable<T>
     * @param attributeList : LinkedList
     * @return attribute : T
     */
    protected DataTuple.DiscreteTag id3(DataTable dataTable, LinkedList attributeList) throws Exception {
        System.out.println("### IN id3 ###");
        DataTuple.DiscreteTag bestAttribute = null;
        double averageEntropy;

        //calculate Info(D) for the whole table
        averageEntropy = information(dataTable);

        ArrayList<DataTuple.DiscreteTag> columns = new ArrayList<>(attributeList);
        double[] entropies = new double[columns.size()];
        double probabilityDenominator=dataTable.getTableSize();

        //calculate InfoA(D) for each column
        for(int i = 0; i<columns.size();i++){
            //partition table on column values
            double entropy = 0;
            DataTable[] partitionedTables = dataTable.partitionSetOnAttributeValue(columns.get(i),dataTable);
            for(int j = 0; j<partitionedTables.length;j++){
                //calculate Dj
                double probablilityNumerator = partitionedTables[j].getTableSize();
                entropy+= (probablilityNumerator/probabilityDenominator)* information(partitionedTables[j]);
            }
            //Total entropy of attribute i
            entropies[i]=averageEntropy-entropy;
        }

        //select T with highest entropy
        int indexOfHighest =0;
        double highestEntropy = Double.MIN_VALUE;
        for(int i=0; i<entropies.length;i++){
            double entropy = entropies[i];
            int compare = Double.compare(entropy,highestEntropy);
            if(compare>=0){
                indexOfHighest = i;
                highestEntropy=entropy;
            }

        }
        bestAttribute = columns.get(indexOfHighest);
        if(Utilities.log)
            System.out.println("\t\tSelected attribute: "+bestAttribute.toString());
        return bestAttribute;
    }


    private double information(DataTable dataTable) {
        System.out.println("\t calculating information for:"+dataTable.toString());
        double entropy=0;
        ArrayList<DataTuple.DiscreteTag> uniqueClasses = dataTable.getUniqueValsFromColumn(dataTable.table.get(dataTable.table.size()-1));
        double probabilityDenominator;
        double[] probabilityNumerators = new double[uniqueClasses.size()]; //amount of occurances of each class stored at index corresponding to uniqueClasses
        ArrayList<DataTuple.DiscreteTag> classColumn = dataTable.table.get(dataTable.table.size()-1);
        probabilityDenominator = classColumn.size()-1; //dont count column name

        //Info(D)
        for(int i = 0; i<uniqueClasses.size();i++){
            //calculate each numerator
            for(int j = 1; j<classColumn.size();j++){
                if(classColumn.get(j)==uniqueClasses.get(i)){
                    probabilityNumerators[i]++;
                }
            }
            //-pi log2(pi)
            double probability = probabilityNumerators[i]/probabilityDenominator;
            entropy += -probability * (Math.log(probability)/Math.log(2));

        }

        return entropy;
    }


    //Just for testing
    public static void main(String[] args) {
        new AttributeSelector().test();
    }

    private void test() {
        DataTable table = new DataTable();
        LinkedList<DataTuple.DiscreteTag> attributes = table.getAttributeList();
        try {
            DataTuple.DiscreteTag attribute= id3(table,attributes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
