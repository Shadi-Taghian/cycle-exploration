import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Explore {
    public static ArrayList<int[][]> cycles = new ArrayList<>();
    public static  int[][][] distance;
    public static int[][][][] d;
    public static  int oo = 1000*1000;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[][] staticCycle = fillStaticCycle(n);
        distance = new int[n][n][4*n];
        for(int i=0; i<n;i++){
            for(int j=0; j<n;j++){
                for (int k=0; k< 3*n; k++){
                    distance[i][j][k] = oo;
                }
            }
        }
        d = new int[n][n][2][4*n];
        createTemporalCycle(staticCycle);

        for(int time=0;time<4*n;time++){
            for(int node =0; node<n; node++){
                temporalBFS(node, time, n);
            }
        }

        for(int l=0; l<4*n; l++){
            for(int m=0; m<n; m++){
                for(int o=0; o<n; o++){
                    distance[m][o][l] -= l;
                }
            }
        }
        explore(n);
    }

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
    public static int[][] copyArray(int[][] oldArr, int n){
        int[][] newArr = new int[n][n];
        for(int i=0; i<n; i++)
            for(int j=0; j<n; j++){
                newArr[i][j] = oldArr[i][j];
            }
        return newArr;
    }
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
                int firstTime = findTheFirstTime(node, i, n, time)+1;
                if(distance[u][i][t] > firstTime){
                    distance[u][i][t] = firstTime;

                    q.add(new NodeTime(i, firstTime));
                }

            }
        }
    }

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

    public static void explore(int n){
        for(int x=0; x<2*n; x++){
            for(int y=0; y<n-1; y++){
                d[y][y+1][1][x] = distance[y][y+1][x];
            }
            d[n-1][0][1][x] = distance[n-1][0][x];
        }

        for(int t=0; t< 2*n; t++){
            for(int i=0; i<n; i++){
                for(int j=0; j<n-1; j++){
                    for(int k=0; k<2; k++){
                        int left, right;
                        if(k==0){
                            right = distance[i][increment(j,n)][t] +
                                    d[i][increment(j,n)][1][(t+distance[j][increment(j,n)][t])];
                            left = distance[i][decrement(i,n)][t] +
                                    d[decrement(i,n)][j][0][(t+distance[i][decrement(i,n)][t])];
                        }
                        else{
                            right = distance[j][increment(j,n)][t] +
                                    d[i][increment(j,n)][1][(t+distance[j][increment(j,n)][t])];
                            left = distance[j][decrement(i,n)][t] +
                                    d[decrement(i,n)][j][0][(t+distance[j][increment(j,n)][t])];
                        }
                        d[i][j][k][t] = (left < right) ? left : right;
                    }
                }
            }
        }
    }

    public static int increment(int number, int n){
        if(number == (n-1))
            return 0;
        else
            return (number+1);
    }
    public static int decrement(int number, int n){
        if(number == 0)
            return (n-1);
        else
            return (number-1);
    }
    public static void cycleWriter(int n){
        try {
            File myObj = new File("temporal-cycle.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter("filename.txt");
            for(int s=0; s<cycles.size();s++){
                myWriter.write("Snapshot: " + s );
                for(int i=0; i<n; i++){
                    for(int j=0; j<n; j++){
                        myWriter.write(cycles.get(s)[i][j] + " ");
                    }
                    myWriter.write("\n");
                }
            }
            myWriter.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
