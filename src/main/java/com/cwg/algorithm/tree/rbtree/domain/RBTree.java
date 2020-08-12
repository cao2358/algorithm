package com.cwg.algorithm.tree.rbtree.domain;

import lombok.Getter;

/**
 * 创建时间：2020-08-09 10:35
 *
 * @author 曹文岗
 **/
@Getter
public class RBTree<T extends Comparable<T>> {
    /**
     * 根节点
     */
    private RBTNode<T> mRoot;

    private static final boolean RED = false;
    private static final boolean BLACK = true;

    /**
     * 对红黑树的节点(x)进行左旋转
     * <p>
     * 左旋示意图(对节点x进行左旋)：
     * px                              px
     * /                               /
     * x                               y
     * /  \      --(左旋)-.             / \
     * lx   y                          x  ry
     * /   \                       /  \
     * ly   ry                     lx  ly
     */
    private void leftRotate(RBTNode<T> x) {
        //1、设置x的右节点为y
        RBTNode<T> y = x.right;

        //2、设置x的右节点为y的左节点
        x.right = y.left;

        //3、如果y的左节点非空，将x设置为y的左节点的父节点
        if (y.left != null) {
            y.left.parent = x;
        }

        //4、将x的父节点设为y的父节点
        y.parent = x.parent;

        //5、替换父节点的子节点
        if (x.parent == null) {//5.1 如果x的父节点为空，则将y设置为根节点
            this.mRoot = y;
        } else {
            if (x.parent.left == x) { //5.2 如果x是它父节点的左节点，则将y设置为x父节点的左节点
                x.parent.left = y;
            } else {
                x.parent.right = y; //5.3 否则将其设置为父节点的右节点
            }
        }

        //6、将x设置为y的左孩子
        y.left = x;

        //7、将x的父节点设置为y
        x.parent = y;
    }

    /**
     * 对红黑树的节点(y)进行右旋转
     * <p>
     * 右旋示意图(对节点y进行左旋)：
     * py                               py
     * /                                /
     * y                                x
     * /  \      --(右旋)-.              /  \
     * x   ry                           lx   y
     * / \                                   / \
     * lx  rx                                rx  ry
     */
    private void rightRotate(RBTNode<T> y) {
        //1 设置x是当前节点的左节点
        RBTNode<T> x = y.left;

        //2 将x的右节点设置为y的左节点
        y.left = x.right;
        //3 如果x的右节点不为空的话，将y设置为x右节点的父节点
        if (x.right != null) {
            x.right.parent = y;
        }

        //4 将y父节点设置为x父节点
        x.parent = y.parent;

        //5 替换父节点的子节点
        if (y.parent == null) {
            this.mRoot = x;
        } else {
            if (y == y.parent.right) {
                y.parent.right = x;
            } else {
                y.parent.left = x;
            }
        }

        //6 将y设置为x的右节点
        x.right = y;

        //将y父节点设置为x
        y.parent = x;
    }

