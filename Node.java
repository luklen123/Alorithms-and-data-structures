import jdk.jshell.spi.ExecutionControl;

import javax.naming.OperationNotSupportedException;
import java.util.Comparator;

public class Node<T> {
    private Node<T> parent;
    private Node<T>[] child;
    private T[] key;
    private int size;
    private boolean isLeaf;
    private final Comparator<T> comparator;

    public Node(int capacity, Node parent, boolean isLeaf, Comparator<T> comparator) {
        child = (Node<T>[]) new Node[(2*capacity)+1];
        key = (T[]) new Object[2*capacity];
        this.parent = parent;
        this.comparator = comparator;
        this.isLeaf = isLeaf;
    }

    public int findKey(T value){
        if(comparator.compare(key[0], value) > 0) return -1;
        if(comparator.compare(key[size-1], value) < 0) return -1;
        int l = 0;
        int r = size-1;
        int mid = (l+r)/2;
        while(l < r){
            if(comparator.compare(key[mid], value) < 0){
                l=mid+1;
            } else if(comparator.compare(key[mid], value) == 0){
                return mid;
            } else {
                r = mid-1;
            }
            mid = (l+r)/2;
        }
        if(comparator.compare(key[l], value) != 0) return -1;
        return l;
    }
    public int getIndex(T value){
        if(comparator.compare(key[0], value) > 0) return 0;
        if(comparator.compare(key[size-1], value) < 0) return size;
        int l = 1;
        int r = size-1;
        int mid = (l+r)/2;
        while(l < r){
            if(comparator.compare(key[mid], value) < 0){
                l = mid+1;
            } else {
                r = mid;
            }
            mid = (l+r)/2;
        }
        return l;
    }
    public void add(T value){
        if(size == 0){
            key[0] = value;
        } else {
            int index = getIndex(value);
            moveKeyR(index);
            key[index] = value;
        }
        size++;
    }
    public void moveKeyR(int index){
        for(int i=size-1; i>=index; i--){
            key[i+1] = key[i];
        }
    }
    public Node<T> split(){
        int index = (size-1)/2;
        boolean bigger = false;
        Node<T> brother = new Node<>(key.length/2, parent, isLeaf, comparator);
        brother.setChild(0, child[index+1]);
        if(!isLeaf) child[index+1].setParent(brother);
        child[index+1] = null;
        for(int i = index+1; i < size; i++){
            brother.setKey(i-(index+1), key[i]);
            brother.setChild(i-index, child[i+1]);
            if(!isLeaf) child[i+1].setParent(brother);
            key[i] = null;
            child[i+1] = null;
        }
        brother.setSize(size-index-1);
        if(parent == null) {
            parent = new Node<>(key.length/2, null, false, comparator);
            parent.setChild(0, this);
            brother.setParent(parent);
            bigger = true;
        }
        parent.add(key[index]);
        key[index] = null;
        size = index;
        index = parent.getIndex(key[0])+1;
        for(int i = parent.getSize()-1; i >= index; i--){
            parent.setChild(i+1, parent.getChild(i));
        }
        parent.setChild(index, brother);
        if(bigger) return parent;
        else return null;
    }
    public void moveChildR(int index){
        for(int i=size; i>=index; i--){
            key[i+1] = key[i];
        }
    }
    public void delete(T value){
        int index = findKey(value);
        moveKeyL(index);
    }
    public Node<T> nodedelete(boolean biggest) {
        Node<T> value = this;
        if(biggest) {
            if(!isLeaf) {
                value = child[size].nodedelete(biggest);
            }
        } else {
            if(!isLeaf) {
                value = child[0].nodedelete(biggest);
            }
        }
        return value;
    }
    public Node<T> fix(){
        int index = parent.getIndex(key[0]);
        if(index-1 >=0 && parent.getChild(index-1) != null && parent.getChild(index-1).getSize() >= key.length/2){
            // zabieram z lewego brata

            Node<T> brother = parent.getChild(index-1);
            add(parent.getKey(index-1));
            parent.setKey(index-1, brother.getKey(brother.getSize()-1));
            brother.setKey(brother.getSize()-1, null);
            if(!isLeaf){
                moveChildR(0);
                setChild(0, brother.getChild(brother.getSize()));
                child[0].setParent(this);
                brother.setChild(brother.getSize(), null);
            }
            brother.setSize(brother.getSize()-1);
        } else if(index+1 <= child.length-1 && parent.getChild(index+1) != null && parent.getChild(index+1).getSize() >= key.length/2){
            // zabieram z prawego brata

            Node<T> brother = parent.getChild(index+1);
            add(parent.getKey(index));
            parent.setKey(index, brother.getKey(0));
            brother.moveKeyL(0);
            brother.setSize(brother.getSize()+1);
            if(!isLeaf){
                setChild(size, brother.getChild(0));
                child[size].setParent(this);
                brother.moveChildL(0);
            }
            brother.setSize(brother.getSize()-1);
        } else if(index-1 >=0 && parent.getChild(index-1) != null){
            // lacze sie z lewym bratem i usuwam siebie

            Node<T> brother = parent.getChild(index-1);
            if(!isLeaf){
                for(int i=0; i<=size; i++){
                    brother.setChild(brother.getSize()+i+1, child[i]);
                    child[i].setParent(brother);
                }
            }
            for(int i=0; i<size; i++) brother.add(key[i]);
            parent.moveChildL(index);
            parent.setSize(parent.getSize()+1);
            brother.add(parent.getKey(index-1));
            parent.moveKeyL(index-1);
            parent.setSize(parent.getSize()-1);
            size=0;
            if(parent.getSize() == 0 && parent.getParent() == null){
                brother.setParent(null);
                return brother;
            }
        } else if(index+1 <= child.length-1 && parent.getChild(index+1) != null){
            // lacze sie z prawym bratem i go usuwam
            Node<T> brother = parent.getChild(index+1);
            if(!isLeaf){
                for(int i=0; i<=brother.getSize(); i++){
                    child[(size+i+1)] = brother.getChild(i);
                    child[(size+i+1)].setParent(this);
                }
            }
            for(int i=0; i<brother.getSize(); i++) add(brother.getKey(i));
            parent.moveChildL(index+1);
            parent.setSize(parent.getSize()+1);
            add(parent.getKey(index));
            parent.moveKeyL(index);
            parent.setSize(parent.getSize()-1);
            if(parent.getSize() == 0 && parent.getParent() == null){
                setParent(null);
                return this;
            }
        } else {
            throw new IllegalArgumentException();
        }
        return null;
    }
    // przesun wszystko w lewo zeby zniszczyc index
    public void moveChildL(int index){
        for(int i=index; i<size; i++){
            child[i] = child[i+1];
        }
        for(int i=size; i<child.length; i++){
            child[i] = null;
        }
    }
    // przesun wszystko w lewo zeby zniszczyc index
    public void moveKeyL(int index){
        for(int i=index; i<size-1; i++) key[i] = key[i+1];
        for(int i=size-1; i<child.length-1; i++) key[i] = null;
        size--;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Node<T> getParent() {
        return parent;
    }
    public void setParent(Node<T> parent) {
        this.parent = parent;
    }
    public Node<T> getChild(int index) {
        return child[index];
    }
    public void setChild(int index, Node<T> node) {
        child[index] = node;
    }
    public T getKey(int index) {
        return key[index];
    }
    public void setKey(int index, T value) {
        key[index] = value;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public boolean leaf(){
        return isLeaf;
    }
    public void Show(){
        for(int i = 0; i < key.length; i++){
            if(key[i] != null) System.out.print(key[i]+",");
        }
    }
}
