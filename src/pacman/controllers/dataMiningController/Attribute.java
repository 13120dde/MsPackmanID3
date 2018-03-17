package pacman.controllers.dataMiningController;

import java.util.ArrayList;
import java.util.LinkedList;

public class Attribute<T> {


    private ArrayList<T> values;

    public Attribute(T label){
        values = new ArrayList<>();
        values.add(label);
    }

    public T getColumnLabel(){
        return values.get(0);
    }

    public void setColumnLabel(T label){
        values.add(0,label);
    }

    public ArrayList<T> getValues() {
        return values;
    }

    public void addValue(T value) {
        values.add((T) value);
    }

}
