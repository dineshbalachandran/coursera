import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private class ListIterator implements Iterator<Item> {

        private int[] r;
        private int cnt;

        public ListIterator() {
            r = new int[size()];
            for (int i = head; i < tail; i++) {
                r[i - head] = rn[i];
            }

            StdRandom.shuffle(r);
        }

        public boolean hasNext() {
            return cnt < r.length;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {

            if (cnt >= r.length) {
                throw new NoSuchElementException();
            }

            return a[r[cnt++]];
        }
    }

    private Item[] a = (Item[]) new Object[1];

    private int head = 0;
    private int tail = 0;
    private int[] rn = new int[1];

    public RandomizedQueue() {
    } // construct an empty randomized queue

    private void resize(int max) {

        Item[] temp = (Item[]) new Object[max];

        head = 0;
        tail = 0;

        for (int i = 0; i < a.length; i++) {
            if (a[i] != null) {
                temp[tail++] = a[i];
            }
        }
        a = temp;

        rn = new int[max];
        for (int i = 0; i < tail; i++)
            rn[i] = i;

        if (tail > 0)
            StdRandom.shuffle(rn, head, tail);
    }

    private void randomSwap(int[] ar, int lo, int hi) {

        int i = StdRandom.uniform(lo, hi);
        int temp = ar[i];
        ar[i] = ar[hi - 1];
        ar[hi - 1] = temp;
    }

    public boolean isEmpty() {
        return (size() == 0);
    } // is the queue empty?

    public int size() {
        return (tail - head);
    } // return the number of items on the queue

    public void enqueue(Item item) { // add the item

        if (item == null) {
            throw new NullPointerException();
        }

        if (tail == a.length)
            resize(2 * (tail - head + 1));
        a[tail] = item;
        rn[tail] = tail++;

        randomSwap(rn, head, tail);
    }

    public Item dequeue() { // remove and return a random item

        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Item item = a[rn[head]];
        a[rn[head++]] = null;

        if (size() > 0 && size() == a.length / 4)
            resize(a.length / 2);

        return item;
    }

    public Item sample() { // return (but do not remove) a random item

        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        int i = StdRandom.uniform(head, tail);

        Item item = a[rn[i]];

        return item;
    }

    public Iterator<Item> iterator() { // return an independent iterator over
                                       // items in random order
        return new ListIterator();
    }

}
