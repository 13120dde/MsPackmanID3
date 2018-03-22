package pacman.controllers.id3Controller;

import dataRecording.DataTuple;

import java.util.ArrayList;

public class Attribute{

    protected DataTuple.DiscreteTag selectedAttribute;
    protected ArrayList<DataTuple.DiscreteTag> list;


    protected Attribute(DataTuple.DiscreteTag selectedAttribute,ArrayList<DataTuple.DiscreteTag> list){
        this.selectedAttribute=selectedAttribute;
        this.list = list;
    }

    protected boolean isEmpty(){
        return list.isEmpty();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Attribute: "+selectedAttribute+" = {");
        for (DataTuple.DiscreteTag tag: list) {
            sb.append(tag+", ");
        }
        sb.append("}");
        return sb.toString();
    }

}
