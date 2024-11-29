package tracker.manager;

class Node<TASK> {

    public TASK data;
    public Node<TASK> next;
    public Node<TASK> prev;

    public Node(Node<TASK> prev, TASK data, Node<TASK> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}