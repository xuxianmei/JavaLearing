using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Singleton_Pattern
{
    class Program
    {
        static void Main(string[] args)
        {
        }
    }
    public sealed class SingleTon
    {
        private static SingleTon uniqueInstance=null;
        private static readonly Object obj=new Object();
        
        private SingleTon()
        {

        }
        public  static SingleTon GetInstance()
        {
            if (uniqueInstance == null)
            {
                Monitor.Enter(obj);

                if (uniqueInstance == null)
                {
                    var temp = new SingleTon();
                    Volatile.Write(ref uniqueInstance, temp);
                }

                Monitor.Exit(obj);

            }
            return uniqueInstance;
        }
    }

    public abstract class Car
    {
        public abstract void Go();
    }
    public  class BMWCar:Car
    {
        public override void Go()
        {
            Console.WriteLine("BMW GO...");
        }

    }
    public class BenZiCar : Car
    {
        public override void Go()
        {
            Console.WriteLine("BenZi Go...");
        }
    }
    public abstract class Factory
    {
        public abstract Car CreateCar();
    }
    public class BenZiFactory : Factory
    {
        public override Car CreateCar()
        {
            return new BenZiCar();
        }
    }
    public class BMWCarFactory : Factory
    {
        public override Car CreateCar()
        {
            return new BMWCar();
        }
    }
}
