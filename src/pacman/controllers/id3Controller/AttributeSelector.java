package pacman.controllers.id3Controller;

import dataRecording.DataTuple;

import java.util.ArrayList;

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

    private ArrayList<DataTuple.DiscreteTag> uniqueClasses;
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
    protected Attribute id3(DataTable dataTable, Attribute attributeList) throws Exception {

        System.out.println("### IN id3 ###");
        double averageEntropy;

        probabilityDenominator = dataTable.table.get(0).size()-1;
         uniqueClasses = dataTable.getUniqueValsFromColumn(
                 dataTable.getColumn(dataTable, DataTuple.DiscreteTag.CLASS));

        //calculate Info(D) for the whole table
        averageEntropy = generalEntropy(dataTable);


        double[] entropies = new double[attributeList.list.size()];

        //Calculate entropy for every attribute in the list
        for(int i= 0; i<attributeList.list.size();i++){

            //split tables on element in attribute's column
            ArrayList<DataTuple.DiscreteTag> columnToCheck = dataTable.getColumn(dataTable,attributeList.list.get(i));
            ArrayList<DataTuple.DiscreteTag> uniqueValsInColumn = dataTable.getUniqueValsFromColumn(columnToCheck);

            DataTable[] splitTables = new DataTable[uniqueValsInColumn.size()];
            ArrayList<DataTuple.DiscreteTag>[] tuple = dataTable.getTuple(0);
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


                ArrayList<DataTuple.DiscreteTag> classColumn = splitTables[k].getColumn(splitTables[k], DataTuple.DiscreteTag.CLASS);
                ArrayList<DataTuple.DiscreteTag> uniqueClassesInSplit = splitTables[k].getUniqueValsFromColumn(classColumn);
                denominator = splitTables[k].table.get(0).size()-1; //Disregard column name
                double[] numerators= new double[uniqueClassesInSplit.size()];

                //Get numerators in split
                ArrayList<DataTuple.DiscreteTag> splitTableClassCol = splitTables[k].getColumn(splitTables[k], DataTuple.DiscreteTag.CLASS);
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

        DataTuple.DiscreteTag highestAttribute = attributeList.list.get(indexToHighest);
        ArrayList<DataTuple.DiscreteTag> uniqueElements = dataTable.getUniqueValsFromColumn(
                dataTable.getColumn(dataTable,highestAttribute));

        Attribute attribute = new Attribute(highestAttribute,uniqueElements);
        System.out.println();

        return attribute;

    }

    private double generalEntropy(DataTable dataTable) {
        System.out.println("\t calculating generalEntropy for:"+dataTable.toString());
        double entropy=0;
        double denominator = dataTable.table.get(0).size()-1;
        ArrayList<DataTuple.DiscreteTag> columnToCheck= dataTable.table.get(dataTable.table.size()-1);

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


    //Just for testing
    public static void main(String[] args) {
        new AttributeSelector().test();
    }

    private void test() {
        DataTable table = new DataTable();
        table.loadExampleData();
        Attribute attributeList = new Attribute(null,table.getAttributeList());

        try {
            Attribute attribute= id3(table,attributeList);
            //DataTable[] tables = table.partitionSetOnAttributeValue(attribute.selectedAttribute,table);
            //Attribute attribute2= id3(tables[0],attribute);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
