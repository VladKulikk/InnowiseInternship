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
            newNode.next = head;
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
            tail.next = newNode;
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
                newNode.next = temp.next;
                temp.next = newNode;
                break;
            }
            temp = temp.next;
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
            temp = temp.next;
            i++;
        }
        return temp;
    }

    public void removeFirst(){
        this.head = this.head.next;
        size--;
    }

    public void removeLast(){
        if(this.size == 1){
            this.head = null;
            this.tail = null;
        }
        else{
            Node<T> temp = this.head;
            while(temp.next != tail){
                temp = temp.next;
            }
            tail = temp;
            tail.next = null;
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
                temp.next = temp.next.next;
                size--;
            }
            temp = temp.next;
        }
    }

    public void show() {
        var cursor = this.head;
        while (cursor != null) {
            System.out.println(cursor.data);
            cursor = cursor.next;
        }
    }
}
