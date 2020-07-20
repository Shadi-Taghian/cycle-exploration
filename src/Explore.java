import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Explore {
    public static ArrayList<int[][]> cycles = new ArrayList<>();
    public static  int[][][] temporalBFS;
    public static int[][][][] d;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[][] staticCycle = fillStaticCycle(n);
        temporalBFS = new int[n][n][2*n];
        d = new int[n][n][n][4*n];
        createTemporalCycle(staticCycle);
        for(int t=0; t<2*n; t++){
            for(int s=0; s<n; s++){
                temporalBFS(s,t,n);
                System.out.println("okkkkkk");
            }
        }

//        explore(n);
//        for(int i=0; i<n; i++){
//            for(int j=0; j<n; j++){
//                for(int k=0; k<n; k++){
//                    for(int t=0; t<2*n; t++){
//                        System.out.println(d[i][j][k][t]);
//                    }
//                }
//            }
//        }
    }

    public static void createTemporalCycle(int[][] staticCycle){
        int n = staticCycle.length;
        Random rand = new Random();
        for(int i = 0; i< 3*n; i++){
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
        ArrayList<Integer> q = new ArrayList<>();
        boolean[] mark = new boolean[n];
        int[] firstTime = new int[n];
        q.add(u);
        mark[u] = true;
        firstTime[u] = t;
        int node;
        while(!q.isEmpty()){
            node =  q.get(0);
            q.remove(0);
            System.out.println(node);
            for(int i=0; i<n; i++){
                if((cycles.get(t)[node][i] == 1) && !mark[i]){
                    q.add(i);
                    mark[i] = true;
                    firstTime[i] = findTheFirstTime(u, i, n);
                }
            }
            t++;
        }
    }

    public static int findTheFirstTime(int u, int i, int n){
        int[][] d = fillStaticCycle(n);
        int t=0;
        for(int p=0; p<n; p++){
            if(cycles.get(p)[u][i] == 1){
                t=p;
                break;
            }
        }
        return t;
    }

    public static void explore(int n){
        for(int t=0; t< 2*n; t++){
            for(int i=0; i<n; i++){
                for(int j=0; j<n; j++){
                    for(int k=0; k<n; k++){
                        if(i==0 || j==0){
                            d[i][j][k][t] = 0;
                        }
                        int left = temporalBFS[j][j+1][t] + d[i][j+1][j+1][(t+temporalBFS[j][j+1][t])];
                        int right = temporalBFS[j][i-1][t] + d[i-1][j][i-1][(t+temporalBFS[j][j+1][t])];
                        d[i][j][k][t] = Math.min(left, right);
                    }
                }
            }
        }
    }
}
