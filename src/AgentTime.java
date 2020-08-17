public class AgentTime {

    private int time;
    private int node;

    public AgentTime(int t, int n){
        this.node = n;
        this.time = t;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }
}
