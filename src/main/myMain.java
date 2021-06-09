package main;

import algorithms.BST;

public class myMain {

    public static void main(String[] args) throws Exception {
        BST a = new BST();

        a.insert(3);
        a.insert(4);
        a.remove(3);
        a.remove(76);
        System.out.println(a.contains(3));
        System.out.println(a.insert(7));
        System.out.println(a.contains(4));
        System.out.println(a.remove(4));
        System.out.println(a.contains(4));
        System.out.println(a.remove(7));
        System.out.println(a.insert(7));
        System.out.println(a.size());
        System.out.println(a.getKeysum());
    }
}
