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


    protected class Attribute{
        protected DataTuple.DiscreteTag selectedAttribute;
        protected ArrayList<DataTuple.DiscreteTag> uniqueAttributes;


        private Attribute(DataTuple.DiscreteTag selectedAttribute,ArrayList<DataTuple.DiscreteTag> uniqueAttributes){
            this.selectedAttribute=selectedAttribute;
            this.uniqueAttributes = uniqueAttributes;
        }

    }



    /**Calculates the entropy for each attribute (column in the data set) in the provided list and returns
     * the attribute with highest entropy, ie attributes that will lead to purest data sets. Following the
     * ID3 approach the definition of pureness is how homogeneous the values in the table classify as.
     *
     *
     * @param dataTable : DataTable<T>
     * @param attributeList : LinkedList
     * @return attribute : T
     */
    protected Attribute id3(DataTable dataTable, LinkedList<DataTuple.DiscreteTag> attributeList) throws Exception {
        System.out.println("### IN id3 ###");

        System.out.println("### IN id3 ###");
        DataTuple.DiscreteTag bestAttribute = null;
        double averageEntropy;

        //calculate Info(D) for the whole table
        averageEntropy = generalEntropy(dataTable);


        double[] entropies = new double[attributeList.size()];
        double probabilityDenominator=dataTable.getTableSize();

        ArrayList<DataTuple.DiscreteTag> uniqueClasses = dataTable.getUniqueValsFromColumn(
                dataTable.table.get(dataTable.table.size()-1)
        );

        //Check every attribute
        for(int i= 0; i<attributeList.size();i++){

            ArrayList<DataTuple.DiscreteTag> columnToCheck = dataTable.getColumn(dataTable,attributeList.get(i));
            ArrayList<DataTuple.DiscreteTag> uniqueValsInColumn = dataTable.getUniqueValsFromColumn(columnToCheck);
            double denominator = columnToCheck.size()-1;
            double[] uniqueValsNumerator = new double[uniqueValsInColumn.size()];

            double entropyForColumn = 0;

            //check column
            for(int j = 1;j<columnToCheck.size();j++){

                //calculate |Dj|
                for (int k = 0; k<uniqueValsInColumn.size();k++){
                    if (columnToCheck.get(j)==uniqueValsInColumn.get(k)){
                        uniqueValsNumerator[k]++;
                    }
                }

            }

            for(int j =0;j<uniqueClasses.size();j++){
                for(int k = 1; k<columnToCheck.size();k++){
                    for( int z=0;z<uniqueValsInColumn.size();z++){
                        if(columnToCheck.get(k)==uniqueValsInColumn.get(z)){
                            if(dataTable.table.get(dataTable.table.size()-1).get(k)==uniqueClasses.get(j)){
                                //uniqueValsNumerator2[z]++;
                            }
                        }
                    }

                }

            }
            System.out.println();

        }



        return null;

    }

    private double generalEntropy(DataTable dataTable) {
        System.out.println("\t calculating generalEntropy for:"+dataTable.toString());
        double entropy=0;
        ArrayList<DataTuple.DiscreteTag> columnToCheck= dataTable.table.get(dataTable.table.size()-1);
        ArrayList<DataTuple.DiscreteTag> uniqueClasses = dataTable.getUniqueValsFromColumn(columnToCheck);
        int probabilityDenominator =  columnToCheck.size()-1;
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
            double probability = (double)probabilityNumerators[i]/probabilityDenominator;
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
        table.loadExampleData();
        LinkedList<DataTuple.DiscreteTag> attributes = table.getAttributeList();
        try {
            Attribute attribute= id3(table,attributes);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
