import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
/**
 * Your implementation of an AVL Tree.
 *
 * @author YIFAN SHEN
 * @userid yshen324
 * @GTID 903222059
 * @version 1.0
 */
public class AVL<T extends Comparable<? super T>> implements AVLInterface<T> {
    // DO NOT ADD OR MODIFY INSTANCE VARIABLES.
    private AVLNode<T> root;
    private int size;

    /**
     * A no argument constructor that should initialize an empty AVL tree.
     * DO NOT IMPLEMENT THIS CONSTRUCTOR!
     */
    public AVL() {
    }

    /**
     * Initializes the AVL tree with the data in the Collection. The data
     * should be added in the same order it is in the Collection.
     *
     * @param data the data to add to the tree
     * @throws IllegalArgumentException if data or any element in data is null
     */
    public AVL(Collection<T> data) {
        if (data == null) {
            throw new IllegalArgumentException("The collection is empty!");
        }
        for (T t : data) {
            if (t == null) {
                throw new IllegalArgumentException("Null data in collection.");
            }
            add(t);
        }
    }

    @Override
    public void add(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data about to add is null!");
        }
        if (root == null) {
            root = new AVLNode<>(data);
            size++;
        } else {
            root = addHelper(root, data);
        }
    }

    /**
     * Recursive helper method for add.
     * @param node the current root node
     * @param data the data to be added
     * @return the rotated and balanced root node
     */
    private AVLNode<T> addHelper(AVLNode<T> node, T data) {
        if (node  == null) {
            AVLNode<T> newNode = new AVLNode<T>(data);
            newNode.setBalanceFactor(0);
            newNode.setHeight(0);
            size++;
            return newNode;
        }
        if (node.getData().compareTo(data) < 0) {
            node.setRight(addHelper(node.getRight(), data));
        } else if (node.getData().compareTo(data) > 0) {
            node.setLeft(addHelper(node.getLeft(), data));
        }
        heightSetters(node);
        adjustBF(node);
        node = rebuild(node);
        return node;
    }

    /**
     * Helper method for reseting the height of nodes after
     * insertion and deletion.
     * @param node the node needs height recalculation
     */
    private void heightSetters(AVLNode<T> node) {
        if (node.getRight() == null && node.getLeft() == null) {
            node.setHeight(0);
        } else if (node.getRight() == null) {
            node.setHeight(node.getLeft().getHeight() + 1);
        } else if (node.getLeft() == null) {
            node.setHeight(node.getRight().getHeight() + 1);
        } else if (node.getRight().getHeight() >= node.getLeft().getHeight()) {
            node.setHeight(node.getRight().getHeight() + 1);
        } else {
            node.setHeight(node.getLeft().getHeight() + 1);
        }
    }

    /**
     * Helper method for reset the balance factor of current node.
     * @param node node to reset the balance factor
     */
    private void adjustBF(AVLNode<T> node) {
        int lh = -1;
        int rh = -1;
        if (node.getLeft() != null) {
            lh = node.getLeft().getHeight();
        }
        if (node.getRight() != null) {
            rh = node.getRight().getHeight();
        }
        node.setBalanceFactor(lh - rh);
    }

    /**
     * Rebuild the tree by testing which rotation should be opreated.
     * @param node current subtree root node
     * @return new root node after rotation
     */
    private AVLNode<T> rebuild(AVLNode<T> node) {
        if (node.getBalanceFactor() <= 1
            && node.getBalanceFactor() >= -1) {
            return node;
        } else if (node.getBalanceFactor() > 1) {
            if (node.getLeft().getBalanceFactor() < 0) {
                node = rotateLR(node);
            } else {
                node = rotateR(node);
            }
        } else {
            if (node.getRight().getBalanceFactor() <= 0) {
                node = rotateL(node);
            } else {
                node = rotateRL(node);
            }
        }
        return node;
    }

    /**
     * Single left rotation.
     * @param node current root node to be rotated to left
     * @return the middle node, also the new root node
     */
    private AVLNode<T> rotateL(AVLNode<T> node) {
        AVLNode<T> rotated = node.getRight();
        node.setRight(rotated.getLeft());
        rotated.setLeft(node);
        heightSetters(node);
        adjustBF(node);
        heightSetters(rotated);
        adjustBF(rotated);
        return rotated;
    }

    /**
     * Double rotation, first left then right.
     * @param node current root node to be Left-right rotated
     * @return the middle node, also the new root node
     */
    private AVLNode<T> rotateLR(AVLNode<T> node) {
        node.setLeft(rotateL(node.getLeft()));
        heightSetters(node);
        adjustBF(node);
        return rotateR(node);
    }

    /**
     * Double rotation, first right then left.
     * @param node current root node to be Right-left rotated
     * @return the middle node, also the new root node
     */
    private AVLNode<T> rotateRL(AVLNode<T> node) {
        node.setRight(rotateR(node.getRight()));
        heightSetters(node);
        adjustBF(node);
        return rotateL(node);
    }

    /**
     * Single right rotation.
     * @param node current root node to be rotated to right
     * @return the middle node, also the new root node
     */
    private AVLNode<T> rotateR(AVLNode<T> node) {
        AVLNode<T> rotated = node.getLeft();
        node.setLeft(rotated.getRight());
        rotated.setRight(node);
        heightSetters(node);
        adjustBF(node);
        heightSetters(rotated);
        adjustBF(rotated);
        return rotated;
    }

    @Override
    public T remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Cannot remove null data!");
        }
        if (root == null) {
            throw new NoSuchElementException("Cannot remove data"
                    + "from an empty AVL tree.");
        }
        AVLNode<T> dummy = new AVLNode<T>(null);
        root = removeHelper(root, data, dummy);
        size--;
        return dummy.getData();
    }

    /**
     * Recursive helper method for remove method, will rebuild the tree.
     * @throws java.util.NoSuchElementException if the data is not found
     * @param node current subtree root node
     * @param data the data to be removed
     * @param dummy dummy node to store the removed data
     * @return the node's parent node
     */
    private AVLNode<T> removeHelper(AVLNode<T> node, T data,
                                    AVLNode<T> dummy) {
        if (node.getData().compareTo(data) == 0) {
            dummy.setData(node.getData());
            if (node.getLeft() == null && node.getRight() == null) {
                return null;
            } else if (node.getLeft() == null) {
                return node.getRight();
            } else if (node.getRight() == null) {
                return node.getLeft();
            } else {
                AVLNode<T> pred = node.getLeft();
                if (pred.getRight() == null) {
                    node.setData(pred.getData());
                    node.setLeft(pred.getLeft());
                } else {
                    AVLNode<T> temp = new AVLNode<T>(null);
                    node.setLeft(getPred(pred, temp));
                    node.setData(temp.getData());
                }
            }
        } else if (node.getData().compareTo(data) < 0) {
            if (node.getRight() == null) {
                throw new java.util.NoSuchElementException("There is no"
                        + " such element");
            }
            node.setRight(removeHelper(node.getRight(), data, dummy));
        } else {
            if (node.getLeft() == null) {
                throw new java.util.NoSuchElementException("There is no"
                        + " such element");
            }
            node.setLeft(removeHelper(node.getLeft(), data, dummy));
        }
        heightSetters(node);
        adjustBF(node);
        node = rebuild(node);
        return node;
    }

    /**
     * Recursive helper method for getting the predecessor.
     * @param node current subtree root node
     * @param temp node for storing predecessor's data
     * @return the current node
     */
    private AVLNode<T> getPred(AVLNode<T> node, AVLNode<T> temp) {
        if (node.getRight() == null) {
            temp.setData(node.getData());
            return node.getLeft();
        } else {
            node.setRight(getPred(node.getRight(), temp));
            heightSetters(node);
            adjustBF(node);
            node = rebuild(node);
            return node;
        }
    }

    @Override
    public T get(T data) {
        if (data == null) {
            throw new IllegalArgumentException("The data is null!");
        }
        if (root == null) {
            throw new NoSuchElementException("Cannot get data"
                    + "from empty AVL!");
        }
        return getHelper(root, data).getData();
    }

    /**
     * Recursive helper method for get.
     * @throws java.util.NoSuchElementException if the data is not found
     * @param node current subtree root node
     * @param data the data to look for
     * @return the got node's data
     */
    private AVLNode<T> getHelper(AVLNode<T> node, T data) {
        if (data == null) {
            throw new IllegalArgumentException("The data is null!");
        }
        if (node == null) {
            throw new NoSuchElementException("No such data in the AVL.");
        }
        if (node.getData().equals(data)) {
            return node;
        } else if (node.getData().compareTo(data) < 0) {
            return getHelper(node.getRight(), data);
        } else {
            return getHelper(node.getLeft(), data);
        }
    }

    @Override
    public boolean contains(T data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    "Cannot check whether the AVL tree contains null data.");
        }
        try {
            AVLNode<T> returnNode = getHelper(root, data);
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    @Override
    public int size() {
        // DO NOT MODIFY THIS METHOD!
        return size;
    }

    @Override
    public List<T> preorder() {
        List<T> dataList = new ArrayList<T>();
        preHelper(root, dataList);
        return dataList;
    }

    /**
     * Recursive helper method for printing the AVLTREE in preorder.
     * @param node current subtree root node
     * @param list the list for storing nodes data in this subtree.
     */
    private void preHelper(AVLNode<T> node, List<T> list) {
        if (node != null) {
            list.add(node.getData());
            preHelper(node.getLeft(), list);
            preHelper(node.getRight(), list);
        }
    }

    @Override
    public List<T> postorder() {
        List<T> dataList = new ArrayList<T>();
        postHelper(root, dataList);
        return dataList;
    }

    /**
     * Recursive helper method for printing the AVLTREE in postorder.
     * @param node current subtree root node
     * @param list the list for storing nodes data in this subtree.
     */
    private void postHelper(AVLNode<T> node, List<T> list) {
        if (node != null) {
            postHelper(node.getLeft(), list);
            postHelper(node.getRight(), list);
            list.add(node.getData());
        }
    }

    @Override
    public List<T> inorder() {
        List<T> dataList = new ArrayList<T>();
        inHelper(root, dataList);
        return dataList;
    }

    /**
     * Recursive helper method for printing the AVLTREE in inorder.
     * @param node current subtree root node
     * @param list the list for storing nodes data in this subtree.
     */
    private void inHelper(AVLNode<T> node, List<T> list) {
        if (node != null) {
            inHelper(node.getLeft(), list);
            list.add(node.getData());
            inHelper(node.getRight(), list);
        }
    }

    @Override
    public List<T> levelorder() {
        List<T> dataList = new ArrayList<T>();
        Queue<AVLNode<T>> queue = new LinkedList<AVLNode<T>>();
        queue.add(root);
        return levelHelper(dataList, queue);
    }

    /**
     * Helper method for printing the AVLTREE in levelorder.
     * @param list the list for storing nodes data
     * @param queue the queue for arranging nodes level by level
     * @return the list with data nodes in level order
     */
    private List<T> levelHelper(List<T> list, Queue<AVLNode<T>> queue) {
        while (!queue.isEmpty()) {
            AVLNode<T> curr = queue.remove();
            if (curr != null) {
                list.add(curr.getData());
                if (curr.getLeft() != null) {
                    queue.add(curr.getLeft());
                }
                if (curr.getRight() != null) {
                    queue.add(curr.getRight());
                }
            }
        }
        return list;
    }

    @Override
    public List<T> listLeavesDescending() {
        ArrayList<T> datalist = new ArrayList<T>();
        return descendHelper(root, datalist);
    }

    /**
     * Recursice helper method for listing nodes in descenidng order.
     * @param node current subtree root node.
     * @param list the list for storing data
     * @return the list with this subtree data
     */
    private List<T> descendHelper(AVLNode<T> node, ArrayList<T> list) {
        if (node == null) {
            return list;
        } else if (node.getLeft() == null && node.getRight() == null) {
            return list;
        } else {
            descendHelper(node.getLeft(), list);
            descendHelper(node.getRight(), list);
            return list;
        }
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public int height() {
        if (root == null) {
            return -1;
        } else {
            return root.getHeight();
        }
    }

    @Override
    public AVLNode<T> getRoot() {
        // DO NOT MODIFY THIS METHOD!
        return root;
    }
}
