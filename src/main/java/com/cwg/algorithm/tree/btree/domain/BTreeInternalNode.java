package com.cwg.algorithm.tree.btree.domain;

/**
 * 创建时间：2020-08-12 09:45
 *
 * @author 曹文岗
 **/
class BTreeInternalNode<K extends Comparable<K>> extends AbstractBTreeNode<K> {

    private final Object[] keys;
    private final AbstractBTreeNode<K>[] children;
    private int nkey = 0;
    private int nchild = 0;

    BTreeInternalNode(int degree) {
        super(degree);
        keys = new Object[2 * degree - 1];
        children = new AbstractBTreeNode[2 * degree];
    }

    @Override
    protected boolean isLeaf() {
        return false;
    }


    @Override
    K getKey(int idx) {
        return (K) keys[idx];
    }

    @Override
    protected K setKey(K newKey, int oldKeyIndex) {
        K old = (K) keys[oldKeyIndex];
        keys[oldKeyIndex] = newKey;
        return old;
    }

    @Override
    protected void setChild(AbstractBTreeNode<K> sub, int index) {
        children[index] = sub;
    }

    @Override
    AbstractBTreeNode<K> getChild(int index) {
        if (index >= 0 && index < children.length) {
            return children[index];
        }
        return null;
    }

    @Override
    protected int setNKey(int nkey) {
        int old = this.nkey;
        this.nkey = nkey;
        return old;
    }

    @Override
    int nkey() {
        return nkey;
    }

    @Override
    int nchild() {
        return nchild;
    }

    @Override
    protected int setNChild(int nchild) {
        int old = this.nchild;
        this.nchild = nchild;
        return old;
    }


    @Override
    K search(K key) {
        int index = indexOfKey(key);
        if (index >= 0) {
            return (K) keys[index];
        }
        index = 0;
        while (key.compareTo((K) keys[index++]) > 0) ;
        return children[index].search(key);
    }

    @Override
    void insertNotFull(K key) {
        checkNotFull();
        int i = 0;
        while (i < nkey && key.compareTo((K) keys[i]) >= 0) {
            i++;
        }
        if (this.children[i].isFull()) {
            this.splitChild(i);
            if (key.compareTo((K) this.keys[i]) > 0) {
                i++;
            }
        }

        this.children[i].insertNotFull(key);
    }

    @Override
    void deleteNotEmpty(K key) {
        //key in this node
        if (this.existsKey(key)) {
            int index = indexOfKey(key);
            AbstractBTreeNode<K> node;
            //predecessor child could delete
            if ((node = children[index]).nkey() >= degree) {
                K repKey = node.getKey(node.nkey() - 1);                  //maximum key in predecessor
                node.deleteNotEmpty(repKey);
                setKey(repKey, index);
            }
            //follow child could delete a key
            else if ((node = children[index + 1]).nkey() >= degree) {
                K repKey = node.getKey(0);                              //minimum key in follow
                node.deleteNotEmpty(repKey);
                setKey(repKey, index);
            }

            //merge predecessor with follow
            else {
                node = children[index];
                node.merge(key, children[index + 1]);
                this.deleteKey(index);
                this.deleteChild(index + 1);
                node.deleteNotEmpty(key);
            }
        }

        //key may exist in child
        else{
            int i = 0;
            //find proper child the key may exists in
            while (i < nkey) {
                if (key.compareTo((K) keys[i]) < 0)
                    break;
                i++;
            }
            AbstractBTreeNode<K> target = children[i];
            //child has enough key
            if (target.nkey() >= degree) {
                target.deleteNotEmpty(key);
            } else {
                AbstractBTreeNode<K> sibling;
                //try to find replacement from predecessor
                if (i > 0 && (sibling = children[i - 1]).nkey() >= degree) {
                    if (!target.isLeaf()) {
                        AbstractBTreeNode<K> sub = sibling.deleteChild(nchild); //last child
                        target.insertChild(sub, 0);
                    }
                    K repKey = sibling.deleteKey(sibling.nkey() - 1);    //maximum key
                    repKey = setKey(repKey, i - 1);
                    target.insertKey(repKey);
                    target.deleteNotEmpty(key);
                }
                //try to find replacement from follower
                else if (i < nkey && (sibling = children[i + 1]).nkey() >= degree) {
                    if (!target.isLeaf()) {
                        AbstractBTreeNode<K> sub = sibling.deleteChild(0);  //first child
                        target.insertChild(sub, target.nchild());
                    }
                    K repKey = sibling.deleteKey(0);                    //minimum key
                    repKey = setKey(repKey, i);
                    target.insertKey(repKey);
                    target.deleteNotEmpty(key);
                }
                //merge child with one of it's sibling
                else {
                    //merge with predecessor sibling
                    if (i > 0) {
                        K repKey = this.deleteKey(i - 1);
                        sibling = children[i - 1];
                        sibling.merge(repKey, target);
                        this.deleteChild(target);
                        sibling.deleteNotEmpty(key);
                    } else {
                        K repKey = this.deleteKey(i);
                        sibling = children[i + 1];
                        target.merge(repKey, sibling);
                        deleteChild(i + 1);
                        target.deleteNotEmpty(key);
                    }
                }
            }
        }

    }

    @Override
    protected void splitChild(int child) {
        AbstractBTreeNode<K> old = children[child];
        AbstractBTreeNode<K> neo = old.isLeaf()
                ? new BTreeLeaf<>(degree)
                : new BTreeInternalNode<>(degree);
        K middle = old.splitSelf(neo);
        this.insertKey(middle);
        this.insertChild(neo, child + 1);
    }

    @Override
    protected K splitSelf(AbstractBTreeNode<K> newNode) {
        if (!(newNode instanceof BTreeInternalNode)) {
            throw new RuntimeException("Instance not match.");
        }
        if (!isFull()) {
            throw new RuntimeException("Node is not full");
        }

        K middle = (K) keys[degree - 1];
        BTreeInternalNode<K> node = (BTreeInternalNode) newNode;
        int i = 0;
        while (i < degree - 1) {
            node.keys[i] = this.keys[i + degree];
            this.keys[i + degree] = null;
            i++;
        }
        this.keys[degree - 1] = null;

        i = 0;
        while (i < degree) {
            node.children[i] = this.children[i + degree];
            this.children[i + degree] = null;
            i++;
        }

        this.nkey = degree - 1;
        node.nkey = degree - 1;
        this.nchild = degree;
        node.nchild = degree;
        return middle;
    }

    @Override
    protected void merge(K middle, AbstractBTreeNode<K> sibling) {
        if (!(sibling instanceof BTreeInternalNode)) {
            throw new RuntimeException("Sibling is not leaf node");
        }
        BTreeInternalNode node = (BTreeInternalNode) sibling;
        this.insertKey(middle);
        for (int i = 0; i < node.nkey(); i++) {
            this.insertKey((K) node.keys[i]);
        }
        for (int i = 0; i < node.nchild(); i++) {
            insertChild(node.children[i], i + degree);
        }
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" internal node ---- ").append("size: ").append(nkey).append(" keys:").append("[");
        for (int i = 0; i < nkey; i++) {
            sb.append(keys[i]).append(",");
        }
        sb.append("]");
        return sb.toString();
    }

}
