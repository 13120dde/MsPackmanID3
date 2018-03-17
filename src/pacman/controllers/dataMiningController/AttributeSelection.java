package pacman.controllers.dataMiningController;

import java.util.LinkedList;

public class AttributeSelection<T> {


    public T id3(DataTable d, LinkedList attributeList) {
        return null;
    }

    public T getSimpleAttribute(DataTable d, LinkedList<T> attributeList){
        return attributeList.removeFirst();

    }
}
