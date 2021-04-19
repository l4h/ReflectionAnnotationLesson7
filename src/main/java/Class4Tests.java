import java.util.Arrays;

public class Class4Tests {
    private static int[] arr;

    //чтобы не создавать экземпляр класса сделаем методы статическими
    @ValSource
    @MyBeforeSuite
    public static void beforeSuite(int[] array){
        arr = array;
        System.out.println("Begin with "+Arrays.toString( arr));
    }


    @MyTest
    public static int returnZero(){
        return 0;
    }

    @MyTest(priority = 3)
    public static int returnOne(){
        return 1;
    }

    @MyTest(priority = 1)
    public static int sum(int ... a){
        int sum=0;
        for (int i = 0; i < a.length; i++) {
            sum=sum+a[i];
        }
        return sum;
    }

    public static void dontTestMePlease(){
    }

    @MyAfterSuite
    public static void afterSuite(){
        System.out.println("The end!");
    }

}
