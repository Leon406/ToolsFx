package lianzhang;

/** https://blog.csdn.net/abc1235454/article/details/89384296 */
public class Matrix {
    /** 1 求解代数余子式 输入：原始矩阵+行+列 现实中真正的行和列数目 */
    public static float[][] getDY(float[][] data, int h, int v) {
        int H = data.length;
        int V = data[0].length;
        float[][] newData = new float[H - 1][V - 1];

        for (int i = 0; i < newData.length; i++) {

            if (i < h - 1) {
                for (int j = 0; j < newData[i].length; j++) {
                    if (j < v - 1) {
                        newData[i][j] = data[i][j];
                    } else {
                        newData[i][j] = data[i][j + 1];
                    }
                }
            } else {
                for (int j = 0; j < newData[i].length; j++) {
                    if (j < v - 1) {
                        newData[i][j] = data[i + 1][j];
                    } else {
                        newData[i][j] = data[i + 1][j + 1];
                    }
                }
            }
        }
        // System.out.println("---------------------代数余子式测试.---------------------------------");
        // for(int i=0;i<newData.length;i++){
        // for(int j=0;j<newData[i].length;j++){
        // System.out.print("newData["+i+"]"+"["+j+"]="+newData[i][j]+"   ");
        // }
        //
        // System.out.println();
        // }

        return newData;
    }

    public static float getHL(float[][] data) {

        // 终止条件
        if (data.length == 2) {
            return data[0][0] * data[1][1] - data[0][1] * data[1][0];
        }

        float total = 0;
        // 根据data 得到行列式的行数和列数
        int num = data.length;
        // 创建一个大小为num 的数组存放对应的展开行中元素求的的值
        float[] nums = new float[num];

        for (int i = 0; i < num; i++) {
            if (i % 2 == 0) {
                nums[i] = data[0][i] * getHL(getDY(data, 1, i + 1));
            } else {
                nums[i] = -data[0][i] * getHL(getDY(data, 1, i + 1));
            }
        }
        for (int i = 0; i < num; i++) {
            total += nums[i];
        }
        System.out.println("total=" + total);
        return total;
    }

    public static float[][] getN(float[][] data) {
        // 先是求出行列式的模|data|
        float A = getHL(data);
        // 创建一个等容量的逆矩阵
        float[][] newData = new float[data.length][data.length];

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                float num;
                if ((i + j) % 2 == 0) {
                    num = getHL(getDY(data, i + 1, j + 1));
                } else {
                    num = -getHL(getDY(data, i + 1, j + 1));
                }

                newData[i][j] = num / A;
            }
        }

        // 转置 代数余子式转制
        newData = getA_T(newData);
        // 打印
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                System.out.print("newData[" + i + "][" + j + "]= " + newData[i][j] + "   ");
            }

            System.out.println();
        }

        return newData;
    }
    /**
     * 取得转置矩阵
     *
     * @param A
     * @return
     */
    public static float[][] getA_T(float[][] A) {
        int h = A.length;
        int v = A[0].length;
        // 创建和A行和列相反的转置矩阵
        float[][] A_T = new float[v][h];
        // 根据A取得转置矩阵A_T
        for (int i = 0; i < v; i++) {
            for (int j = 0; j < h; j++) {
                A_T[j][i] = A[i][j];
            }
        }
        System.out.println("取得转置矩阵 wanbi…");
        return A_T;
    }
}
