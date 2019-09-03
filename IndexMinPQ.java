import java.util.Iterator;
import java.util.NoSuchElementException;

public class IndexMinPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
    private int maxN; // maximum number of elements on pq
    private int n; // number of actual elements on pq
    private int[] pq; // binary heap array with 1-based indexing
    private int[] qp; // qp[pq[i]] = pq[qp[i]] = i
    private Key[] keys; // keys[i] is the priority of i

    public IndexMinPQ(int maxN) {
        this.maxN = maxN;
        n = 0;
        pq = new int[1 + maxN];
        qp = new int[1 + maxN];
        keys = (Key[]) new Comparable[maxN + 1];
        for (int i = 0; i < maxN + 1; i++) {
            qp[i] = -1;
        }
    }

    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Is i an index on this pq?
     */
    public boolean contains(int i) {
        if (i < 0 || i > maxN) throw new IllegalArgumentException();
        return qp[i] != -1;
    }

    public int size() {
        return n;
    }

    /**
     * Associate key with index i.
     */
    public void insert(int i, Key key) {
        if (i < 0 || i > maxN) throw new IllegalArgumentException();
        if (contains(i)) throw new IllegalArgumentException("index already in pq");
        n++;
        qp[i] = n;
        pq[n] = i;
        keys[i] = key;
        swim(n);
    }

    /**
     * Index associate with the minimum key.
     */
    public int minIndex() {
        if (n == 0) throw new NoSuchElementException("pq underflow");
        return pq[1];
    }

    public Key minKey() {
        if (n == 0) throw new NoSuchElementException("pq underflow");
        return keys[pq[1]];
    }

    /**
     * Remove the minimum key and return its index.
     */
    public int delMin() {
        if (n == 0) throw new NoSuchElementException("pq underflow");
        int min = pq[1];
        exch(1, n--);
        sink(1);
        qp[min] = -1;
        keys[min] = null;
        pq[n + 1] = -1;
        return min;
    }

    public Key keyOf(int i) {
        if (i < 0 || i > maxN) throw new IllegalArgumentException();
        if (!contains(i)) throw new NoSuchElementException("index not in pq");
        return keys[i];
    }

    /**
     * Change the value associate with the specific index.
     */
    public void changeKey(int i, Key key) {
        if (i < 0 || i >= maxN) throw new IllegalArgumentException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        keys[i] = key;
        swim(qp[i]);
        sink(qp[i]);
    }

    /**
     * Remove the key associated with index i.
     */
    public void delete(int i) {
        if (i < 0 || i >= maxN) throw new IllegalArgumentException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        int index = qp[i];
        exch(index, n--);
        swim(index);
        sink(index);
        keys[i] = null;
        qp[i] = -1;
    }

    private boolean greater(int i, int j) {
        return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }

    /**
     * Heap helper functions.
     */
    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j + 1)) {
                j++;
            }
            if (!greater(k, j)) {
                break;
            }
            exch(k, j);
            k = j;
        }
    }

    private void exch(int i, int j) {
        int temp = pq[i];
        pq[i] = pq[j];
        pq[j] = temp;
        qp[pq[i]] = i;
        qp[pq[j]] = j;
    }

    /**
     * Interator.
     */
    public Iterator<Integer> iterator() {return new HeapIterator();}

    private class HeapIterator implements Iterator<Integer> {
        // Use a new pq for iterating.
        private IndexMinPQ<Key> copy;
        public HeapIterator() {
            copy = new IndexMinPQ<>(pq.length - 1);
            for (int i = 1; i <= n; i++) {
                copy.insert(pq[i], keys[pq[i]]);
            }
        }

        public boolean hasNext() {
            return !copy.isEmpty();
        }

        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMin();
        }
    }

    /**
     * Unit test.
     */
    public static void main(String[] args) {
        String[] strings = {"it", "was", "the", "best", "of", "times", "it", "was", "the", "worst"};
        IndexMinPQ<String> pq = new IndexMinPQ<>(strings.length);
        for (int i = 0; i < strings.length; i++) {
            pq.insert(i, strings[i]);
        }
        while (!pq.isEmpty()) {
            int i = pq.delMin();
            System.out.println(i + ", " + strings[i]);
        }
        System.out.println();
        for (int i = 0; i < strings.length; i++) {
            pq.insert(i, strings[i]);
        }
        for (int idx: pq) {
            System.out.println(idx + ", " + strings[idx]);
        }
        while (!pq.isEmpty()) {
            pq.delMin();
        }
    }
}