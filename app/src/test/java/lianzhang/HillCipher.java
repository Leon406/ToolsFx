package lianzhang;

import java.util.Scanner;

public class HillCipher {
    public static void main(String[] args) {
        System.out.println(encrypt());
        System.out.println(decrypt());
        //		if(args[0].charAt(0) == 'e')
        //			System.out.println(encrypt());
        //		else if(args[0].charAt(0) == 'd')
        //			System.out.println(decrypt());
        //		else
        //			System.exit(1);
    }

    private static String decrypt() {
        int[][] keyMatrix = getKey();
        int[] cipherTextArray = getPlainText(keyMatrix.length, 0);
        int[][] adjMatrix = adjoint(keyMatrix); // without det
        int det = determinant(keyMatrix);
        int det_denominator = mod(det);
        int detModInverse = 0;
        for (int i = 1; (i < 26) && detModInverse == 0; i++) {
            if ((i * det_denominator) % 26 == 1) detModInverse = i;
        }
        if (detModInverse == 0) {
            System.out.println(
                    "The determinant is not co-prime with 26, hence this key is not valid. Terminating...");
            System.exit(1);
        }
        int[][] keyInverseMatrix = new int[keyMatrix.length][keyMatrix[0].length];
        for (int i = 0; i < keyInverseMatrix.length; i++) {
            for (int j = 0; j < keyInverseMatrix[0].length; j++) {
                keyInverseMatrix[i][j] = detModInverse * (adjMatrix[i][j]);
                keyInverseMatrix[i][j] = mod(keyInverseMatrix[i][j]);
            }
        }

        // The actual decryption
        int[][] answer = new int[cipherTextArray.length / keyMatrix.length][];
        int index = 0;
        for (int i = 0; i < answer.length; i++) {
            int[] tempArray = new int[keyMatrix.length];
            for (int j = 0; j < tempArray.length; j++) {
                tempArray[j] = cipherTextArray[index++];
            }
            answer[i] = multiply(tempArray, keyInverseMatrix);
        }
        answer = modReduceMatrix(answer);
        String s = matrixToString(answer);
        return s;
    }

    private static int[][] adjoint(int[][] keyMatrix) {
        int[][] answer = new int[keyMatrix.length][keyMatrix[0].length];
        if (keyMatrix.length == 2) {
            answer[0][0] = keyMatrix[1][1];
            answer[0][1] = -1 * keyMatrix[0][1];
            answer[1][0] = -1 * keyMatrix[1][0];
            answer[1][1] = keyMatrix[0][0];
        } else {
            answer[0][0] =
                    (keyMatrix[1][1] * keyMatrix[2][2]) - (keyMatrix[1][2] * keyMatrix[2][1]);
            answer[0][1] =
                    -1
                            * ((keyMatrix[1][0] * keyMatrix[2][2])
                                    - (keyMatrix[1][2] * keyMatrix[2][0]));
            answer[0][2] =
                    (keyMatrix[1][0] * keyMatrix[2][1]) - (keyMatrix[1][1] * keyMatrix[2][0]);

            answer[1][0] =
                    -1
                            * ((keyMatrix[0][1] * keyMatrix[2][2])
                                    - (keyMatrix[0][2] * keyMatrix[2][1]));
            answer[1][1] =
                    (keyMatrix[0][0] * keyMatrix[2][2]) - (keyMatrix[0][2] * keyMatrix[2][0]);
            answer[1][2] =
                    -1
                            * ((keyMatrix[0][0] * keyMatrix[2][1])
                                    - (keyMatrix[0][1] * keyMatrix[2][0]));

            answer[2][0] =
                    (keyMatrix[0][1] * keyMatrix[1][2]) - (keyMatrix[0][2] * keyMatrix[1][1]);
            answer[2][1] =
                    -1
                            * ((keyMatrix[0][0] * keyMatrix[1][2])
                                    - (keyMatrix[0][2] * keyMatrix[1][0]));
            answer[2][2] =
                    (keyMatrix[0][0] * keyMatrix[1][1]) - (keyMatrix[0][1] * keyMatrix[1][0]);

            int[][] temp = new int[answer.length][answer[0].length];

            // copy answer to temp. Perform deep copy. Direct copy gives us a reference.
            for (int i = 0; i < answer.length; i++) {
                for (int j = 0; j < answer[0].length; j++) {
                    temp[i][j] = answer[i][j];
                }
            }

            for (int i = 0; i < answer.length; i++) {
                for (int j = 0; j < answer[0].length; j++) {
                    answer[i][j] = temp[j][i];
                }
            }
        }
        return answer;
    }

