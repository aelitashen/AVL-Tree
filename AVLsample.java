import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Your implementation of an AVL Tree.
 *
 * @author Jinghua Zhang
 * @version 1.0
 */
public class AVLsample<T extends Comparable<? super T>> implements AVLInterface<T> {
    // DO NOT ADD OR MODIFY INSTANCE VARIABLES.
    private AVLNode<T> root;
    private int size;

    /**
     * A no argument constructor that should initialize an empty AVL tree.
     * DO NOT IMPLEMENT THIS CONSTRUCTOR!
     */
    public AVLsample() {
    }

    /**
     * Initializes the AVL tree with the data in the Collection. The data
     * should be added in the same order it is in the Collection.
     *
     * @param data the data to add to the tree
     * @throws IllegalArgumentException if data or any element in data is null
     */
    public AVLsample(Collection<T> data) {
        if (data == null) {
            throw new java.lang.IllegalArgumentException("data cannot be null");
        }
        for (T each : data) {
            if (each == null) {
                throw new java.lang.IllegalArgumentException("data"
                + " cannot be null");
            }
            add(each);
        }
    }

    @Override
    public void add(T data) {
        if (data == null) {
            throw new java.lang.IllegalArgumentException("data cannot be null");
        }
        if (size == 0) {
            root = new AVLNode<T>(data);
            size++;
        } else {
            root = addRecursion(root, data);
        }

    }

    /**
     * add the node and reset the subtree
     *
     * @param node the added node
     * @param data the data to add to the tree
     * @return the newly added node or the its parent node
     */
    private AVLNode<T> addRecursion(AVLNode<T> node, T data) {
        if (node == null) {
            AVLNode<T> newNode = new AVLNode<T>(data);
            newNode.setBalanceFactor(0);
            newNode.setHeight(0);
            size++;
            return newNode;
        }
        if (node.getData().compareTo(data) < 0) {
            node.setRight(addRecursion(node.getRight(), data));
        } else if (node.getData().compareTo(data) > 0) {
            node.setLeft(addRecursion(node.getLeft(), data));
        }
        getHeightHelper(node);
        changeBalanceFactor(node);
        node = rotateCheck(node);
        return node;
    }

