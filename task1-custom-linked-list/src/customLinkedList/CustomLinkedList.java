package customLinkedList;

import node.Node;

public class CustomLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;


    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public int size(){
        return this.size;
    }

    public void addFirst(T data){
        Node<T> newNode = new Node<>(data);
        if (this.size == 0){
            this.head = newNode;
            this.tail = newNode;
        }
        else{
            newNode.setNext(head);
            head = newNode;
        }
        this.size++;

    }

    public void addLast(T data){
        Node<T> newNode = new Node<>(data);
        if (this.size == 0){
            this.head = newNode;
            this.tail = newNode;
        }
        else{
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    public void add(int index, T data){
        if (this.size < index){
            throw new IndexOutOfBoundsException(STR."Index: \{index}, Size: \{this.size}");
        }
        Node<T> newNode = new Node<>(data);
        Node<T> temp = head;
        for (int i = 0; i < index; i++) {
            if (i == index - 1){
                newNode.setNext(temp.getNext());
                temp.setNext(newNode);
                break;
            }
            temp = temp.getNext();
        }
        size++;
    }

    public Node<T> getFirst(){
        return this.head;
    }

    public Node<T> getLast(){
        return this.tail;
    }

    public Node<T> get(int index){
        if (this.size < index){
            throw new IndexOutOfBoundsException(STR."Index: \{index}, Size: \{this.size}");
        }
        Node <T> temp = head;
        int i = 0;
        while(i < index){
            temp = temp.getNext();
            i++;
        }
        return temp;
    }

    public void removeFirst(){
        this.head = this.head.getNext();
        size--;
    }

    public void removeLast(){
        if(this.size == 1){
            this.head = null;
            this.tail = null;
        }
        else{
            Node<T> temp = this.head;
            while(temp.getNext() != tail){
                temp = temp.getNext();
            }
            tail = temp;
            tail.setNext(null);
        }
        size--;
    }

    public void remove(int index){
        if (this.size < index){
            throw new IndexOutOfBoundsException(STR."Index: \{index}, Size: \{this.size}");
        }
        Node<T> temp = head;
        for (int i = 0; i < index; i++) {
            if (i == index - 1){
                temp.setNext(temp.getNext().getNext());
                size--;
            }
            temp = temp.getNext();
        }
    }

    public void show() {
        var cursor = this.head;
        while (cursor != null) {
            System.out.println(cursor.getData());
            cursor = cursor.getNext();
        }
    }
}
