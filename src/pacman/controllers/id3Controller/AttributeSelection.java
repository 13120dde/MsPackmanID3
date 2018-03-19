package pacman.controllers.id3Controller;

import dataRecording.DataTuple;

import java.util.ArrayList;
import java.util.LinkedList;

/**This class is responsible for selecting the best attribute, where the best attribute is the one with highest entropy.
 * Currently provides only id3.
 *
 * Created by: Patrik Lind, 17-03-2018
 *
 * @param
 */
public class AttributeSelection {

    /**Helper method, returns the first attribute in the list-
     *
     * @param d
     * @param attributeList
     * @return
     */
    public DataTuple.DiscreteTag getSimpleAttribute(DataTable d, LinkedList<DataTuple.DiscreteTag> attributeList){
        return attributeList.removeFirst();

    }

    /**Calculates the entorpy for each attribute in the provided list and returns the attribute with highest entropy
     * (attributes that will lead to pure datasets).
     *
     *
     * @param dataTable : DataTable<T>
     * @param attributeList : LinkedList
     * @return attribute : T
     */
    public DataTuple.DiscreteTag id3(DataTable dataTable, LinkedList attributeList) {
        System.out.println("### IN id3 ###");
        DataTuple.DiscreteTag bestAttribute = null;
        double averageEntropy;

        //calculate Info(D)
        averageEntropy = calculateEntropy(dataTable);

        ArrayList<DataTuple.DiscreteTag> columns = new ArrayList<>(attributeList);
        double[] entropies = new double[columns.size()];
        double probabilityDenominator=dataTable.table.get(0).size()-1;

        //calculate InfoA(D) for each column
        for(int i = 0; i<columns.size();i++){
            //partition table on column values
            double entropy = 0;
            DataTable[] partitionedTables = dataTable.partitionSetOnAttributeValue(columns.get(i),dataTable);
            for(int j = 0; j<partitionedTables.length;j++){
                //calculate Dj
                double probablilityNumerator = partitionedTables[j].table.get(0).size()-1;
                entropy+= (probablilityNumerator/probabilityDenominator)*calculateEntropy(partitionedTables[j]);
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
        System.out.println("\t\tSelected attribute: "+bestAttribute.toString());
        return bestAttribute;
    }


    private double calculateEntropy(DataTable dataTable) {
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
        new AttributeSelection().test();
    }

    private void test() {
        DataTable table = new DataTable();
        LinkedList<DataTuple.DiscreteTag> attributes = table.getAttributeList();
        DataTuple.DiscreteTag attribute= id3(table,attributes);
    }
}
