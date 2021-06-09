package algorithms;

import main.BSTInterface;

public class BST implements BSTInterface {

    public BST() {
    }

    public final boolean contains(final int key) {
        System.out.println("contains was called");
        return false;
    }

    public final boolean insert(final int key) {

        System.out.println("insert was called");
        return false;
    }

    public final boolean remove(final int key) {
        System.out.println("remove was called");
        return false;
    }

    // Return your ID #
    public String getName() {
        return "203308192";
    }

    // Returns size of the tree.
    public final int size() {
    // NOTE: Guaranteed to be called without concurrent operations,
	// so need to be thread-safe.  The method will only be called
	// once the benchmark completes.
        System.out.println("size was called");
        return 1;
    }

    // Returns the sum of keys in the tree
    public final long getKeysum() {
    // NOTE: Guaranteed to be called without concurrent operations,
	// so no need to be thread-safe.
	//
	// Make sure to sum over a "long" variable or you will get incorrect
	// results due to integer overflow!
        System.out.println("getKeySum was called");
        return 1;
    }
}
