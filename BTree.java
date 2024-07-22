import java.util.Comparator;
import java.util.NoSuchElementException;

public class BTree<T>{
    private Node<T> root;
    private int t;
    final private Comparator<T> comparator;

    public BTree(int t, Comparator<T> comparator){
        this.t = t;
        root = null;
        this.comparator = comparator;
    }

    public boolean Find(T value){
        if(root == null) return false;
        return find(root, value);
    }
    private boolean find(Node<T> node, T value){
        int index = node.findKey(value);
        if(index != -1){
            return true;
        } else {
            if(node.getChild(node.getIndex(value)) == null) return false;
            return find(node.getChild(node.getIndex(value)), value);
        }
    }



    public void Insert(T value){
        if(root == null){
            root = new Node<>(t, null, true, comparator);
            root.add(value);
        } else {
            if(Find(value)) throw new IllegalArgumentException();
            Dig(root, value);
        }
    }
    private void Dig(Node<T> node, T value){
        if(node.leaf()){
            node.add(value);
        } else {
            int index = node.getIndex(value);
            Dig(node.getChild(index), value);
        }
        if(node.getSize() == 2*t){
            Node<T> newroot = node.split();
            if(newroot != null) root = newroot;
        }
    }
    public void Erase(T value){
        if(!Find(value)) throw new NoSuchElementException();
        erase(root, value);
    }
    private void erase(Node<T> node, T value){
        int index = node.findKey(value);
        Node<T> isRoot = null;
        if(index != -1){
            Node<T> actual;
            if(node.leaf()){
                node.delete(value);
                actual = node;
            } else {
                if(node.getChild(index).getSize()>=node.getChild(index+1).getSize()){
                    actual = node.getChild(index).nodedelete(true);
                    node.setKey(index, actual.getKey(actual.getSize()-1));
                    actual.moveKeyL((actual.getSize()-1));
                } else {
                    actual = node.getChild(index+1).nodedelete(false);
                    node.setKey(index, actual.getKey(0));
                    actual.moveKeyL(0);
                }
            }
            while(actual != null && actual != root && actual.getSize() == t-2){
                isRoot = actual.fix();
                if(isRoot != null){
                    root = isRoot;
                }
                actual = actual.getParent();
            }
        } else {
            erase(node.getChild(node.getIndex(value)), value);
        }
    }
    public void PreOrder(){
        System.out.println("\n\nPREORDER");
        pre(root);
    }
    private void pre(Node<T> node){
        node.Show();
        System.out.println("("+node.getSize()+")");
        if(!node.leaf()){
            for(int i=0; i<=(2*t)-1; i++){
                if(node.getChild(i) != null){
                    if(node.getChild(i).getParent()!=node) System.out.println("Blad");
                    pre(node.getChild(i));
                } else break;
            }
        }
    }
    public void InOrder(){
        System.out.println("\n\nINORDER");
        inord(root);
    }
    private void inord(Node<T> node){
        if(!node.leaf()){
            for(int i=0; i<=(2*t)-1; i++){
                if(node.getChild(i) != null){
                    inord(node.getChild(i));
                    if(node.getKey(i) != null) System.out.print(node.getKey(i)+",");
                } else break;
            }
        } else {
            node.Show();
        }
    }
}
