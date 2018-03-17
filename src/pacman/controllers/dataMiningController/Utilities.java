package pacman.controllers.dataMiningController;

import dataRecording.DataTuple;

public class Utilities {

    protected static void printTrainingData(DataTuple[] dataTuples){
        System.out.format("%15s%15s%15s%15s%15s%15s%15s%15s","PILLS T","REM","P PILLS T","REM","Blinky Dir","Blinky dis","EDIBLE","CLASS");
        System.out.println();

        for (int i =0;i<dataTuples.length;i++){

            System.out.format("%15d%15d%15d%15d%15s%15d%b%15s",
                    /*dataTuples[i].DirectionChosen.toString(),*/
                    dataTuples[i].numberOfTotalPillsInLevel,
                    dataTuples[i].numOfPillsLeft,
                    dataTuples[i].numberOfTotalPowerPillsInLevel,
                    dataTuples[i].numOfPowerPillsLeft,
                    dataTuples[i].blinkyDir.toString(),
                    dataTuples[i].blinkyDist,
                    dataTuples[i].isBlinkyEdible
            );

            System.out.println();
            //System.out.println("\t"+dataTuples[i].DirectionChosen+"\n");


        }
    }
}
