import customLinkedList.CustomLinkedList;

public static void main(){
    CustomLinkedList customLinkedList = new CustomLinkedList();

    customLinkedList.addFirst(10);
    customLinkedList.addFirst(20);
    customLinkedList.addFirst(30);
    customLinkedList.addFirst(40);
    customLinkedList.addFirst(50);
    customLinkedList.addFirst(60);
    customLinkedList.addLast(100);
    customLinkedList.add(4,7);

    //customLinkedList.remove(4);

    //customLinkedList.get(4).show();

    customLinkedList.show();
}