    /**
     * 将结点插入到红黑树中
     * <p>
     * 参数说明：
     * node 插入的结点        // 对应《算法导论》中的node
     */
    private void insert(RBTNode<T> node) {
        int cmp;
        RBTNode<T> y = null;
        RBTNode<T> x = this.mRoot;

        // 1. 将红黑树当作一颗二叉查找树，将节点添加到二叉查找树中。
        while (x != null) {
            y = x;
            cmp = node.key.compareTo(x.key);
            if (cmp < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        node.parent = y;

        if (y != null) {
            cmp = node.key.compareTo(y.key);
            if (cmp < 0) {
                y.left = node;
            } else {
                y.right = node;
            }
        } else {
            this.mRoot = node;
        }

        // 2. 设置节点的颜色为红色
        node.color = RED;

        // 3. 将它重新修正为一颗二叉查找树
        insertFixUp(node);
    }

    /**
     * 新建结点(key)，并将其插入到红黑树中
     * <p>
     * 参数说明：
     * key 插入结点的键值
     */
    public void insert(T key) {
        RBTNode<T> node = new RBTNode<>(key, BLACK, null, null, null);

        // 如果新建结点失败，则返回。
        insert(node);
    }

    private void insertFixUp(RBTNode<T> node) {
        RBTNode<T> parent, gparent;

        //若父节点存在，且父节点的颜色是红色
        while ((parent = parentOf(node)) != null && isRed(parent)) {
            gparent = parentOf(parent);

            //若“父节点”是“祖父节点的左孩子”
            if (parent == gparent.left) {
                RBTNode<T> uncle = gparent.right;

                if (uncle != null && isRed(uncle)) {
                    setBlack(uncle);
                    setBlack(parent);
                    setRed(gparent);
                    node = gparent;
                    continue;
                }

                // Case 2条件：叔叔是黑色，且当前节点是右孩子
                if (parent.right == node) {
                    RBTNode<T> tmp;
                    leftRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }

                // Case 3条件：叔叔是黑色，且当前节点是左孩子。
                setBlack(parent);
                setRed(gparent);
                rightRotate(gparent);
            } else { //若“z的父节点”是“z的祖父节点的右孩子”
                // Case 1条件：叔叔节点是红色
                RBTNode<T> uncle = gparent.left;
                if (uncle != null && isRed(uncle)) {
                    setBlack(uncle);
                    setBlack(parent);
                    setRed(gparent);
                    node = gparent;
                    continue;
                }

                // Case 2条件：叔叔是黑色，且当前节点是左孩子
                if (parent.left == node) {
                    RBTNode<T> tmp;
                    rightRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }

                // Case 3条件：叔叔是黑色，且当前节点是右孩子。
                setBlack(parent);
                setRed(gparent);
                leftRotate(gparent);
            }
        }

        // 将根节点设为黑色
        setBlack(this.mRoot);
    }

    /**
     * 删除结点(node)，并返回被删除的结点
     *
     * 参数说明：
     *     node 删除的结点
     */
    private void remove(RBTNode<T> node) {
        RBTNode<T> child, parent;
        boolean color;

        // 被删除节点的"左右孩子都不为空"的情况。
        if ((node.left != null) && (node.right != null)) {
            // 被删节点的后继节点。(称为"取代节点")
            // 用它来取代"被删节点"的位置，然后再将"被删节点"去掉。
            RBTNode<T> replace = node;

            // 获取后继节点
            replace = replace.right;
            while (replace.left != null)
                replace = replace.left;

            // "node节点"不是根节点(只有根节点不存在父节点)
            if (parentOf(node) != null) {
                if (parentOf(node).left == node)
                    parentOf(node).left = replace;
                else
                    parentOf(node).right = replace;
            } else {
                // "node节点"是根节点，更新根节点。
                this.mRoot = replace;
            }

            // child是"取代节点"的右孩子，也是需要"调整的节点"。
            // "取代节点"肯定不存在左孩子！因为它是一个后继节点。
            child = replace.right;
            parent = parentOf(replace);
            // 保存"取代节点"的颜色
            color = colorOf(replace);

            // "被删除节点"是"它的后继节点的父节点"
            if (parent == node) {
                parent = replace;
            } else {
                // child不为空
                if (child != null)
                    setParent(child, parent);
                parent.left = child;

                replace.right = node.right;
                setParent(node.right, replace);
            }

            replace.parent = node.parent;
            replace.color = node.color;
            replace.left = node.left;
            node.left.parent = replace;

            if (color == BLACK)
                removeFixUp(child, parent);

            node = null;
            return;
        }

        if (node.left != null) {
            child = node.left;
        } else {
            child = node.right;
        }

        parent = node.parent;
        // 保存"取代节点"的颜色
        color = node.color;

        if (child != null)
            child.parent = parent;

        // "node节点"不是根节点
        if (parent != null) {
            if (parent.left == node)
                parent.left = child;
            else
                parent.right = child;
        } else {
            this.mRoot = child;
        }

        if (color == BLACK)
            removeFixUp(child, parent);
        node = null;
    }

    private void setParent(RBTNode<T> node, RBTNode<T> parent) {
        node.parent = parent;
    }

    private void removeFixUp(RBTNode<T> node, RBTNode<T> parent) {
        RBTNode<T> other;

        while ((node == null || isBlack(node)) && (node != this.mRoot)) {
            if (parent.left == node) {
                other = parent.right;
                if (isRed(other)) {
                    // Case 1: x的兄弟w是红色的
                    setBlack(other);
                    setRed(parent);
                    leftRotate(parent);
                    other = parent.right;
                }

                if ((other.left == null || isBlack(other.left)) &&
                        (other.right == null || isBlack(other.right))) {
                    // Case 2: x的兄弟w是黑色，且w的俩个孩子也都是黑色的
                    setRed(other);
                    node = parent;
                    parent = parentOf(node);
                } else {

                    if (other.right == null || isBlack(other.right)) {
                        // Case 3: x的兄弟w是黑色的，并且w的左孩子是红色，右孩子为黑色。
                        setBlack(other.left);
                        setRed(other);
                        rightRotate(other);
                        other = parent.right;
                    }
                    // Case 4: x的兄弟w是黑色的；并且w的右孩子是红色的，左孩子任意颜色。
                    setColor(other, colorOf(parent));
                    setBlack(parent);
                    setBlack(other.right);
                    leftRotate(parent);
                    node = this.mRoot;
                    break;
                }
            } else {

                other = parent.left;
                if (isRed(other)) {
                    // Case 1: x的兄弟w是红色的
                    setBlack(other);
                    setRed(parent);
                    rightRotate(parent);
                    other = parent.left;
                }

                if ((other.left == null || isBlack(other.left)) &&
                        (other.right == null || isBlack(other.right))) {
                    // Case 2: x的兄弟w是黑色，且w的俩个孩子也都是黑色的
                    setRed(other);
                    node = parent;
                    parent = parentOf(node);
                } else {

                    if (other.left == null || isBlack(other.left)) {
                        // Case 3: x的兄弟w是黑色的，并且w的左孩子是红色，右孩子为黑色。
                        setBlack(other.right);
                        setRed(other);
                        leftRotate(other);
                        other = parent.left;
                    }

                    // Case 4: x的兄弟w是黑色的；并且w的右孩子是红色的，左孩子任意颜色。
                    setColor(other, colorOf(parent));
                    setBlack(parent);
                    setBlack(other.left);
                    rightRotate(parent);
                    node = this.mRoot;
                    break;
                }
            }
        }

        if (node != null)
            setBlack(node);
    }

    private void setColor(RBTNode<T> node, boolean color) {
        node.color = color;
    }

    private boolean colorOf(RBTNode<T> node) {
        return node.color;
    }

    private boolean isBlack(RBTNode<T> node) {
        return node.color == BLACK;
    }

    /**
     * 删除结点(z)，并返回被删除的结点
     *
     * 参数说明：
     *     tree 红黑树的根结点
     *     z 删除的结点
     */
    public void remove(T key) {
        RBTNode<T> node;

        if ((node = search(mRoot, key)) != null)
            remove(node);
    }

    private RBTNode<T> search(RBTNode<T> mRoot, T key) {
        int cmp;
        RBTNode<T> x = mRoot;
        // 1. 将红黑树当作一颗二叉查找树，查找节点。
        while (x != null) {
            if (key.equals(x.key)) {
                return x;
            }
            cmp = key.compareTo(x.key);
            if (cmp < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        return null;
    }

    private void setRed(RBTNode<T> node) {
        node.color = RED;
    }

    private void setBlack(RBTNode<T> node) {
        node.color = BLACK;
    }

    private RBTNode<T> parentOf(RBTNode<T> node) {
        return node.parent;
    }

    private boolean isRed(RBTNode<T> node) {
        return node.color == RED;
    }

    @Getter
    public class RBTNode<T extends Comparable<T>> {
        /**
         * 颜色
         */
        boolean color;
        /**
         * 关键字（键值）
         */
        T key;
        /**
         * 左节点
         */
        RBTNode<T> left;
        /**
         * 右节点
         */
        RBTNode<T> right;
        /**
         * 父节点
         */
        RBTNode<T> parent;

        public RBTNode(T key, boolean color, RBTNode<T> parent, RBTNode<T> left, RBTNode<T> right) {
            this.key = key;
            this.color = color;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }
    }

}
