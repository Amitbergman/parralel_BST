package main;

import algorithms.BST;

public class myMain {

    public static void main(String[] args) throws Exception {

        BST a = new BST();

        a.insert(2);
        a.insert(1);
        a.insert(8);
        a.insert(7);
        System.out.println(a.remove(7));
        System.out.println(a.remove(7));
        System.out.println(a.remove(7));
        
    }
}
