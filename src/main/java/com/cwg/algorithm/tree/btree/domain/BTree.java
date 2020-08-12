package com.cwg.algorithm.tree.btree.domain;

/**
 * 创建时间：2020-08-12 09:46
 *
 * @author 曹文岗
 **/
public class BTree<K extends Comparable<K>> {

    private final int degree;
    private AbstractBTreeNode<K> root;

    public BTree(int degree){
        if (degree < 2){
            throw new IllegalArgumentException("degree mustn't < 2");
        }
        this.degree = degree;
        root = new BTreeLeaf<>(degree);
    }

    public AbstractBTreeNode<K> getRoot(){
        return root;
    }
    /**
     * Insert a key into B-Tree.
     *
     * @param key key to insert.
     */
    public void insert(K key){
        AbstractBTreeNode<K> n = root;
        if (root.isFull()){
            AbstractBTreeNode<K> newRoot = new BTreeInternalNode<>(degree);
            newRoot.insertChild(n,0);
            newRoot.splitChild(0);
            n = newRoot;
            root = newRoot;
        }
        n.insertNotFull(key);
    }

    /**
     * Delete a key from B-Tree,if key doesn't exist in current tree,will effect nothing.
     *
     * @param key key to delete.
     */
    public void delete(K key){
        AbstractBTreeNode<K> node = root;
        node.deleteNotEmpty(key);
        if (node.nkey() == 0){
            //shrink
            root = node.getChild(0);
            if (root == null){
                root = new BTreeLeaf<>(degree);
            }
        }
    }

    @Override
    public String toString() {
        return AbstractBTreeNode.BTreeToString(this.root);
    }

}
