package com.minimessage.test.algorithm;

/**
 * 二分查找实现
 */
public class BinarySearch {
    public static void main(String[] args) {

    }

    /**
     * a为有序数组
     * i为区间左值，j为区间右值
     * 左闭右闭
     *
     * @param a
     * @param target
     * @return
     */
    public static int binarySearchBasic(int[] a, int target) {
        int i = 0, j = a.length - 1;
        while (i <= j) {
            int m = (i + j) >>> 2;
            if (target < a[m]) {
                j = m - 1;
            } else if (target > a[m]) {
                i = m + 1;
            } else {
                return m;
            }
        }
        return -1;
    }

    /**
     * 改进版本
     * 左闭右开区间
     *
     * @return
     */
    public static int binarySearchNewVersion(int[] a, int target) {
        int i = 0, j = a.length;
        while (i < j) {
            int m = (i + j) >>> 1;
            if (target < a[m]) {
                j = m;
            } else if (a[m] < target) {
                i = m + 1;
            } else {
                return m;
            }
        }
        return -1;
    }

    /**
     * 数组a中有查找的重复元素，找重复值最左边的
     *
     * @param a
     * @param target
     * @return
     */
    public static int binarySearchLeftmost1(int[] a, int target) {
        int i = 0, j = a.length - 1;
        int candidate = -1;
        while (i <= j) {
            int m = (i + j) >>> 1;
            if (target < a[m]) {
                j = m - 1;
            } else if (a[m] < target) {
                i = m + 1;
            } else {
                //记录候选者
                candidate = m;
                j = m - 1;
            }
        }
        return candidate;
    }

    public static int binarySearchRightmost1(int[] a, int target){
        int i = 0,j = a.length - 1;
        int candidate = -1;
        while(i <= j){
            int m = (i+j) >>> 1;
            if (target < a[m]){
                j = m - 1;
            }else if( a[m] < target){
                i = m + 1;
            }else {
                candidate = m;
                i = m + 1;
            }
        }
        return candidate;
    }

}
