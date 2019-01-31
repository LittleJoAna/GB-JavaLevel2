package lesson_2;

/*
1. Есть строка вида: "1 3 1 2\n2 3 2 2\n5 6 7 1\n3 3 1 0"; (другими словами матрица 4x4)
 1 3 1 2
 2 3 2 2
 5 6 7 1
 3 3 1 0
 Написать метод, на вход которого подаётся такая строка, метод должен преобразовать строку в двумерный массив типа String[][];
 2. Преобразовать все элементы массива в числа типа int, просуммировать, поделить полученную сумму на 2, и вернуть результат;
 3. Ваш метод должен бросить исключения в случаях:
    Если размер матрицы, полученной из строки, не равен 4x4;
    Если в одной из ячеек полученной матрицы не число; (например символ или слово)
 4. В методе main необходимо вызвать полученный метод, обработать возможные исключения и вывести результат расчета.
 */

import java.util.Arrays;

public class HomeWorkException {
    public static void main(String[] args) {
        String str = "1 3 1 2\n2 3 2 2\n5 6 7 1\n3 3 1 0";
        String[][] arrStr = new String[4][4];
        try {
            convertStringToArrays(str, arrStr);
            System.out.println(performActions(arrStr));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Вы ввели неверный размер массива: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("В массиве используется нецифровой элемент: " + e.getMessage());
        }

    }

    //1. Написать метод, на вход которого подаётся такая строка, метод должен преобразовать строку
    // в двумерный массив типа String[][];
    static void convertStringToArrays(String str, String[][] arrStr) throws ArrayIndexOutOfBoundsException {
        String[] temp = str.split("\n");
        String[] temp2;

        for (int i = 0; i < arrStr.length; i++) {
            for (int j = 0; j < arrStr[i].length; j++) {
                temp2 = temp[i].split(" ");
                arrStr[i][j] = temp2[j];
            }
        }
    }

    //2. Преобразовать все элементы массива в числа типа int, просуммировать, поделить полученную сумму
    // на 2, и вернуть результат;
    static int performActions (String[][] arrStr) throws NumberFormatException, ArrayIndexOutOfBoundsException {
        int result = 0;
        int[][] arrInt= new int[4][4];
        for (int i = 0; i < arrStr.length; i++) {
            for (int j = 0; j < arrStr[i].length; j++) {
                arrInt[i][j] = Integer.parseInt(arrStr[i][j]);
                result += arrInt[i][j];
            }
        }
        return result / 2;
    }
}
