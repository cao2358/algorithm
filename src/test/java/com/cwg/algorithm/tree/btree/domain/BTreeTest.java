package com.cwg.algorithm.tree.btree.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 创建时间：2020-08-12 09:48
 *
 * @author 曹文岗
 **/
class BTreeTest {

    private final List<Integer> keys = new ArrayList<>(12);
    private final List<List<Integer[]>> insertResults = new ArrayList<>(12);
    private final List<List<Integer[]>> deleteResults = new ArrayList<>(12);

    @Before
    public void before() throws Exception {
        int[] ks = new int[]{6, 18, 16, 22, 3, 12, 8, 10, 20, 21, 13, 17};
        for (int i = 0; i < ks.length; i++) {
            keys.add(i, ks[i]);
        }
    }

    @After
    public void after() throws Exception {
        keys.clear();
        insertResults.clear();
    }


    /**
     * Method: insert(K key)
     */
    @Test
    public void testInsert() throws Exception {
        BTree<Integer> tree = new BTree<>(2);
        fillInsertResult();
        for (int i = 0; i < keys.size(); i++) {
            tree.insert(keys.get(i));
            assertTrue(checkBTree(tree, insertResults.get(i)));
        }
    }

    private <K extends Comparable<K>> boolean checkBTree(BTree<K> tree, List<K[]> result)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        AbstractBTreeNode<K> node = tree.getRoot();
        Queue<AbstractBTreeNode<K>> nodes = new LinkedList<>();

        Class<?> clazz = node.getClass();
        Method nChild = clazz.getDeclaredMethod("nchild", null);
        Method getChild = clazz.getDeclaredMethod("getChild", int.class);
        Method nKey = clazz.getDeclaredMethod("nkey", null);
        Method getKey = clazz.getDeclaredMethod("getKey", int.class);
        nChild.setAccessible(true);
        getChild.setAccessible(true);
        nKey.setAccessible(true);
        getKey.setAccessible(true);

        int nodeIndex = 0;
        while (node != null) {
            //add children
            int nchild = (Integer) nChild.invoke(node, null);
            for (int i = 0; i < nchild; i++) {
                Object n = (AbstractBTreeNode<K>) getChild.invoke(node, nchild);
                if (n != null) {
                    nodes.offer((AbstractBTreeNode<K>) n);
                }
            }
            K[] nodeKeys = result.get(nodeIndex++);
            //compare keys
            for (int i = 0; i < nodeKeys.length; i++) {
                if (!getKey.invoke(node, i).equals(nodeKeys[i])) {
                    return false;
                }
            }
            node = nodes.poll();
        }
        return true;
    }

    private void fillInsertResult() {

        //tree values after insert keys
        Integer[][][] res = {
                {{6}},                                                            // 6
                {{6, 18}},                                                        // 18
                {{6, 16, 18}},                                                    // 16
                {{16}, {6}, {18, 22}},                                            // 22
                {{16}, {3, 6}, {18, 22}},                                         // 3
                {{16}, {3, 6, 12}, {18, 22}},                                     // 12
                {{6, 16}, {3}, {8, 12}, {18, 22}},                                // 8
                {{6, 16}, {3}, {8, 10, 12}, {18, 22}},                            // 10
                {{6, 16}, {3}, {8, 10, 12}, {18, 20, 22}},                        // 20
                {{6, 16, 20}, {3}, {8, 10, 12}, {18}, {21, 22}},                  // 21
                {{16}, {6, 10}, {20}, {3}, {8}, {12, 13}, {18}, {21, 22}},        // 13
                {{16}, {6, 10}, {20}, {3}, {8}, {12, 13}, {17, 18}, {21, 22}},    // 17
        };

        for (int i = 0; i < 12; i++) {
            insertResults.add(Arrays.asList(res[i]));
        }
    }

    private void fillDeleteResult() {
        //tree values after insert keys
        Integer[][][] res = {
                {{16}, {10}, {20}, {3, 8}, {12, 13}, {17, 18}, {21, 22}},                  // 6
                {{10, 16, 20}, {3, 8}, {12, 13}, {17}, {21, 22}},                          // 18
                {{10, 13, 20}, {3, 8}, {12}, {17}, {21, 22}},                              // 16
                {{10, 13, 20}, {3, 8}, {12}, {17}, {21}},                                  // 22
                {{10, 13, 20}, {8}, {12}, {17}, {21}},                                     // 3
                {{13, 20}, {8, 10}, {17}, {21}},                                           // 12
                {{13, 20}, {10}, {17}, {21}},                                              // 8
                {{20}, {13, 17}, {21}},                                                    // 10
                {{17}, {13}, {21}},                                                        // 20
                {{13, 17}},                                                                // 21
                {{17}},                                                                    // 13
                {{}},                                                                      // 17
        };

        for (int i = 0; i < 12; i++) {
            deleteResults.add(Arrays.asList(res[i]));
        }
    }

    /**
     * Method: delete(K key)
     */
    @Test
    public void testDelete() throws Exception {
        BTree<Integer> bTree = new BTree<>(2);
        for (int key : keys) {
            bTree.insert(key);
        }
        fillDeleteResult();

        for (int i = 0; i < keys.size(); i++) {
            bTree.delete(keys.get(i));
            assertTrue(checkBTree(bTree, deleteResults.get(i)));
        }
    }

}
