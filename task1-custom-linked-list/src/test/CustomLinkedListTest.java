package test;

import customLinkedList.CustomLinkedList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CustomLinkedListTest {
    private CustomLinkedList<Integer> list =  new CustomLinkedList<>();

    @Test
    public void testAddFirst(){
        list.addFirst(Integer.valueOf(1));
        list.addFirst(Integer.valueOf(2));

        assertEquals(2, list.size());
        assertEquals(Integer.valueOf(2), list.getFirst().getData());
        assertEquals(Integer.valueOf(1), list.getLast().getData());
    }

    @Test
    public void testAddLast(){
        list.addLast(Integer.valueOf(5));
        list.addLast(Integer.valueOf(10));

        assertEquals(2, list.size());
        assertEquals(Integer.valueOf(5), list.getFirst().getData());
        assertEquals(Integer.valueOf(10), list.getLast().getData());
    }

    @Test
    @DisplayName("Test for adding element by index ")
    public void testAddAtIndex() {
        list.addLast(Integer.valueOf(1));
        list.addLast(Integer.valueOf(3));
        list.add(1, Integer.valueOf(2));

        assertEquals(3, list.size());
        assertEquals(Integer.valueOf(1), list.get(0).getData());
        assertEquals(Integer.valueOf(2), list.get(1).getData());
        assertEquals(Integer.valueOf(3), list.get(2).getData());
    }

    @Test
    public void testGetByIndex() {
        list.addLast(Integer.valueOf(100));
        list.addLast(Integer.valueOf(200));
        list.addLast(Integer.valueOf(300));

        assertEquals(Integer.valueOf(200), list.get(1).getData());
    }

    @Test
    public void testRemoveFirst() {
        list.addLast(Integer.valueOf(10));
        list.addLast(Integer.valueOf(20));

        list.removeFirst();

        assertEquals(1, list.size());
        assertEquals(Integer.valueOf(20), list.getFirst().getData());
    }

    @Test
    public void testRemoveLast() {
        list.addLast(Integer.valueOf(10));
        list.addLast(Integer.valueOf(20));
        list.addLast(Integer.valueOf(30));

        list.removeLast();

        assertEquals(2, list.size());
        assertEquals(Integer.valueOf(20), list.getLast().getData());
    }

    @Test
    public void testRemoveByIndex() {
        list.addLast(Integer.valueOf(10));
        list.addLast(Integer.valueOf(20));
        list.addLast(Integer.valueOf(30));

        list.remove(1);

        assertEquals(2, list.size());
        assertEquals(Integer.valueOf(30), list.get(1).getData());
    }
}
