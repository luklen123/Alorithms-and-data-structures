import java.util.Comparator;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        BTree<Integer> drzewo = new BTree<Integer>(3,new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1-o2;
            }
        });
        for (int i = 1; i <= 40; i++) {
            drzewo.Insert(i);
        }
        System.out.print("\n1:");
        drzewo.PreOrder();
        drzewo.InOrder();
        drzewo.Erase(15);
        System.out.print("\n2:");
        drzewo.PreOrder();
        drzewo.InOrder();
        drzewo.Erase(18);
        System.out.print("\n3:");
        drzewo.PreOrder();
        drzewo.InOrder();
        drzewo.Erase(3);
        System.out.print("\n4:");
        drzewo.PreOrder();
        drzewo.InOrder();
        drzewo.Insert(18);
        System.out.print("\n5:");
        drzewo.PreOrder();
        drzewo.InOrder();
    }
            /*for (int i = 1; i <= 10; i++) {
            drzewo.Insert(i);
        }
        drzewo.PreOrder();
        for (int i = 1; i <= 10; i++) {
            drzewo.Erase(i);
            drzewo.PreOrder();
            System.out.println();
        }*/
                /*for (int i = 1; i <= 40; i++) {
            drzewo.Insert(i);
        }
        System.out.println("\n1:");
        drzewo.PreOrder();
        drzewo.InOrder();
        drzewo.Erase(15);
        System.out.println("\n2:");
        drzewo.PreOrder();
        drzewo.InOrder();*/
}