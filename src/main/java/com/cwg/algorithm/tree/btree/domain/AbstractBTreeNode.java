package com.cwg.algorithm.tree.btree.domain;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 创建时间：2020-08-11 10:29
 *
 * @author 曹文岗
 **/
public abstract class AbstractBTreeNode<K extends Comparable<K>> {
    protected final int degree;

    AbstractBTreeNode(int degree){
        if(degree < 2){
            throw new IllegalArgumentException("degree must >= 2");
        }

        this.degree = degree;
    }

    abstract boolean isLeaf();

    abstract K search(K key);

    abstract void insertNotFull(K key);

    abstract void deleteNotEmpty(K key);

    void insertKey(K key){
        checkNotFull();
        int i = this.nkey();
        //move keys
        while (i > 0 && key.compareTo(this.getKey(i - 1)) < 0){
            this.setKey(this.getKey(i - 1), i);
            i--;
        }

        this.setKey(key, i);
        this.setNKey(this.nkey()+1);
    }

    /**
     * <p>Delete given key in current node.</p>
     *
     * @param key the key to delete.
     * @return the key deleted or null if key does not exist.
     */
    protected K deleteKey(K key) {
        int index = indexOfKey(key);
        if (index >= 0) {
            return this.deleteKey(index);
        }
        return null;
    }

    /**
     * <p>Delete a key with given index.</p>
     *
     * @param index index of key to delete
     * @return key deleted or null if index is invalid.
     * @throws java.lang.RuntimeException if index is invalid
     */
    protected K deleteKey(int index) {
        if (index < 0 || index >= this.nkey()) {
            throw new RuntimeException("Index is invalid.");
        }
        K result = this.getKey(index);
        while (index < this.nkey() - 1) {
            this.setKey(this.getKey(index + 1), index);
            index++;
        }
        this.setKey(null, this.nkey() - 1);
        this.setNKey(this.nkey() - 1);
        return result;
    }

    /**
     * <p>Check if current exists given key</p>
     *
     * @param key key to check
     * @return true is given key exists in current node.
     */
    boolean existsKey(K key) {
        return indexOfKey(key) >= 0;
    }

    /**
     * Replace one key with newKey
     *
     * @param oldKey the key to replace
     * @param newKey the new key to insert in
     */
    protected void replaceKey(K oldKey, K newKey) {
        int index = indexOfKey(oldKey);
        if (index >= 0) {
            setKey(newKey, index);
        }
    }

    /**
     * Set one of current child with given index.
     *
     * @param sub   child subtree
     * @param index index of child to set
     */
    protected abstract void setChild(AbstractBTreeNode<K> sub, int index);

    /**
     * Insert a child at given index.
     *
     * @param sub   child subtree to insert
     * @param index index of child to insert
     */
    protected void insertChild(AbstractBTreeNode<K> sub, int index) {
        int i = this.nchild();
        while (i > index) {
            this.setChild(this.getChild(i - 1), i);
            i--;
        }
        this.setChild(sub, index);
        this.setNChild(this.nchild() + 1);
    }

    /**
     * Get child with given index.
     *
     * @param index index of child to get
     * @return child subtree of null if index is invalid
     */
    abstract AbstractBTreeNode<K> getChild(int index);

    /**
     * Delete given child in current node.
     *
     * @param child child subtree to delete.
     */
    protected void deleteChild(AbstractBTreeNode<K> child) {
        int index = -1;
        for (int i = 0; i < nchild(); i++) {
            if (this.getChild(i) == child) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            deleteChild(index);
        }
    }

    /**
     * Delete child with given index
     *
     * @param index index of child to delete
     * @return child subtree or null if index is invalid
     */
    protected AbstractBTreeNode<K> deleteChild(int index) {
        AbstractBTreeNode<K> result = null;
        if (index >= 0 && index < this.nchild()) {
            result = this.getChild(index);
            while (index < this.nchild() - 1) {
                this.setChild(this.getChild(index + 1), index);
                index++;
            }
            this.setChild(null, index);
            this.setNChild(this.nchild() - 1);
        }
        return result;
    }

    /**
     * Split a full child to two child node.
     *
     * @param child child index to split
     * @throws java.lang.RuntimeException is child to spilt is not full
     */
    protected abstract void splitChild(int child);

    /**
     * Split current node to two node.
     *
     * @param newNode new node
     * @return middle of current node before split
     * @throws java.lang.RuntimeException if current node is not full.
     */
    protected abstract K splitSelf(AbstractBTreeNode<K> newNode);

    /**
     * Merge current node with another .
     *
     * @param middle  middle key of the two node
     * @param sibling sibling node to merge
     * @throws java.lang.RuntimeException if keys of either node exceed degree-1.
     */
    protected abstract void merge(K middle, AbstractBTreeNode<K> sibling);

    /**
     * Child amount of current node.
     *
     * @return child amount.
     */
    abstract int nchild();

    /**
     * Set child amount of current node.
     *
     * @param nchild child amount.
     * @return old child amount.
     */
    protected abstract int setNChild(int nchild);

    /**
     * Get index of given key.
     *
     * @param key the key to get.
     * @return index of key or -1 if key does not exist in current node.
     */
    protected int indexOfKey(K key) {
        for (int i = 0; i < this.nkey(); i++) {
            if (key.equals(this.getKey(i))) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Check whether current node is full.
     *
     * @return true if current node is full,else false.
     */
    protected boolean isFull() {
        return nkey() == degree * 2 - 1;
    }

    /**
     * Check current node is not full.
     *
     * @throws java.lang.RuntimeException if current is full.
     */
    protected final void checkNotFull() {
        if (isFull()) {
            throw new RuntimeException(this.toString() + " is full.");
        }
    }

    /**
     * Set key amount of current node.
     *
     * @param nkey key amount to set
     * @return old key amount
     */
    protected abstract int setNKey(int nkey);

    /**
     * Key amount of current node.
     *
     * @return key amount of current node.
     */
    abstract int nkey();

    /**
     * <p>Get a key with index of {@code idx} in current node</p>
     *
     * @param idx idx of key to get.
     * @return key of given index
     * @throws java.lang.RuntimeException if {@code idx < 0 } or {@code idx >= degree *2 -1}
     */
    abstract K getKey(int idx);

    /**
     * Replace given index key with a new key
     *
     * @param newKey      the new key to insert in
     * @param oldKeyIndex old key index
     * @return the key be replaced or null if index is invalid
     */
    protected abstract K setKey(K newKey, int oldKeyIndex);

    /**
     * Recursively traverse the B-Tree,constitute a string.
     *
     * @param root root of B-Tree.
     * @param <K> Type of key of B-Tree
     * @return String of B-Tree.
     */
    static <K extends Comparable<K>> String BTreeToString(AbstractBTreeNode<K> root){
        StringBuffer sb = new StringBuffer();
        AbstractBTreeNode node;
        Queue<AbstractBTreeNode> queue = new LinkedList<>();
        queue.add(root);
        String newLine = System.getProperty("line.separator");
        while (!queue.isEmpty()){
            node = queue.poll();
            sb.append(node).append(newLine);
            int i = 0;
            while (node.getChild(i) != null){
                queue.offer(node.getChild(i));
                i++;
            }
        }
        return sb.toString();
    }
}
