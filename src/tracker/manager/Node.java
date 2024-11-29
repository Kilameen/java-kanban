package tracker.manager;

class Node<NodeTask> { // отдельный класс Node для узла списка

    public NodeTask data;
    public Node<NodeTask> next;
    public Node<NodeTask> prev;

    public Node(Node<NodeTask> prev, NodeTask data, Node<NodeTask> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}