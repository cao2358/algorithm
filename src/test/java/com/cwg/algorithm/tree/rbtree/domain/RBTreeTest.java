package com.cwg.algorithm.tree.rbtree.domain;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 创建时间：2020-08-12 09:51
 *
 * @author 曹文岗
 **/
public class RBTreeTest {

    @Test
    public void insert(){
        RBTree<Integer> rbTree = new RBTree<>();
        rbTree.insert(1);
        rbTree.insert(2);
        rbTree.insert(3);
        rbTree.insert(4);
        rbTree.insert(5);
        rbTree.insert(6);
        rbTree.insert(7);
        rbTree.insert(8);
        rbTree.insert(9);
        rbTree.insert(10);
        rbTree.insert(11);
        assertEquals(4, rbTree.getMRoot().getKey().intValue());

        rbTree.remove(4);
        assertEquals(5, rbTree.getMRoot().getKey().intValue());
    }

}
