package com.minimessage.test.algorithm.recursion;

/**
 * 插入排序
 */
public class E05InsertionSort {

    public static void sort(int[] a) {
        insertion(a, 1);
    }

    private static void insertion(int[] a, int low) {
        if (low == a.length) {
            return;
        }
        int t = a[low];
        int i = low - 1;
        while (i >= 0 && a[i] > t) {
            a[i + 1] = a[i];
            i--;
        }
        a[i + 1] = t;
        insertion(a, low + 1);
    }
}