    private static String encrypt() {
        int[][] keyMatrix = getKey();
        int[] plainTextArray = getPlainText(keyMatrix.length, 1);
        int[][] answer = new int[plainTextArray.length / keyMatrix.length][];
        int index = 0;
        for (int i = 0; i < answer.length; i++) {
            int[] tempArray = new int[keyMatrix.length];
            for (int j = 0; j < tempArray.length; j++) {
                tempArray[j] = plainTextArray[index++];
            }
            answer[i] = multiply(tempArray, keyMatrix);
        }
        answer = modReduceMatrix(answer);
        String s = matrixToString(answer);
        return s;
    }

    private static String matrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) sb.append((char) (matrix[i][j] + 97));
        }
        return sb.toString();
    }

    private static int[][] modReduceMatrix(int[][] matrix) {
        int[][] answer = matrix;
        for (int i = 0; i < answer.length; i++) {
            for (int j = 0; j < answer[0].length; j++) {
                answer[i][j] = matrix[i][j] % 26;
            }
        }
        return answer;
    }

    private static int[] multiply(int[] plainTextArray, int[][] keyMatrix) {
        int[] answer = new int[plainTextArray.length];
        for (int i = 0; i < plainTextArray.length; i++) {
            for (int j = 0; j < keyMatrix.length; j++) {
                answer[i] += (plainTextArray[j] * keyMatrix[j][i]);
            }
        }
        return answer;
    }

    private static int[] getPlainText(int keyOrder, int flag) {
        Scanner op = new Scanner(System.in);
        System.out.println((flag == 1) ? "Enter Plain Text" : "Enter Cipher Text");
        String plainText = op.nextLine();
        while (plainText.length() % keyOrder != 0) {
            plainText = plainText + 'Z';
        }
        int[] plainTextArray = new int[plainText.length()];
        for (int i = 0; i < plainText.length(); i++) {
            plainTextArray[i] = Character.toUpperCase(plainText.charAt(i)) - 65;
        }
        op.close();
        return plainTextArray;
    }

    private static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void errorExit(String s) {
        System.out.println(s);
        System.exit(1);
    }

    private static int[][] getKey() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a valid key: ");
        String key = sc.nextLine();
        if (!isKeyValid(key)) {
            errorExit("Please enter a valid key. It won't form a square matrix. Terminating....");
        }
        int[][] matrix = generateKeyMatrix(key);
        if (!hasInverse(matrix)) {
            errorExit("Please make sure key matrix is non-singular. Terminating...");
        }
        int det = determinant(matrix);
        if (!isDetCoPrime(det)) {
            errorExit(
                    "Please make sure key matrix's determinant is co-prime with 26. Terminating...");
        }
        return matrix;
    }

    private static boolean isDetCoPrime(int det) {
        return (gcd(det, 26) == 1) ? true : false;
    }

    private static int gcd(int a, int b) {
        return (b != 0) ? gcd(b, a % b) : a;
    }

    private static boolean hasInverse(int[][] matrix) {
        return (determinant(matrix) != 0) ? true : false;
    }

    private static int determinant(int[][] matrix) {
        int det = 0;
        if (matrix.length == 1) det = matrix[0][0];
        else if (matrix.length == 2)
            det = (matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1]);
        else {
            det =
                    matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1])
                            - matrix[0][1]
                                    * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0])
                            + matrix[0][2]
                                    * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);
            // since matrix can be at most 3x3
        }
        return det;
    }

    private static int[][] generateKeyMatrix(String key) {
        int index = 0, rowLength = (int) Math.sqrt(key.length());
        int[][] matrix = new int[rowLength][rowLength];
        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < rowLength; j++)
                matrix[i][j] = (int) Character.toUpperCase(key.charAt(index++)) - 65;
        }
        return matrix;
    }

    private static boolean isKeyValid(String s) {
        double l = Math.sqrt(s.length());
        return (l != (long) l) ? false : true;
    }

    private static int mod(int det) {
        return ((det % 26) >= 0) ? (det % 26) : (det % 26) + 26;
    }
}
