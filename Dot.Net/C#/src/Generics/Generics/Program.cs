using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Generics
{
    class Program
    {
        static void Main(string[] args)
        {

            
            Func<Person, Student> f1 = (p) => new Student() { Age = 10, Name = "123" };
            Func<Student, Person> f2 = f1;




        }
    }
    interface IStack<out T> where T: class,new()
        
    {
        //void Push(T t);
        T Pop();
    }
    class MyStack<U> : IStack<U> where U:class,new()
    {
        //public void Push(U t)
        //{

        //}
        public U Pop()
        {
            return new U();
        }
    }

    public class Person {
        public Int32 Age { get; set; } 
        public String Name { get; set; }
    }
    public class Student:Person
    {
        public String SchoolName { get; set; }
        
    }


}
