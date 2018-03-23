package pacman.controllers.id3Controller;

import dataRecording.DataTuple;

import java.util.LinkedList;

public class Attribute{

    protected String selectedAttribute;
    protected LinkedList<String> list;


    protected Attribute(String selectedAttribute,LinkedList<String> list){
        this.selectedAttribute=selectedAttribute;
        this.list = list;
    }

    public Attribute(Attribute attribute) {
        this.selectedAttribute=attribute.selectedAttribute;
        this.list = new LinkedList<>();
        for(String s : attribute.list)
            this.list.add(s);

    }

    protected boolean isEmpty(){
        return list.isEmpty();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Attribute: "+selectedAttribute+" = {");
        for (String tag: list) {
            sb.append(tag+", ");
        }
        sb.append("\b\b}");
        return sb.toString();
    }

}
