package nlp;

import java.util.Arrays;

public class extracredit {


    public static void main(String args[]){

        int n = 5;
        int[] A = {1,2,3,4,5};
        int[][] B = new int[n][n];

        for(int i = 0; i < n; i++)
        {
            int sum = A[i];
            for(int j = i+1; j < n; j++)
            {
                sum = sum + A[j];
                B[i][j] = sum;
            }
        }
        System.out.println(Arrays.deepToString(B));
        B = new int[n][n];
        for(int i = 0; i < n; i++){
            for(int j = i + 1; j < n; j++){
                int sum = 0;
                for(int start = i; start <= j; start++)
                    sum += A[start];
                B[i][j] = sum;
            }
        }
        System.out.println(Arrays.deepToString(B));

    }


}
