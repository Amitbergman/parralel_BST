package algorithms;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import main.BSTInterface;

public class BST implements BSTInterface {
    
    private class Node {
        public int valueOfTheNode;
        public Node rightChild;
        public Node leftChild;
        public ReentrantLock lock;
        public boolean deleted;
        public boolean isSentinel;

        public Node(int value, boolean isSentinel){
            this.valueOfTheNode = value;
            this.rightChild = null;
            this.leftChild = null;
            this.lock = new ReentrantLock();
            this.deleted = false;
            this.isSentinel = isSentinel;
        }
    }

    private class finder{
        public boolean found;
        public boolean foundOnRoot;
        public Node parent;
        public boolean right;
        public Node child;
    }

    private class resultOfInternalMethod{
        public boolean success;
        public boolean result;
        public resultOfInternalMethod(boolean success, boolean result){
            this.success = success;
            this.result = result;
        }
    }

    private Node sentinel;
    
    private final ReentrantLock lock = new ReentrantLock();

    private Node root;

    public BST() {
        this.root = null;
        this.sentinel = new Node(0, true);
        this.sentinel.rightChild = this.root;
    }

     

    private finder findAnItemsPlace(final int key){
        Node searcher = this.root;
        if (searcher == null){
            finder res = new finder();
            res.foundOnRoot = false;
            res.found = false;
            return res;
        }
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
                    res.found = leftChild.deleted == false;
                    res.parent = searcher;
                    res.right = false;
                    res.child = leftChild;
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
                //right child is not null
                if (rightChild.valueOfTheNode == key){
                    finder res = new finder();
                    res.foundOnRoot = false;
                    res.found = rightChild.deleted == false;
                    res.parent = searcher;
                    res.right = true;
                    res.child = rightChild;
                    return res;
                }
                //We should just go right and search there
                if (searcher == searcher.rightChild){
                    System.out.println("Should never happen right");;
                }
                searcher = searcher.rightChild;
            }
            
        }
        
    }
  
    public final boolean contains(final int key) {
        if (this.root == null){
            return false;
        }
        finder resOfFinder = findAnItemsPlace(key);
        if (resOfFinder.found){
            return true;
        }
        resOfFinder = findAnItemsPlace(key);
        return resOfFinder.found;
    }

    private final resultOfInternalMethod insertInternal(final int key){
        if (this.root == null){
            this.sentinel.lock.lock();
            //Sentinel is locked, we can check that the root is still null
            if (this.root == null){
                this.root =  new Node(key, false);;
                this.sentinel.rightChild = this.root;
                this.sentinel.lock.unlock();
                return new resultOfInternalMethod(true, true); //Success and the result is true
            }
            else{
                //Someone pushed something between the time we checked and the time we locked
                //We will try again
                this.sentinel.lock.unlock();
                return new resultOfInternalMethod(false, false);
            }

        }
        finder finderResult = findAnItemsPlace(key);

        if (finderResult.found){
            return new resultOfInternalMethod(true, false); //success and we did not insert
        }

        //The item was not found, we need to insert it

        finderResult.parent.lock.lock();
        if (finderResult.parent.deleted){
            finderResult.parent.lock.unlock();
            return new resultOfInternalMethod(false, false); //Someone deleted the parent we want to use
        }

        //Now we have the lock of the parent, and it is not deleted
        if (finderResult.right && finderResult.parent.rightChild != null){
            finderResult.parent.lock.unlock();
            return new resultOfInternalMethod(false, false); //Someone added a child in the place we wanted
        }

        if (finderResult.right == false && finderResult.parent.leftChild != null){
            finderResult.parent.lock.unlock();
            return new resultOfInternalMethod(false, false); //Someone added a child in the place we wanted
        }

        //Now we know that the place we want to push the child is empty, and the parent is not deleted
        //And we also have the lock on that parent so no one can remove him / add things below him

        if (finderResult.right){
            finderResult.parent.rightChild = new Node(key, false);
            finderResult.parent.lock.unlock();
            return new resultOfInternalMethod(true, true);
        }
        if (finderResult.right == false){ //wanna push it as left child
            finderResult.parent.leftChild = new Node(key, false);
            finderResult.parent.lock.unlock();
            return new resultOfInternalMethod(true, true);
        }

        System.out.println("should never happen 1232");
        return null;
    }

    public final boolean insert(final int key) {

        while (true){
            resultOfInternalMethod a = insertInternal(key);
            if (a.success == true){
                return a.result;
            }
        }
    }


    //Returns the parent of the successor of the current node and the successor himself
    private Node[] findSuccessorsAndParent(Node current){
        Node prev = current;
        Node next = current.rightChild;
        while(true){
            if (next.leftChild == null){
                //Then next is the successor
                return new Node[]{prev, next};
            }
            prev = next;
            next = next.leftChild;
        }
    }

    private void removeRoot(){
        //we have the sentinel lock and root lock when this is called
        if (this.root.leftChild == null && this.root.rightChild == null){
            this.root = null;
            this.sentinel.rightChild = null;
            return;
        }

        if (this.root.leftChild == null){
            //this means the right child is not null
            this.root = this.root.rightChild;
            this.sentinel.rightChild = this.root;
            return;
        }
        if (this.root.rightChild == null){
            //this means the left child is not null
            this.root = this.root.leftChild;
            this.sentinel.rightChild = this.root;
            return;
        }

        //Now we know that both are not null - we will replace the root with it's successor
        while (true){
            try {
                Node[] successorAndParent = this.findSuccessorsAndParent(this.root);
                Node parent = successorAndParent[0];
                Node successor = successorAndParent[1];
                parent.lock.lock();
                successor.lock.lock();
                if (successor.leftChild != null || (parent.rightChild != successor && parent.leftChild!=successor)){
                    //Soemthing is curropted, release locks and try again
                    parent.lock.unlock();
                    successor.lock.unlock();
                    throw new Exception();
                }
                //Now we have all the locks we need and we can replace the root with the successor
                if (parent == this.root){
                    //this means that the root has right child with no left children
                    if (successor.leftChild != null){
                        System.out.println("should not happen 12");
                    }
                    successor.leftChild = this.root.leftChild;
                    this.root = successor;
                    return;
                }
        
                //Now we know that the root has right child with left children
                parent.leftChild = successor.rightChild;
                successor.rightChild = this.root.rightChild;
                successor.leftChild = this.root.leftChild;
                this.root = successor;
                parent.lock.unlock();
                successor.lock.unlock();
                //Should mark here the successor as deleted
                return;

            } catch (Exception e) {
                continue;
            }
            finally {

            }
        }

    }

    public final boolean remove(final int key) {

        while (true){
            resultOfInternalMethod a = removeInternal(key);
            if (a.success == true){
                return a.result;
            }
        }
    }

    private final resultOfInternalMethod handleFoundOnRoot(final int key){
        this.sentinel.lock.lock();
        if (this.root == null){
            this.sentinel.lock.unlock();
            return new resultOfInternalMethod(false, false);
        }
        else{
            Node tempRoot = this.root;
            tempRoot.lock.lock();
            if (this.root.valueOfTheNode == key && this.root.deleted == false){
                //The item is still on the root and now that we have the lock of the root and of the sentinel so 
                //this cannot be changed
                this.root.deleted = true;
                removeRoot();
                tempRoot.lock.unlock();
                this.sentinel.lock.unlock();
                return new resultOfInternalMethod(true, true);

            }
            else{
                //Someone changed the root
                this.root.lock.unlock();
                this.sentinel.lock.unlock();
                return new resultOfInternalMethod(false, false);
            }
        }
    }


    public final resultOfInternalMethod removeInternal(final int key) {
        if (this.root == null){
            return new resultOfInternalMethod(true, false);
        }

        finder resultOfFinder = findAnItemsPlace(key);
        if (resultOfFinder.foundOnRoot){
            //The item is the root
            return handleFoundOnRoot(key);
            
        }

        if (resultOfFinder.found == false){
            //Did not find the item, so there is nothing to remove.
            return new resultOfInternalMethod(true, false);
        }
        
        //Now we know that the item is in the set and is not in the root

        resultOfFinder.parent.lock.lock();
        resultOfFinder.child.lock.lock();

        if(resultOfFinder.child.deleted || resultOfFinder.child.valueOfTheNode != key || resultOfFinder.parent.deleted){
            resultOfFinder.parent.lock.unlock();
            resultOfFinder.child.lock.unlock();
            //Someone deleted our item or changed it, try again
            return new resultOfInternalMethod(false, false);
        }

        if (resultOfFinder.parent.rightChild != resultOfFinder.child && resultOfFinder.parent.leftChild != resultOfFinder.child){
            resultOfFinder.parent.lock.unlock();
            resultOfFinder.child.lock.unlock();
            //Someone changed the parent's children, try again
            return new resultOfInternalMethod(false, false);
        }

        if (resultOfFinder.right){
            //the item is the right child of parent
            Node ourNodeToRemove = resultOfFinder.parent.rightChild;
            if (ourNodeToRemove != resultOfFinder.child){
                return new resultOfInternalMethod(false, false);
            }
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild == null){
                //The node to remove is a leaf, just cut it
                resultOfFinder.parent.rightChild = null;
                resultOfFinder.parent.lock.unlock();
                resultOfFinder.child.lock.unlock();
                return new resultOfInternalMethod(true, true);
            }
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild != null){
                //The node to remove has only left child
                resultOfFinder.parent.rightChild = ourNodeToRemove.leftChild;
                resultOfFinder.parent.lock.unlock();
                resultOfFinder.child.lock.unlock();
                return new resultOfInternalMethod(true, true);
            }
            if (ourNodeToRemove.leftChild == null && ourNodeToRemove.rightChild !=null){
                //The node to remove has only right child
                resultOfFinder.parent.rightChild = ourNodeToRemove.rightChild;
                resultOfFinder.parent.lock.unlock();
                resultOfFinder.child.lock.unlock();
                return new resultOfInternalMethod(true, true);
            }
            if (ourNodeToRemove.leftChild != null && ourNodeToRemove.rightChild != null){
                //The node to remove has both right and left children - we would like to replace him with his successor
                Node[] successorsAndParent = findSuccessorsAndParent(ourNodeToRemove);
                Node successorParent = successorsAndParent[0];
                Node successor = successorsAndParent[1];

                successorParent.lock.lock();
                successor.lock.lock();

                Node[] successorsAndParentAfterLock = findSuccessorsAndParent(ourNodeToRemove);

                if (successorParent != successorsAndParentAfterLock[0] || successor != successorsAndParentAfterLock[1]){
                    successorParent.lock.unlock();
                    successor.lock.unlock();

                    //Something went wrong, this is no longer the successor and his parent
                    return new resultOfInternalMethod(false, false);
                }
                
                if (successorParent == ourNodeToRemove){
                    ourNodeToRemove.deleted = true;
                    successor.leftChild = ourNodeToRemove.leftChild;
                    resultOfFinder.parent.rightChild = successor;

                    resultOfFinder.parent.lock.unlock();
                    resultOfFinder.child.lock.unlock();
                    successorParent.lock.unlock();
                    successor.lock.unlock();
                    return new resultOfInternalMethod(true, true);
                }

                ourNodeToRemove.valueOfTheNode = successor.valueOfTheNode;
                successorParent.leftChild = successor.rightChild;
                successor.rightChild = ourNodeToRemove.rightChild;
                successor.leftChild = ourNodeToRemove.leftChild;
                resultOfFinder.parent.rightChild = successor;
                resultOfFinder.parent.lock.unlock();
                resultOfFinder.child.lock.unlock();
                successorParent.lock.unlock();
                successor.lock.unlock();
                return new resultOfInternalMethod(true, true);
            }
        }

        if (resultOfFinder.right == false){
            //the item is the left child of parent
            Node ourNodeToRemove = resultOfFinder.parent.leftChild;
            if (ourNodeToRemove != resultOfFinder.child){
                return new resultOfInternalMethod(false, false);
            }
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild == null){
                //The node to remove is a leaf, just cut it
                resultOfFinder.parent.leftChild = null;
                resultOfFinder.parent.lock.unlock();
                resultOfFinder.child.lock.unlock();
                return new resultOfInternalMethod(true, true);
            }
            if (ourNodeToRemove.rightChild == null && ourNodeToRemove.leftChild != null){
                //The node to remove has only left child
                resultOfFinder.parent.leftChild = ourNodeToRemove.leftChild;
                resultOfFinder.parent.lock.unlock();
                resultOfFinder.child.lock.unlock();
                return new resultOfInternalMethod(true, true);
            }
            if (ourNodeToRemove.leftChild == null && ourNodeToRemove.rightChild !=null){
                //The node to remove has only right child
                resultOfFinder.parent.leftChild = ourNodeToRemove.rightChild;
                resultOfFinder.parent.lock.unlock();
                resultOfFinder.child.lock.unlock();
                return new resultOfInternalMethod(true, true);
            }
            if (ourNodeToRemove.leftChild != null && ourNodeToRemove.rightChild != null){
                //The node to remove has both right and left children - we would like to replace him with his successor
                Node[] successorsAndParent = findSuccessorsAndParent(ourNodeToRemove);
                Node successorParent = successorsAndParent[0];
                Node successor = successorsAndParent[1];

                successorParent.lock.lock();
                successor.lock.lock();

                Node[] successorsAndParentAfterLock = findSuccessorsAndParent(ourNodeToRemove);

                if (successorParent != successorsAndParentAfterLock[0] || successor != successorsAndParentAfterLock[1]){
                    successorParent.lock.unlock();
                    successor.lock.unlock();

                    //Something went wrong, this is no longer the successor and his parent
                    return new resultOfInternalMethod(false, false);
                }
                
                if (successorParent == ourNodeToRemove){
                    ourNodeToRemove.deleted = true;
                    successor.leftChild = ourNodeToRemove.leftChild;
                    resultOfFinder.parent.leftChild = successor;
                    
                    resultOfFinder.parent.lock.unlock();
                    resultOfFinder.child.lock.unlock();
                    successorParent.lock.unlock();
                    successor.lock.unlock();
                    return new resultOfInternalMethod(true, true);
                }

                ourNodeToRemove.valueOfTheNode = successor.valueOfTheNode;
                successorParent.leftChild = successor.rightChild;
                successor.rightChild = ourNodeToRemove.rightChild;
                successor.leftChild = ourNodeToRemove.leftChild;
                resultOfFinder.parent.leftChild = successor;
                resultOfFinder.parent.lock.unlock();
                resultOfFinder.child.lock.unlock();
                successorParent.lock.unlock();
                successor.lock.unlock();
                return new resultOfInternalMethod(true, true);
            }
        }
        return null;

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
        return  getSize(this.root);
    }

    private final int getSize(Node n){
        if (n == null){
            return 0;
        }
        return 1 + getSize(n.leftChild) + getSize(n.rightChild);
    }

    // Returns the sum of keys in the tree
    public final long getKeysum() {
    // NOTE: Guaranteed to be called without concurrent operations,
	// so no need to be thread-safe.
	//
	// Make sure to sum over a "long" variable or you will get incorrect
	// results due to integer overflow!
        return getKeysum(this.root);
    }

    private final long getKeysum(Node n){
        if (n == null){
            return 0;
        }
        return (long)n.valueOfTheNode + getKeysum(n.leftChild) + getKeysum(n.rightChild);
    }
}
