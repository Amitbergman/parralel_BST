package algorithms;

import java.util.concurrent.locks.ReentrantLock;

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

    private class finder{
        public boolean found;
        public boolean foundOnRoot;
        public Node parent;
        public boolean right;
        public Node child;

    }

    private final ReentrantLock lock = new ReentrantLock();

    private long sum = 0;
    private int count = 0;

    private Node root;

    public BST() {
        this.root = null;
    }

    //Do not call it if the root is null
    private finder findAnItemsPlace(final int key){
        Node searcher = this.root;

        if (searcher.valueOfTheNode == key){
            finder res = new finder();
            res.foundOnRoot = true;
            res.found = true;
            return res;
        }
        
        while (true){
            boolean shouldGoLeft = searcher.valueOfTheNode > key;
            boolean shouldGoRight = searcher.valueOfTheNode < key;
            if (shouldGoLeft){
                Node leftChild = searcher.leftChild;
                if (leftChild == null){
                    finder res = new finder();
                    res.foundOnRoot = false;
                    res.found = false;
                    res.parent = searcher;
                    res.right = false;
                    return res;
                }
                //Left child is not null
                if (leftChild.valueOfTheNode == key){
                    finder res = new finder();
                    res.foundOnRoot = false;
                    res.found = true;
                    res.parent = searcher;
                    res.right = false;
                    res.child = searcher.leftChild;
                    return res;
                }
                //We should just go left and search there
                if (searcher == searcher.leftChild){
                    System.out.println("Should never happen left");;
                }
                searcher = searcher.leftChild;
                
            }
            if (shouldGoRight){
                Node rightChild = searcher.rightChild;
                if (rightChild == null){
                    finder res = new finder();
                    res.foundOnRoot = false;
                    res.found = false;
                    res.parent = searcher;
                    res.right = true;
                    return res;
                }
                //Left child is not null
                if (rightChild.valueOfTheNode == key){
                    finder res = new finder();
                    res.foundOnRoot = false;
                    res.found = true;
                    res.parent = searcher;
                    res.right = true;
                    res.child = rightChild;
                    return res;
                }
                //We should just go left and search there
                 //We should just go left and search there
                if (searcher == searcher.rightChild){
                    System.out.println("Should never happen");;
                }
                searcher = searcher.rightChild;
            }
            
        }
        
    }
  
    public final boolean contains(final int key) {
        lock.lock();
        if (this.root == null){
            return false;
        }
        finder resOfFinder = findAnItemsPlace(key);
        lock.unlock();
        return resOfFinder.found;
    }

    private void addSumAndCount(int key, boolean insert){
        if (insert){
            this.count +=1;
            this.sum +=(long)key;
        }
        else{
            this.count -=1;
            this.sum -=(long)key;
        }
    }

    public final boolean insert(final int key) {
        lock.lock();
        Node newOne = new Node(key);
        if (this.root == null){
            this.root = newOne;
            this.addSumAndCount(key, true);
            lock.unlock();
            return true;
        }
        finder finderResult = findAnItemsPlace(key);

        if (finderResult.found){
            lock.unlock();
            return false;
        }

        if (finderResult.right){
            finderResult.parent.rightChild = newOne;
            this.addSumAndCount(key, true);
            lock.unlock();
            return true;
        }
        else{
            finderResult.parent.leftChild = newOne;
            this.addSumAndCount(key, true);
            lock.unlock();
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
            prev = next;
            next = next.leftChild;
        }
    }

    private void removeRoot(){
        if (this.root.leftChild == null && this.root.rightChild == null){
            this.root = null;
            return;
        }

        if (this.root.leftChild == null){
            //this means the right child is not null
            this.root = this.root.rightChild;
            return;
        }
        if (this.root.rightChild == null){
            //this means the left child is not null
            this.root = this.root.leftChild;
            return;
        }

        //Now we know that both are not null
        Node successorParent = this.findSuccessorsParent(this.root);

        if (successorParent == this.root){
            //this means that the root has right child with no left childs
            Node successor = this.root.rightChild;
            if (successor.leftChild != null){
                System.out.println("should not happen 12");
            }
            successor.leftChild = this.root.leftChild;
            this.root = successor;
            return;
        }

        //Now we know that the root has right child with left children
        Node successor = successorParent.leftChild;
        successorParent.leftChild = successor.rightChild;
        successor.rightChild = this.root.rightChild;
        successor.leftChild = this.root.leftChild;
        this.root = successor;
        return;

    }


    public final boolean remove(final int key) {
        lock.lock();
        if (this.root == null){
            lock.unlock();
            return false;
        }

        finder resultOfFinder = findAnItemsPlace(key);
        if (resultOfFinder.foundOnRoot){
            removeRoot();
            this.addSumAndCount(key, false);
            lock.unlock();
            return true;
        }

        if (resultOfFinder.found == false){
            //Did not find the item, so there is nothing to remove.
            lock.unlock();
            return false;
        }
        
        //Now we know that the item is in the set
        if (resultOfFinder.right){
            //the item is the right child of parent
            Node ourNodeToRemove = resultOfFinder.parent.rightChild;
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild == null){
                //The node to remove is a leaf, just cut it
                resultOfFinder.parent.rightChild = null;
                this.addSumAndCount(key, false);
                lock.unlock();

                return true;
            }
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild != null){
                //The node to remove has only left child
                resultOfFinder.parent.rightChild = ourNodeToRemove.leftChild;
                this.addSumAndCount(key, false);
                lock.unlock();

                return true;
            }
            if (ourNodeToRemove.leftChild == null && ourNodeToRemove.rightChild !=null){
                //The node to remove has only right child
                resultOfFinder.parent.rightChild = ourNodeToRemove.rightChild;
                this.addSumAndCount(key, false);
                lock.unlock();

                return true;
            }
            if (ourNodeToRemove.leftChild != null && ourNodeToRemove.rightChild != null){
                //The node to remove has both right and left children
                Node successorsParent = findSuccessorsParent(ourNodeToRemove);
                boolean successorIsLeftChildOfItsParent = true;
                Node successor = successorsParent.leftChild;
                if (successor == null){
                    successorIsLeftChildOfItsParent = false;
                    successor = successorsParent.rightChild;
                }
                if (successorsParent == ourNodeToRemove){
                    //This means that we are removing a node that is the direct parent of its successor
                    resultOfFinder.parent.rightChild = ourNodeToRemove.rightChild;
                    resultOfFinder.parent.rightChild.leftChild = ourNodeToRemove.leftChild;
                    this.addSumAndCount(key, false);
                    lock.unlock();

                    return true;
                }
                resultOfFinder.parent.rightChild = successor;
                if (successorIsLeftChildOfItsParent){
                    successorsParent.leftChild = successor.rightChild;
                }
                else{
                    successorsParent.rightChild = successor.rightChild;
                }
                successor.rightChild = ourNodeToRemove.rightChild;
                successor.leftChild = ourNodeToRemove.leftChild;
                this.addSumAndCount(key, false);
                lock.unlock();

                return true;
            }
        }

         //Now we know that the item is in the set
         if (!resultOfFinder.right){
            //the item is the left child of parent
            Node ourNodeToRemove = resultOfFinder.parent.leftChild;
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild == null){
                //The node to remove is a leaf, just cut it
                resultOfFinder.parent.leftChild = null;
                this.addSumAndCount(key, false);
                lock.unlock();

                return true;
            }
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild != null){
                //The node to remove has only left child
                resultOfFinder.parent.leftChild = ourNodeToRemove.leftChild;
                this.addSumAndCount(key, false);
                lock.unlock();

                return true;
            }
            if (ourNodeToRemove.leftChild == null && ourNodeToRemove.rightChild !=null){
                //The node to remove has only right child
                resultOfFinder.parent.leftChild = ourNodeToRemove.rightChild;
                this.addSumAndCount(key, false);
                lock.unlock();

                return true;
            }
            if (ourNodeToRemove.leftChild != null && ourNodeToRemove.rightChild != null){
                //The node to remove has both right and left children
                Node successorsParent = findSuccessorsParent(ourNodeToRemove);
                boolean successorIsLeftChildOfItsParent = true;
                Node successor = successorsParent.leftChild;
                if (successor == null){
                    successorIsLeftChildOfItsParent = false;
                    successor = successorsParent.rightChild;
                }
                if (successorsParent == ourNodeToRemove){
                    //This means that we are removing a node that is the direct parent of its successor
                    resultOfFinder.parent.leftChild = ourNodeToRemove.rightChild;
                    resultOfFinder.parent.leftChild.leftChild = ourNodeToRemove.leftChild;
                    this.addSumAndCount(key, false);
                    lock.unlock();

                    return true;
                }
                resultOfFinder.parent.leftChild = successor;
                if (successorIsLeftChildOfItsParent){
                    successorsParent.leftChild = successor.rightChild;
                }
                else{
                    successorsParent.rightChild = successor.rightChild;
                }
                successor.rightChild = ourNodeToRemove.rightChild;
                successor.leftChild = ourNodeToRemove.leftChild;
                this.addSumAndCount(key, false);
                lock.unlock();

                return true;
            }
        }

        //Now we know that one of the children of parentOfKey is our node
        lock.unlock();
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
        return  this.count;
    }

    // Returns the sum of keys in the tree
    public final long getKeysum() {
    // NOTE: Guaranteed to be called without concurrent operations,
	// so no need to be thread-safe.
	//
	// Make sure to sum over a "long" variable or you will get incorrect
	// results due to integer overflow!
        return this.sum;
    }
}
