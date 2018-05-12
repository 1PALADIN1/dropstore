public class Queue {
    private String[] queue;
    private int size;
    private int head;   // leaves
    private int tail;   // comes
    private int items;

    public Queue(int size) {
        this.size = size;
        queue = new String[size];
        head = 0;
        tail = -1;
        items = 0;
    }

    public boolean isEmpty() {
        return items == 0;
    }

    public boolean isFull() {
        return items == size;
    }

    public int getSize() {
        return items;
    }

    public void insert(String i) {
        if (isFull()) {
            size *= 2;
            String[] tmpArr = new String[size];
            if (tail >= head) {
                System.arraycopy(queue, 0, tmpArr, 0, queue.length);
            } else {
                System.arraycopy(queue, 0, tmpArr, 0, tail + 1);
                System.arraycopy(queue, head, tmpArr, size - head - 1, queue.length - head);
                head = size - head - 1;
            }
            queue = tmpArr;
        }
        if (tail == size - 1)
            tail = -1;
        queue[++tail] = i;
        items++;
    }

    public String remove() {
        if (isEmpty())
            throw new RuntimeException("Queue is empty!");
        String temp = queue[head++];
        if (head == size)
            head = 0;
        items--;
        return temp;
    }

    public String peek() {
        return queue[head];
    }
}
