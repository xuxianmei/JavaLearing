using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Security.Cryptography;
namespace ConsoleApp1
{
    class Program
    {
        static void Main(string[] args)
        {
          
            int[] cc = new int[] { 1,5,6,3,8,12,34,6,78,8,44,3,46,90,1,3,5,34,76,9,45};
            int[] bb = new int[] {1,13,15,20, 3,14,15,16,21 };
            Quick.sort(bb,0,bb.Length-1);
            foreach (var item in bb)
            {
                Console.Write(item + ",");
            }
            Console.ReadLine();
        }
        /// <summary>
        /// 冒泡排序
        /// </summary>
        /// <param name="a"></param>
        public static void BubbleSort(int[] a)
        {
            bool flag = true;
            for (int i = 1; i < a.Length && flag; i++)
            {
                flag = false;
                for (int j = 0; j < a.Length - i; j++)
                {
                    if (a[j] > a[j + 1])
                    {
                        int temp = a[j];
                        a[j] = a[j + 1];
                        a[j + 1] = temp;
                        flag = true;
                    }
                }
            }
        }

        /// <summary>
        /// 简单选择排序
        /// </summary>
        /// <param name="a"></param>
        public static void SelectSort(int[] a)
        {
           
            for(int i = 0; i < a.Length; i++)
            {
                int min = i;
                for (int j = i + 1; j < a.Length; j++)
                {
                    if (a[j] < a[min])
                        min = j;
                }
                if (min != i)
                {
                    int temp = a[i];
                    a[i] = a[min];
                    a[min] = temp;
                }
            }

        }

        /// <summary>
        /// 插入排序
        /// </summary>
        /// <param name="a"></param>
        public static void InsertSort(int[] a)
        {
            for(int i = 1; i < a.Length; i++)
            {
                for(int j = i; j>0; j--)
                {
                    if(a[j]<a[j-1])
                    {
                        int temp = a[j];
                        a[j] = a[j - 1];
                        a[j - 1] = temp;
                    }
                }    
            }
        }

        /// <summary>
        /// 希尔排序
        /// </summary>
        /// <param name="a"></param>
        public static void ShellSort(int[] a)
        {
            int h = 1;//增量为3
            while (h < (a.Length / 3)) h = 3 * h + 1;
            while (h > 0)
            {
                for (int i = h; i < a.Length; i++)
                {
                    for (int j = i; j >= h; j -= h)
                    {
                        if (a[j] < a[j - h])
                        {
                            int temp = a[j];
                            a[j] = a[j - h];
                            a[j - h] = temp;
                        }
                    }

                }
                h=h/3;
            }
            
        }

        /// <summary>
        /// 归并两个有序数组，其中,start middle代表一个有序数组，middle+1 finish代表一个有序数组
        /// </summary>
        /// <param name="a"></param>
        /// <param name="start"></param>
        /// <param name="middle"></param>
        /// <param name="finish"></param>
        public static void merge(int[] a,int start,int middle,int finish)
        {
            int[] aux = new Int32[a.Length];
            int i = start, j = middle + 1;
            for(int k = start; k <= finish; k++)
            {
                aux[k] = a[k];
            }
            for(int k = start; k <= finish; k++)
            {
                if (i > middle) a[k] = aux[j++];//左边用尽，直接使用右边的，用了一个以后，使用++表示
                else if (j > finish) a[k] = aux[i++];//右边用尽，直接使用左边的，用了一个以后，使用++表示
                //两边都还有元素，取其中较小的
                else if (aux[i] > aux[j]) a[k] = aux[j++];
                else a[k] = aux[i++];
            }
        }

        //归并排序，非递归
        public static void MergeSort(int[] a)
        {
            int N = a.Length;//数组长度
            int[] aux = new Int32[a.Length];//辅助数组


            //分治策略，
            //第一次合并，因为数组无序，所以每个元素自成一个有序的数组，
            //第一次合并完成以后，会成多个包含2个元素的有序数组，再合并成为多个包含4个元素的有序数组
            //size代表有序数组"集合"中每个数组中的元素个数。
            //每一次合并以后，都会有多个有序数组，直到size>N时，代表所有的数组都合并成了一个有序数组
            for (int size = 1; size < N; size += size)
            {
                //根据指定的size，对元素进行分组，分成若干个数组，每两组进行合并成有序数组
                //start代表合并每次都头开始,但有序数组容量在变多），数量在减少
                for (int start = 0; start < N - size; start += (2 * size))
                {

                    //N-size，防止合并最后两个数组时越界
                    //+= 2 * size，每处理好一对数组，再处理下一对数组，就直接跳过这两个已合并好的数组包含的元素

                    //start:两个数组中前一个数组的开始下标
                    //middle:两个数组中的前一个数组的最后下标
                    //finish:两个数组中后一个数组的最后下标
                    int middle = start + size - 1;
                    int finish = Math.Min(start + 2 * size - 1, N - 1);
                    //Math.Min(start + 2 * size - 1, N - 1) 防止合并最后丙个数组时，后面一个数组越界
                    merge(a, start, middle, finish);        
                }
            }
        }



    }
    //归并排序(递归方式)
    public class Merge
    {
        private static int[] aux;
        private static void merge(int[] a, int start, int middle, int finish)
        {            
            int i = start, j = middle + 1;
            if (a[middle] <= a[j]) return;
            for (int k = start; k <= finish; k++)
            {
                aux[k] = a[k];
            }
            for (int k = start; k <= finish; k++)
            {
                if (i > middle) a[k] = aux[j++];//左边用尽，直接使用右边的，用了一个以后，使用++表示
                else if (j > finish) a[k] = aux[i++];//右边用尽，直接使用左边的，用了一个以后，使用++表示
                //两边都还有元素，取其中较小的
                else if (aux[i] > aux[j]) a[k] = aux[j++];
                else a[k] = aux[i++];
            }
        }

        public static void sort(int[] a)
        {
            aux = new Int32[a.Length];
            sort(a, 0, a.Length - 1);
        }

        //自顶向下
        private static void sort(int[] a,int start,int finish)
        {
            if (finish <= start) return;
            int mid = start + (finish-start) / 2;
            sort(a, start, mid);//递归将左半边排序
            sort(a, mid + 1, finish);//递归将右半边排序
            merge(a, start,mid, finish);//归并结果
        }
    }

    public class Quick
    {
        /// <summary>
        /// 排序切分
        /// </summary>
        /// <param name="a"></param>
        /// <param name="start"></param>
        /// <param name="finish"></param>
        /// <returns></returns>
        private static int parition(int[] a,int start,int finish)
        {
            int i = start, j = finish + 1;
            int v = a[start];
            int temp = 0;
            while (true)
            {
                

                while (a[++i] < v) {
                    if (i == finish)
                        break;
                }
                while (v < a[--j])
                {
                    if (j == start)
                        break;
                }
                if (i > j) break;
                temp = a[i];
                a[i] = a[j];
                a[j] = temp;
            }

            temp = a[start];
            a[start] = a[j];
            a[j] = temp;
            return j;

            return 0;

        }

        public static void sort(int[] a,int start,int finish)
        {
            if (finish <= start) return;
            int j = parition(a, start, finish);
            sort(a, 0, j - 1);
            sort(a, j + 1, finish);
        }
    }
}
