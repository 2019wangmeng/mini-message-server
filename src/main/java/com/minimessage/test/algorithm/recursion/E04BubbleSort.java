package com.minimessage.test.algorithm.recursion;

import java.util.Arrays;

/**
 * 递归实现冒泡排序
 */
public class E04BubbleSort {
    public static void sort(int[] a) {
        bubble(a, a.length - 1);
    }

    /**
     * @param a
     * @param j 未排序区域的右边界
     */
    private static void bubble(int[] a, int j) {
        if (j == 0) {
            return;
        }
        int x = 0;
        for (int i = 0; i < j; i++) {
            if (a[i] > a[i + 1]) {
                int t = a[i];
                a[i] = a[i + 1];
                a[i + 1] = t;
                x = i;
            }
        }
        bubble(a, x);
    }

    public static void main(String[] args) {
        int[] a = {6, 3, 5, 1, 2};
        System.out.println(Arrays.toString(a));
        sort(a);
        System.out.println(Arrays.toString(a));
    }
}
