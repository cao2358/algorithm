package com.cwg.algorithm.tree.rbtree;

/**
 * 创建时间：2020-08-11 10:20
 *
 * @author 曹文岗
 **/

import com.cwg.algorithm.tree.rbtree.domain.RBTree;

/**
 * 红黑树(Red-Black Tree，简称R-B Tree)，它一种特殊的二叉查找树。
 * 红黑树是特殊的二叉查找树，意味着它满足二叉查找树的特征：任意一个节点所包含的键值，大于等于左孩子的键值，小于等于右孩子的键值。
 * 除了具备该特性之外，红黑树还包括许多额外的信息。
 *
 * 红黑树的每个节点上都有存储位表示节点的颜色，颜色是红(Red)或黑(Black)。
 * 红黑树的特性:
 * (1) 每个节点或者是黑色，或者是红色。
 * (2) 根节点是黑色。
 * (3) 每个叶子节点是黑色。 [注意：这里叶子节点，是指为空的叶子节点！]
 * (4) 如果一个节点是红色的，则它的子节点必须是黑色的。
 * (5) 从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。
 *
 * 关于它的特性，需要注意的是：
 * 第一，特性(3)中的叶子节点，是只为空(NIL或null)的节点。
 * 第二，特性(5)，确保没有一条路径会比其他路径长出俩倍。因而，红黑树是相对是接近平衡的二叉树。
 */
public class RBTreeTest {

    public static void main(String[] args) {
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
        System.out.println("root is: " + rbTree.getMRoot().getKey());

        rbTree.remove(4);
        System.out.println("root is: " + rbTree.getMRoot().getKey());
    }
}
