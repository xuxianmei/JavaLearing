using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace SyncBlockAndLock
{
    class Program
    {
        static readonly Object obj = new Object();
        static void Main(string[] args)
        {
            try
            {
                Foo f = new Foo();
                Console.WriteLine(f.GetHashCode());
                Console.ReadLine();
            }
            finally
            {
                Monitor.Exit(obj);
            }
        }
    }
    public class Foo
    {

    }
}
