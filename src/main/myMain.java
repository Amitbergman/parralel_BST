package main;
import java.util.concurrent.ThreadLocalRandom;

// nextInt is normally exclusive of the top value,
// so add 1 to make it inclusive
import algorithms.BST;

public class myMain {

    public static void main(String[] args) throws Exception {
        long sum = 0;
        BST a = new BST();
        int numberOfRuns = 30000;

        int[] array = new int[numberOfRuns];
        int[] arrayOperations = new int[numberOfRuns];

        int timeOfFailure = 0;
        for (int i = 0; i < numberOfRuns; i ++){
            int randomNum = ThreadLocalRandom.current().nextInt(0, 100);
            int randomNum2 = ThreadLocalRandom.current().nextInt(0, 3);
            array[i] = randomNum;
            arrayOperations[i] = randomNum2;
            boolean res;
            if (randomNum2 < 1){
                res = a.insert(randomNum);
                if (res){
                    sum = sum + randomNum;
                }
                
            }
            else{
                res =  a.remove(randomNum);
                if (res){
                    //System.out.println("remove succeeded");
                    sum = sum - randomNum;
                }
                
            }

            if (sum != a.getKeysum()){
                System.out.println("this is the time");
                timeOfFailure = i;
                System.out.println(i);
                System.out.println(res);
                System.out.println(randomNum2);
                System.out.println(randomNum);
                break;
            }
        }
        sum = 0;
        a = new BST();

        for (int i = 0; i < numberOfRuns; i ++){
            if (i == timeOfFailure){
                System.out.println("hey");
            }
            boolean res;
            if (arrayOperations[i] < 1){
                res = a.insert(array[i]);
                if (res){
                    sum = sum + array[i];
                }
                
            }
            else{
                res =  a.remove(array[i]);
                if (res){
                    //System.out.println("remove succeeded");
                    sum = sum - array[i];
                }
                
            }

            if (sum != a.getKeysum()){
                System.out.println("this is the time");
                System.out.println(i);
                System.out.println(res);
                System.out.println(arrayOperations[i]);
                System.out.println(array[i]);
                break;
            }
        }



    }
}
