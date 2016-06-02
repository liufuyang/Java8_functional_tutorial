import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        example1();
        example2();
        example3();
        example4();

    }

    private static void example1() {
        System.out.println(isPrime(1));
        System.out.println(isPrime(2));
        System.out.println(isPrime(3));
        System.out.println(isPrime(4));
    }

    /********************* example1 ********************************/

    //    private static boolean idDivible() {
    //
    //    }
    private static boolean isPrime(final int n) {
        //        for (int i = 2; i < n; i++) {
        //            if (n % i == 0) return false;
        //        }
        //        return n > 1;

        //Declarative
        //Focus on what but not how
        //Immutability
        //        Predicate<Integer> isDivisible = divisor -> n % divisor ==0;
        IntPredicate isDivisible = divisor -> n % divisor == 0;
        return n > 1 &&
                IntStream.range(2, n)
                        .noneMatch(isDivisible);
    }

    //    private static boolean isDivisible(int i) {
    //        return n % i == 0; // Problem: n cannot be accessed"
    //    }

    /*****************************************************/

    /************************ example2 *****************************/
    private static void example2() {
        // find the double of the first even number greater than 3
        List<Integer> values = Arrays.asList(1, 2, 3, 5, 4, 6, 7, 8, 9, 10);
        int result = 0;
        for (int e : values) {
            if (e > 3 && e % 2 == 0) {
                result = e * 2;
                break;
            }
        }
        System.out.println(result); // problem above is that, there might not be less than 3 values

        Predicate<Integer> isEven = e -> e % 2 == 0;

        Predicate<Integer> isGreatherThan3Predicate = e -> e > 3;

        Function<Integer, Predicate<Integer>> isGreaterThan = pivot ->
                number -> number > pivot;

        System.out.println(
                values.stream()
                        .filter(Main::isGreaterThan3) // Passing [boolean function], with "method references"
                        .filter(isGreaterThan.apply(3)) // or something like this, passing [Function]
                        .filter(isGreatherThan3Predicate) // or something like this - [Predicate]
                        .filter(isEven)                // Passing Predicate
                        .map(Main::doubltIt)            // intermediate operations
                        .findFirst() // give optional 8, if list less than 3 element, return optional.empty
                // terminal operations. If this terminal operation is not called, the previous
                // intermediate operations are all NOT called/executed.
        );

        // Two main benefit of functional programming:
        // Lazy & Composition
    }

    private static int doubltIt(int number) {
        System.out.println("doubleIt " + number);
        return number * 2;
    }

    private static boolean isGreaterThan3(int number) {
        System.out.println("isGreaterThan3 " + number);
        return number > 3;
    }
    /*****************************************************/

    /************************ example3 *****************************/
    private static void example3() {
        List<Integer> values = Arrays.asList(1, 2, 3, 5, 4, 6, 7, 8, 9, 10);
        int total = totalValuesTrad(values, e -> true); // this will make the Selector interface always return true...
        total = totalValuesTrad(values, new EvenSelector());
        System.out.println("example3 - old way even total: " + total);

        total = totalValues(values, e -> e % 2 == 0);
        System.out.println("example3 - new way even total: " + total);

        total = totalValuesNew(values, e -> e % 2 == 0);
        System.out.println("example3 - new new way even total: " + total);

        // Beauty here is :
        // mixing object composition along with function composition
    }

    // traditional way:
    public static int totalValuesTrad(List<Integer> numbers, Selector selector) {
        // what if we want to total only the selected values
        int result = 0;
        for (int e : numbers) {
            if (selector.pick(e))
                result += e;
        }
        return result;
    }

    // new way:
    public static int totalValues(List<Integer> numbers, Predicate<Integer> selector) {
        // what if we want to total only the selected values
        int result = 0;
        for (int e : numbers) {
            if (selector.test(e))
                result += e;
        }
        return result;
    }

    // new new way:
    public static int totalValuesNew(List<Integer> numbers, Predicate<Integer> selector) {
        // what if we want to total only the selected values
        return numbers.stream()
                .filter(selector)
                .reduce(0, Math::addExact);
    }

    /*****************************************************/

    /************************ example4 *****************************/
    private static void example4() { // stream beauty - ability to parallel
        List<Integer> numbers =
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10); // let's say we want to double them and then total all

        System.out.println("example 4: " +
                numbers.parallelStream() // try stream(), it will be much more slower
                        .mapToInt(Main::doubltItSlowly) // limitation - this doublItSlowly throws an Exception :S
                        .sum()
        );

        //numbers.forEach(x -> System.out.println(x));

        // Some other topic:

//        curry(int value) {
//            return Function<T, R> ... value ...;
//        }
    }

    private static int doubltItSlowly(int number) {
        System.out.println("doubleItSlowly start. " + number);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("doubleItSlowly end. " + number);
        return number * 2; // this is a pure function, a function has no side effects,
        // it takes an input and gives an output.
        // one benefit of pure function is that it is easy to test.
        // second benefit of pure function is that it can do referential transparency:
        // referential transparency:
        // the compiler can replace the method where the result of the method and the input of the method is the same.
    }
}

// old way:
interface Selector {
    boolean pick(int value);
}

class EvenSelector implements Selector {
    @Override
    public boolean pick(final int value) {
        return value % 2 == 0;
    }
}
