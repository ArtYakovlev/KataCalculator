package RX;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Calculator {
    private static final Map<String, String> romanToArabic = Map.of("I","1", "II","2", "III","3", "IV","4", "V","5",
                                                                    "VI","6", "VII","7", "VIII","8", "IX","9", "X","10");
    private static final Map<Integer, String> upToTen = Map.of(1,"I",2,"II",3,"III",4,"IV",5,"V",
                                                               6,"VI",7, "VII",8,"VIII",9,"IX",0,"");
    private static final Map<Integer, String> upToHundred = Map.of(1,"X", 2,"XX", 3,"XXX", 4,"XL", 5,"L",
                                                                   6,"LX", 7,"LXX", 8,"LXXX", 9,"XC");
    private static final Function<String, BinaryOperator<Integer>> binaryOperatorSupplier = op -> op.equals("+") ? (op1, op2) -> op1 + op2 :
                                                                                                  op.equals("-") ? (op1, op2) -> op1  - op2 :
                                                                                                  op.equals("*") ? (op1, op2) -> op1 * op2 :
                                                                                                  op.equals("/") ? (op1, op2) -> op1  / op2 : null;
    private static boolean isArabic(String[] operands) {
        try{
            int operand1 = Integer.parseInt(operands[0]);
            int operand2 = Integer.parseInt(operands[1]);
            if((operand1 < 0 || operand1 > 10) || (operand2 < 0 || operand2 >10)) throw new RuntimeException("Out of range");
            return true;
        }catch(NumberFormatException numberFormatException){
            if(!romanToArabic.containsKey(operands[0]) || !romanToArabic.containsKey(operands[1])) throw new RuntimeException("Wrong operands");
            return false;
        }
    }
    public static void calc(String expr) {
        AtomicInteger atomicInteger = new AtomicInteger();
        String action = expr.chars()
                            .mapToObj(ch ->String.valueOf((char)ch))
                            .filter("+-*/"::contains)
                            .peek(x -> atomicInteger.incrementAndGet())
                            .collect(Collectors.joining());
        if (atomicInteger.get() != 1) throw new RuntimeException("Wrong count of operators. Allowed operators are +, -, *, /");
        String[] operands = expr.split("[-+*/]");
        for(int i = 0; i < operands.length; i++) operands[i] = operands[i].trim();
        if (operands.length != 2) throw new RuntimeException("Wrong count of operands. Only 2 operands allowed. Count: " + operands.length);
        boolean isArabic = isArabic(operands);
        String[] ops = new String[2];
        if(!isArabic) {ops[0] = romanToArabic.get(operands[0]); ops[1] = romanToArabic.get(operands[1]);}
        else ops = operands;
        int result = Arrays.stream(ops).map(Integer::valueOf).reduce(binaryOperatorSupplier.apply(action)).get();
        System.out.println(isArabic ? result : arabicToRoman(result));
    }
    private static String arabicToRoman(int arg) {
        if(arg == 0) throw new RuntimeException("Romans doesn`t have zero");
        return arg == 100 ? "C" : arg >= 10 ? upToHundred.get(arg/10) + upToTen.get(arg%10) : upToTen.get(arg);
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) calc(scanner.nextLine());
    }
}