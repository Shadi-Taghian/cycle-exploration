import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Explore {
    public static ArrayList<int[][]> cycles = new ArrayList<>();
    public static  int[][][] distance;
    public static int[][][][] d;
    public static int[][][][] agents;
    public static ArrayList<AgentTime> agentMoves = new ArrayList<>();
    public static  int oo = 1000*1000;

    public static void main(String[] args) {
        System.out.println("Please enter the number of nodes: ");
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[][] staticCycle = fillStaticCycle(n);
        distance = new int[n][n][4*n];

        //Fill the distance array with infinity before calculation
        for(int i=0; i<n;i++){
            for(int j=0; j<n;j++){
                for (int k=0; k< 3*n; k++){
                    distance[i][j][k] = oo;
                }
            }
        }
        d = new int[n][n][2][4*n];
        agents = new int[n][n][2][4*n];

        //createTemporalCycle(staticCycle);
        //createTemporalCycleAllCycle(staticCycle);
        createTemporalCycleAllTheSame(staticCycle);
        cycleWriter(n);

        //Calculate the distance between every two nodes for all times
        for(int time=0;time<4*n;time++){
            for(int node =0; node<n; node++){
                temporalBFS(node, time, n);
            }
        }

        //Subtract the starting time in order to get the time it takes
        // to travel from the starting node to the destination node
        for(int l=0; l<4*n; l++){
            for(int m=0; m<n; m++){
                for(int o=0; o<n; o++){
                    distance[m][o][l] -= l;
                }
            }
        }

        tBFSWriter(n);
        explore(n);
        dpWriter(n);
        findPath(n);

        for(int i=0; i< agentMoves.size(); i++){
            System.out.println("time: " + agentMoves.get(i).getTime() + " node: " + agentMoves.get(i).getNode());
        }

        System.out.println("----------------------------------------");
        for(int t=0; t<2*n; t++){
            System.out.println("--------------------------t: " + t + "------------------------" );
            for(int k=0;k<2; k++){
                System.out.println("k = " + k);
                for (int i=0; i<n; i++){
                    for(int j=0; j<n; j++){
                        System.out.print(agents[i][j][k][t] + " ");
                    }
                    System.out.println(" ");
                }
            }
        }


    }

    /**
     *  Creates snapshots of temporal cycle using the static cycle
     *  */
    public static void createTemporalCycle(int[][] staticCycle){
        int n = staticCycle.length;
        Random rand = new Random();
        for(int i = 0; i< 4*n; i++){
            int rnd = rand.nextInt(n);
            int[][] cycle = copyArray(staticCycle,n);
            cycle[rnd][(rnd+1)%n] = 0;
            cycle[(rnd+1)%n][rnd] = 0;
            cycles.add(cycle);
        }

    }

    public static void createTemporalCycleAllCycle(int[][] staticCycle){
        int n = staticCycle.length;
        for(int i = 0; i< 4*n; i++){
            int[][] cycle = copyArray(staticCycle,n);
            cycles.add(cycle);
        }

    }

    public static void createTemporalCycleAllTheSame(int[][] staticCycle){
        int n = staticCycle.length;
        Random rand = new Random();
        int rnd = rand.nextInt(n);
        for(int i = 0; i< 4*n; i++){
            int[][] cycle = copyArray(staticCycle,n);
            cycle[rnd][(rnd+1)%n] = 0;
            cycle[(rnd+1)%n][rnd] = 0;
            cycles.add(cycle);
        }

    }

    public static int[][] copyArray(int[][] oldArr, int n){
        int[][] newArr = new int[n][n];
        for(int i=0; i<n; i++)
            for(int j=0; j<n; j++){
                newArr[i][j] = oldArr[i][j];
            }
        return newArr;
    }

    /**
     *  Creates the adjacency matrix of a static cycle
     *  */
    public static int[][] fillStaticCycle(int n){
        int[][] staticCycle = new int[n][n];
        for(int t=0; t<n; t++){
            for(int l=0; l<n; l++){
                staticCycle[t][l] = 0;
            }
        }
        for(int i=0; i<n; i++){
            staticCycle[i][(i+1)%n] = 1;
            staticCycle[(i+1)%n][i] = 1;
        }
        return staticCycle;
    }

    /**
     *  Runs temporal BFS on the snapshots of the temporal graph
     *  in order to calculate the shortest path between two nodes i and j starting at time t
     *  */
    public static void temporalBFS(int u, int t, int n){
        ArrayList<NodeTime> q = new ArrayList<>();
        q.add(new NodeTime(u,t - 1));
        int node;
        int time;

        distance[u][u][t] = t;
        while(!q.isEmpty()){
            node =  q.get(0).getNode();
            time = q.get(0).getTime();
            q.remove(0);
            for(int i=0; i<n; i++){
                int firstTime = findTheFirstTime(node, i, n, time);
                if(distance[u][i][t] > firstTime){
                    distance[u][i][t] = firstTime+1;

                    q.add(new NodeTime(i, firstTime));
                }

            }
        }
    }

    /**
     *  Finds the first time an edge appears between node u and i after time t
     *  */
    public static int findTheFirstTime(int u, int i, int n, int t){
        int time = oo;
        for(int p=t+1; p<4*n; p++){
            if(cycles.get(p)[u][i] == 1){
                time=p;
                break;
            }
        }
        return time;
    }


    /**
     *  Fills the dynamic programming array to calculate the minimum time for exploration.
     *  */
    public static void explore(int n){
        for(int t=0; t< 4*n; t++){
            for(int i=0; i<n; i++){
                for(int j=0; j<n; j++){
                    for(int k=0; k<2; k++){
                        d[i][j][k][t] = oo;
                    }
                }
            }
        }

        for(int x=0; x<4*n; x++){
            for(int y=1; y<n; y++){
                d[y][y-1][1][x] = 0;
                d[y][y-1][0][x] = 0;
            }
            d[0][n-1][1][x] = 0;
            d[0][n-1][0][x] = 0;
        }

        for(int t=(2*n); t>=0; t--){
            for(int p=2; p<=n; p++){
                for(int i=0; i<n; i++){
                    int j = decrement(i,n,p);
                    for(int k=0; k<2; k++){
                        int left, right;
                        if(k==0){
                            right = distance[i][increment(j,n,1)][t] +
                                    d[i][increment(j,n,1)][1][(t+distance[j][increment(j,n,1)][t])];
                            left = distance[i][decrement(i,n,1)][t] +
                                    d[decrement(i,n,1)][j][0][(t+distance[i][decrement(i,n,1)][t])];
                        }
                        else{
                            right = distance[j][increment(j,n,1)][t] +
                                    d[i][increment(j,n,1)][1][(t+distance[j][increment(j,n,1)][t])];
                            left = distance[j][decrement(i,n,1)][t] +
                                    d[decrement(i,n,1)][j][0][(t+distance[j][increment(j,n,1)][t])];
                        }
                        d[i][j][k][t] = (left < right) ? left : right;
                        agents[i][j][k][t] = (left < right) ? 0 : 1;
                    }
                }
            }
        }
    }

    public static void findRecursivePath(int left, int right, int direction, int time, int n){
        System.err.println( "t: "+time+" "+left + " " + right + " direction: " + direction+"   " +d[left][right][direction][time]);
        if(d[left][right][direction][time] == 0){
            int where = (direction==0)? left:right;
            addAgentMove(time, where);
            return;
        }
        System.out.println("------"+agents[left][right][direction][time]);
        if(agents[left][right][direction][time] == 0){
            if(direction == 0){
                addAgentMove(time, left);
                findRecursivePath(decrement(left, n, 1), right, direction, time+1, n);
            }
            else{
                addAgentMove(time, right);
                addAgentMove(time+ distance[right][left][time], left);
                findRecursivePath(decrement(left, n, 1), right,
                        0, time+distance[right][decrement(left, n, 1)][time], n);
            }
        }
        else{
            if(direction == 1){
                addAgentMove(time, right);
                findRecursivePath(left, increment(right, n, 1), direction, time+1, n);
            }
            else{
                addAgentMove(time, left);
                addAgentMove(time+distance[left][right][time], right);
                findRecursivePath(left, increment(right, n,1),
                        1, time+distance[left][increment(right, n, 1)][time], n);
            }
        }
    }

    public static void findPath(int n){
        int start = 0;
        int direction = 0;
        int min = oo;
        int time= 0;

        for(int i=0; i<n; i++){
            for(int k=0; k<2; k++){
                if(d[i][i][k][0]<min){
                    min = d[i][i][k][0];
                    start = i;
                    direction = k;
                }
            }
        }
        addAgentMove(time, start);
        if(agents[start][start][direction][time]==1){
            findRecursivePath(start,increment(start, n, 1), 1, time+1, n);
        }
        else{
            findRecursivePath(decrement(start, n, 1), start,0, time+1, n);
        }
        System.err.println(min);

    }

    public static void addAgentMove(int time, int node){
        AgentTime a = new AgentTime(time, node);
        agentMoves.add(a);
    }

    public static int increment(int number, int n, int howMuch){
        if((number + howMuch) >= n)
            return (number + howMuch)%(n);
        else
            return (number+howMuch);
    }
    public static int decrement(int number, int n, int howMuch){
        if((number-howMuch) < 0){
            return (number-howMuch + n)%(n);
        }
        else
            return (number-howMuch);
    }
    public static void cycleWriter(int n){
        try {
            File myObj = new File("temporal-cycle.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter("temporal-cycle.txt");
            for(int s=0; s<cycles.size();s++){
                //myWriter.write("Snapshot: " + s + "\n");
                for(int i=0; i<n; i++){
                    for(int j=0; j<n; j++){
                        myWriter.write(cycles.get(s)[i][j] + " ");
                    }
                    myWriter.write("\n");
                }
                myWriter.write("........................" + "\n");
            }
            myWriter.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void tBFSWriter(int n){
        try {
            File myObj = new File("distance.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter("distance.txt");
            for(int s=0; s<3*n;s++){
                myWriter.write("time: " + s + "\n");
                for(int i=0; i<n; i++){
                    for(int j=0; j<n; j++){
                        myWriter.write(distance[i][j][s] + " ");
                    }
                    myWriter.write("\n");
                }
                myWriter.write("........................" + "\n");
            }
            myWriter.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void dpWriter(int n){
        try {
            File myObj = new File("dp.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter("dp.txt");
            for(int s=0; s<=(2*n);s++){
                myWriter.write("time: " + s + "\n");

                for(int k=0; k<2; k++){
                    myWriter.write("k: " + k + "\n");
                    for(int i=0; i<n; i++){
                        for(int j=0; j<n; j++){
                            myWriter.write(d[i][j][k][s] + " ");
                        }
                        myWriter.write("\n");
                    }
                    myWriter.write("\n");
                }
                myWriter.write("........................" + "\n");
            }
            myWriter.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
