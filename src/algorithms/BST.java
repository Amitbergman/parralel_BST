package algorithms;

import main.BSTInterface;

public class BST implements BSTInterface {
    
    private class Node {
        public int valueOfTheNode;
        public Node rightChild;
        public Node leftChild;

        public Node(int value){
            this.valueOfTheNode = value;
            this.rightChild = null;
            this.leftChild = null;
        }
    }

    private long sum = 0;
    private int count = 0;

    private Node root;



    public BST() {
        this.root = null;
    }

    //Do not call it if the root is null
    private Node findAnItemsPlace(final int key, boolean returnTheParent){
        Node searcher = this.root;
        
        while (true){
            boolean shouldGoLeft = searcher.valueOfTheNode > key;
            boolean shouldGoRight = searcher.valueOfTheNode < key;
            boolean done = searcher.valueOfTheNode == key;

            if (shouldGoLeft){
                if (searcher.leftChild == null){
                    //The place to put him is as the left child of the current node
                    return searcher;
                }
                if (returnTheParent && searcher.leftChild.valueOfTheNode == key){
                    return searcher;
                }
                searcher = searcher.leftChild;
            }
            if (shouldGoRight){
                if (searcher.rightChild == null){
                    //The place to put him is as the left child of the current node
                    return searcher;
                }
                if (returnTheParent && searcher.rightChild.valueOfTheNode == key){
                    return searcher;
                }
                searcher = searcher.rightChild;
            }
            if (done){
                return searcher;
            }
        }
        
    }
  
    public final boolean contains(final int key) {
        if (this.root == null){
            return false;
        }
        System.out.println("contains was called");
        Node locationOfKey = findAnItemsPlace(key, false);
        if (locationOfKey == null){
            //Did not find the item
            System.out.println("This should never happen");
            return false;
        }
        if (locationOfKey.valueOfTheNode == key){
            //We found him!
            return true;
        }
        return false;
    }

    private void addSumAndCount(int key, boolean insert){
        if (insert){
            this.count +=1;
            this.sum +=key;
        }
        else{
            this.count -=1;
            this.sum -=key;
        }
    }

    public final boolean insert(final int key) {
        System.out.println("insert was called");
        if (this.root == null){
            this.root = new Node(key);
            this.addSumAndCount(key, true);
            return true;
        }
        Node locationOfKey = findAnItemsPlace(key, false);
        if (locationOfKey == null){
            //Did not find the item
            System.out.println("This should never happen");
            return false;
        }
        if (locationOfKey.valueOfTheNode == key){
            //We found him so we do not do anything since he is already in the set
            return false;
        }
        if (locationOfKey.valueOfTheNode > key){
            locationOfKey.leftChild = new Node(key);
            this.addSumAndCount(key, true);
            return true;
        }
        else{
            locationOfKey.rightChild = new Node(key);
            this.addSumAndCount(key, true);
            return true;
        }
    }


    //Returns the parent of the successor of the current node
    private Node findSuccessorsParent(Node current){
        Node prev = current;
        Node next = current.rightChild;
        while(true){
            if (next.leftChild == null){
                //Then next is the successor
                return prev;
            }
            Node temp = next;
            prev = next;
            next = next.leftChild;
        }
        return prev;
    }


    public final boolean remove(final int key) {
        if (this.root == null){
            return false;
        }
        if (this.root.valueOfTheNode == key){
            this.root = null;

            this.addSumAndCount(key, false);

        }
        System.out.println("remove was called");
        Node parentOfKey = findAnItemsPlace(key, true);
        if (parentOfKey == null){
            //Did not find the item
            System.out.println("This should never happen");
            return false;
        }
        if (parentOfKey.leftChild == null && parentOfKey.rightChild == null){
            //He is not in the set
            return false;
        }
        if (parentOfKey.leftChild == null){
            if (parentOfKey.rightChild.valueOfTheNode != key){
                //He is not in the set
                return false;
            }
        }
        if (parentOfKey.rightChild == null){
            if (parentOfKey.leftChild.valueOfTheNode != key){
                //He is not in the set
                return false;
            }
        }

        if (parentOfKey.leftChild.valueOfTheNode != key && parentOfKey.rightChild.valueOfTheNode != key){
            return false;
        }

        //Now we know that one of the children of parentOfKey is our node
        if (parentOfKey.rightChild.valueOfTheNode == key){
            Node ourNodeToRemove = parentOfKey.rightChild;
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild == null){
                parentOfKey.rightChild = null;
                this.addSumAndCount(key, false);
                return true;
            }
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild != null){
                parentOfKey.rightChild = ourNodeToRemove.leftChild;
                this.addSumAndCount(key, false);
                return true;
            }
            if (ourNodeToRemove.leftChild == null && ourNodeToRemove.rightChild !=null){
                parentOfKey.rightChild = ourNodeToRemove.rightChild;
                this.addSumAndCount(key, false);
                return true;
            }
            if (ourNodeToRemove.leftChild != null && ourNodeToRemove.rightChild != null){
                Node successorsParent = findSuccessorsParent(ourNodeToRemove);
                boolean leftChild = true;
                Node successor = successorsParent.leftChild;
                if (successor == null){
                    leftChild = false;
                    successor = successorsParent.rightChild;
                }
                parentOfKey.rightChild = successor;
                successor.rightChild = ourNodeToRemove.rightChild;
                successor.leftChild = ourNodeToRemove.leftChild;
                if (leftChild){
                    successorsParent.leftChild = null;
                }
                else{
                    successorsParent.rightChild = null;
                }
                this.addSumAndCount(key, false);
                return true;
            }
        }

        //Now we know that one of the children of parentOfKey is our node
        if (parentOfKey.leftChild.valueOfTheNode == key){
            Node ourNodeToRemove = parentOfKey.leftChild;
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild == null){
                parentOfKey.leftChild = null;
                this.addSumAndCount(key, false);
                return true;
            }
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild != null){
                parentOfKey.leftChild = ourNodeToRemove.leftChild;
                this.addSumAndCount(key, false);
                return true;
            }
            if (ourNodeToRemove.leftChild == null && ourNodeToRemove.rightChild !=null){
                parentOfKey.leftChild = ourNodeToRemove.rightChild;
                this.addSumAndCount(key, false);
                return true;
            }
            if (ourNodeToRemove.leftChild != null && ourNodeToRemove.rightChild != null){
                Node successorsParent = findSuccessorsParent(ourNodeToRemove);
                //This means that the successor is the left child of its parent
                boolean leftChild = true;
                Node successor = successorsParent.leftChild;
                if (successor == null){
                    leftChild = false;
                    successor = successorsParent.rightChild;
                }
                parentOfKey.leftChild = successor;
                successor.rightChild = ourNodeToRemove.rightChild;
                successor.leftChild = ourNodeToRemove.leftChild;
                if (leftChild){
                    successorsParent.leftChild = null;
                }
                else{
                    successorsParent.rightChild = null;
                }
                this.addSumAndCount(key, false);
                return true;
            }
        }
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
        return  this.count;
    }

    // Returns the sum of keys in the tree
    public final long getKeysum() {
    // NOTE: Guaranteed to be called without concurrent operations,
	// so no need to be thread-safe.
	//
	// Make sure to sum over a "long" variable or you will get incorrect
	// results due to integer overflow!
        System.out.println("getKeySum was called");
        return this.sum;
    }
}
