import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private class Node {
        private Item item;
        private Node next;
        private Node prev;
    }

    private class ListIterator implements Iterator<Item> {

        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {

            if (current == null) {
                throw new NoSuchElementException();
            }

            Item item = current.item;
            current = current.next;

            return item;
        }
    }

    private int count = 0;
    private Node first;
    private Node last;

    public Deque() {
    } // construct an empty deque

    public boolean isEmpty() {
        return (count == 0);
    } // is the deque empty?

    public int size() {
        return count;
    } // return the number of items on the deque

    public void addFirst(Item item) { // add the item to the front

        if (item == null) {
            throw new NullPointerException();
        }

        Node n = new Node();
        n.item = item;
        n.next = first;

        if (first != null) {
            first.prev = n;
        }

        first = n;

        if (last == null) {
            last = first;
        }

        count++;
    }

    public void addLast(Item item) { // add the item to the end

        if (item == null) {
            throw new NullPointerException();
        }

        Node n = new Node();
        n.item = item;
        n.prev = last;

        if (last != null) {
            last.next = n;
        }

        last = n;

        if (first == null) {
            first = last;
        }

        count++;
    }

    public Item removeFirst() { // remove and return the item from the front

        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Node n = first;
        Item item = n.item;

        first = n.next;
        if (first == null) {
            last = first;
        } else {
            first.prev = null;
        }
        count--;

        return item;
    }

    public Item removeLast() { // remove and return the item from the end

        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Node n = last;
        Item item = n.item;

        last = n.prev;
        if (last == null) {
            first = last;
        } else {
            last.next = null;
        }
        count--;

        return item;
    }

    public Iterator<Item> iterator() { // return an iterator over items in order
                                       // from front to end
        return new ListIterator();
    }

    public static void main(String[] args) {

    }

}