    @Override
    public T remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        AVLNode<T> returnNode = new AVLNode<T>(null);
        if (root == null) {
            throw new java.util.NoSuchElementException("There is no"
                    + " such element");
        }
        root = removeRecursion(root, data, returnNode);
        size--;
        return returnNode.getData();

    }

    /**
     * Removes the data from the tree considering 3 different conditions.
     *
     * @throws java.util.NoSuchElementException if the data is not found
     * @param node current node to be compared
     * @param data the data to remove from the tree.
     * @param returnNode the node to be removed
     * @return the removed node's parent and reconstruct the subtree
     */
    private AVLNode<T> removeRecursion(AVLNode<T> node, T data,
                                       AVLNode<T> returnNode) {
        if (node.getData().compareTo(data) == 0) {
            returnNode.setData(node.getData());
            if (node.getLeft() == null && node.getRight() == null) {
                return null;
            } else if (node.getLeft() == null) {
                return node.getRight();
            } else if (node.getRight() == null) {
                return node.getLeft();
            } else {
                AVLNode<T> successor = node.getRight();
                if (successor.getLeft() == null) {
                    node.setData(successor.getData());
                    node.setRight(successor.getRight());
                } else {
                    AVLNode<T> rightNode = new AVLNode<T>(null);
                    node.setRight(getSuccessor(successor, rightNode));
                    node.setData(rightNode.getData());
                }
            }
        } else if (node.getData().compareTo(data) < 0) {
            if (node.getRight() == null) {
                throw new java.util.NoSuchElementException("There is no"
                        + " such element");
            }
            node.setRight(removeRecursion(node.getRight(), data, returnNode));
        } else {
            if (node.getLeft() == null) {
                throw new java.util.NoSuchElementException("There is no"
                        + " such element");
            }
            node.setLeft(removeRecursion(node.getLeft(), data, returnNode));
        }
        getHeightHelper(node);
        changeBalanceFactor(node);
        node = rotateCheck(node);
        return node;
    }

    /**
     * Return the successor which helps to replace the removed AVLNode.
     *
     * @param node the right node of the to-be removed AVLNode.
     * @param dataNode an AVLNode that contains the successor's data
     * @return the current AVLNode
     */
    private AVLNode<T> getSuccessor(AVLNode<T> node, AVLNode<T> dataNode) {
        if (node.getLeft() == null) {
            dataNode.setData(node.getData());
            return node.getRight();
        } else {
            node.setLeft(getSuccessor(node.getLeft(), dataNode));
            getHeightHelper(node);
            changeBalanceFactor(node);
            node = rotateCheck(node);
            return node;
        }
    }

    /**
     * Help to check whether the current node need to be rotated.
     *
     * @param node the current node to be checked whether need to be rotated.
     * @return the current AVLNode after rotation.
     */
    private AVLNode<T> rotateCheck(AVLNode<T> node) {
        if (node.getBalanceFactor() <= 1
            && node.getBalanceFactor() >= -1) {
            return node;
        } else if (node.getBalanceFactor() > 1
            && node.getLeft().getBalanceFactor() < 0) {
            node = rotateLeftRight(node);
        } else if (node.getBalanceFactor() > 1) {
            node = rotateRight(node);
        } else if (node.getBalanceFactor() < -1
            && node.getRight().getBalanceFactor() > 0) {
            node = rotateRightLeft(node);
        } else if (node.getBalanceFactor() < -1) {
            node = rotateLeft(node);
        }
        return node;
    }

    /**
     * Update the new balance factor of the node
     *
     * @param node current node to renew the balance factor
     */
    private void changeBalanceFactor(AVLNode<T> node) {
        int leftFactor = -1;
        int rightFactor = -1;
        if (node.getLeft() != null) {
            leftFactor = node.getLeft().getHeight();
        }
        if (node.getRight() != null) {
            rightFactor = node.getRight().getHeight();
        }
        node.setBalanceFactor(leftFactor - rightFactor);
    }

    /**
     * Correct and renew the height of each node.
     *
     * @param node current node to set the height
     */
    private void getHeightHelper(AVLNode<T> node) {
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
     * Left rotate the current node and return the middle node after rotation.
     *
     * @param node the most left node that need to be left rotated.
     * @return the middle node after rotation.
     */
    private AVLNode<T> rotateLeft(AVLNode<T> node) {
        AVLNode<T> returnNode = node.getRight();
        node.setRight(returnNode.getLeft());
        returnNode.setLeft(node);
        getHeightHelper(node);
        changeBalanceFactor(node);
        getHeightHelper(returnNode);
        changeBalanceFactor(returnNode);
        return returnNode;
    }

    /**
     * Right rotate the current node and return the middle node after rotation.
     *
     * @param node the most right node that need to be right rotated.
     * @return the middle node after rotation.
     */
    private AVLNode<T> rotateRight(AVLNode<T> node) {
        AVLNode<T> returnNode = node.getLeft();
        node.setLeft(returnNode.getRight());
        returnNode.setRight(node);
        getHeightHelper(node);
        changeBalanceFactor(node);
        getHeightHelper(returnNode);
        changeBalanceFactor(returnNode);
        return returnNode;
    }

    /**
     * Left-right rotate the current node and return the middle
     *node after rotation.
     *
     * @param node the node that need to be Left-right rotated.
     * @return the middle node after rotation.
     */
    private AVLNode<T> rotateLeftRight(AVLNode<T> node) {
        node.setLeft(rotateLeft(node.getLeft()));
        getHeightHelper(node);
        changeBalanceFactor(node);
        AVLNode<T> returnNode = rotateRight(node);
        //node =
        return returnNode;
    }

    /**
     * Right-left rotate the current node and
     * return the middle node after rotation.
     *
     * @param node the node that need to be Right-left rotated.
     * @return the middle node after rotation.
     */
    private AVLNode<T> rotateRightLeft(AVLNode<T> node) {
        node.setRight(rotateRight(node.getRight()));
        getHeightHelper(node);
        changeBalanceFactor(node);
        AVLNode<T> returnNode = rotateLeft(node);
        //node = rotateLeft(node);
        return returnNode;
    }

    @Override
    public T get(T data) {
        AVLNode<T> returnNode = getRecursion(root, data);
        if (returnNode == null) {
            throw new java.util.NoSuchElementException("There is no"
                    + " such element");
        }
        return returnNode.getData();
    }

    /**
     * To search the subtree to see whether the data is in the tree
     *
     * @throws IllegalArgumentException if the data is null
     * @param node the current node I am checking
     * @param data the data to search for in the tree.
     * @return the searched BSTNode
     */

    private AVLNode<T> getRecursion(AVLNode<T> node, T data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        if (node == null) {
            return null;
        }
        if (node.getData().equals(data)) {
            return node;
        } else if (node.getData().compareTo(data) < 0) {
            return getRecursion(node.getRight(), data);
        } else {
            return getRecursion(node.getLeft(), data);
        }
    }

    @Override
    public boolean contains(T data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        AVLNode<T> returnNode = getRecursion(root, data);
        return (returnNode != null);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<T> preorder() {
        List<T> dataList = new ArrayList<T>();
        preOrder(root, dataList);
        return dataList;
    }

    /**
     * To have a preorder traversal of the tree
     *
     * @param node the current node I am traversing
     * @param list the preorder traversal list of the tree
     */
    private void preOrder(AVLNode<T> node, List<T> list) {
        if (node != null) {
            list.add(node.getData());
            preOrder(node.getLeft(), list);
            preOrder(node.getRight(), list);
        }
    }

    @Override
    public List<T> postorder() {
        List<T> dataList = new ArrayList<T>();
        postOrder(root, dataList);
        return dataList;
    }

    /**
     * To have a postorder traversal of the tree
     *
     * @param node the current node I am traversing
     * @param list the postorder traversal list of the tree
     */
    private void postOrder(AVLNode<T> node, List<T> list) {
        if (node != null) {
            postOrder(node.getLeft(), list);
            postOrder(node.getRight(), list);
            list.add(node.getData());
        }
    }

    @Override
    public List<T> inorder() {
        List<T> dataList = new ArrayList<T>();
        inOrder(root, dataList);
        return dataList;
    }

    /**
     * To have an inorder traversal of the tree
     *
     * @param node the current node I am traversing
     * @param list the inorder traversal list of the tree
     */
    private void inOrder(AVLNode<T> node, List<T> list) {
        if (node != null) {
            inOrder(node.getLeft(), list);
            list.add(node.getData());
            inOrder(node.getRight(), list);
        }
    }

    @Override
    public List<T> levelorder() {
        List<T> dataList = new ArrayList<T>();
        Queue<AVLNode<T>> queue = new LinkedList<AVLNode<T>>();
        queue.add(root);
        levelOrder(dataList, queue);
        return dataList;
    }

    /**
     * To have a levelorder traversal of the tree
     *
     * @param queue a queue to track all the node
     * @param list the inorder traversal list of the tree
     */
    private void levelOrder(List<T> list, Queue<AVLNode<T>> queue) {
        while (queue.size() != 0) {
            AVLNode<T> remove = queue.remove();
            if (remove != null) {
                list.add(remove.getData());
                if (remove.getLeft() != null) {
                    queue.add(remove.getLeft());
                }
                if (remove.getRight() != null) {
                    queue.add(remove.getRight());
                }
            }
        }
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public int height() {
        if (size == 0) {
            return -1;
        } else {
            return root.getHeight();
        }
    }


    @Override
    public AVLNode<T> getRoot() {
        // DO NOT EDIT THIS METHOD!
        return root;
    }
    @Override
    public List<T> listLeavesDescending() {
        ArrayList<T> datalist = new ArrayList<T>();
        return descendHelper(root, datalist);
    }

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
}
