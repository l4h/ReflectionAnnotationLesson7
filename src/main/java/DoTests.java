import javafx.scene.layout.Priority;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class DoTests {
    static Method[] methods;
    static Map<Integer, ArrayList<Method>> method4Test;
    static SortedSet<Integer> priorities;
    static ArrayList<Integer> params;

    public static void main(String[] args) {
        start(Class4Tests.class);
    }

    static class PriorityPointer {
        public int k=0;
    }

    public static void start(Class cl) {
        methods = cl.getDeclaredMethods(); //получим массив объявленных в этом классе методов
        method4Test = new HashMap<>();
        priorities = new TreeSet<>();

        /** Устанавливаем k=0 чтобы компилятор не ругался на то, что эта переменная может быть не инициализирована
         *  при выходе из метода ParseAnnotations. Она в любом случае будет инициализированна , т.к. аннотация @ValSource
         *  не применяется без @MyBeforeSuite(Хотя в коде это возможно:нет //todo проверки на наличие @MyBeforeSuite)
          */



        PriorityPointer k = new PriorityPointer();
        for (Method m : methods) {
            Annotation[] an = m.getAnnotations();
            if (an.length > 0) {
                for (Annotation el : an) {
                    ParseAnnotations(k, el);
                }
                if (method4Test.get(k.k) == null) method4Test.put(k.k, new ArrayList<>());
                method4Test.get(k.k).add(m);
            }
        }

        for (int i : priorities) {
            ArrayList<Method> methodsByPriority = method4Test.get(i);
            for (Method method : methodsByPriority) {
                Parameter[] methParams = method.getParameters();
                if (methParams.length == 0) {
                    try {
                        System.out.println("Method "+method.getName()           
                                + " invoke result "+method.invoke(null));
                    } catch (Exception e) { //так делать не хорошо, но обрабатывать исключения мы не собираемся, поэтому можно)
                        e.printStackTrace();
                    }
                } else {
                    try {
                        int[] intArr =new int[params.size()];
                        for (int j=0; j< intArr.length;j++) {
                            intArr[j]=params.get(j);
                        }                                                       //универсальность наших тестов запросила слишком много времени
                        System.out.println("Method "+method.getName()           //на реализацию и мы от нее отказались. Поэтому везде int
                                + " invoke result "+method.invoke(null, intArr));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


        }
    }

    private static void ParseAnnotations(PriorityPointer k, Annotation el) {
        if (el.annotationType() == MyBeforeSuite.class) {
            k.k = ((MyBeforeSuite)el).priority();
            if (priorities.contains(k.k)) {
                throw new RuntimeException("Annotation @МуBeforeSuite can't be declared more 1 times");
            } else {
                priorities.add(k.k);
                return;
            }
        }
        if (el.annotationType() == ValSource.class) { //подразумевается, что ValSource может встретиться только совместно
            String par = ((ValSource) el).vals(); //с аннотацией MyBeforeSuite со всеми вытекающими
            if (params != null)
                throw new RuntimeException("@ValSource может использоваться только совместно с @MyBeforeSuite");
            params = new ArrayList<>();
            for (String i : par.split(",")) {
                params.add(Integer.valueOf(i));
            }
            return;
        }
        if (el.annotationType() == MyTest.class) {
            k.k = ((MyTest) el).priority();
            priorities.add(k.k);
        }
    }
}
