package com.tlxxm.learing;

public class FinalOverriding {

    public static void main(String[] args) {
	    OverridingPrivate op=new OverridingPrivate();
	    op.f();
	    op.g();
	    WithFinals wf=op;
	    //不可访问
//        wf.f();
//        wf.g();
    }
}
class WithFinals{
    private final void f(){
        System.out.println("WithFinals.f()");
    }
    private void g(){
        System.out.println("WithFinals.g()");
    }
}
class OverridingPrivate extends WithFinals{
    public final void f(){
        System.out.println("OverridingPrivate.f()");
    }
    public void g(){
        System.out.println("OverridingPrivate.g()");
    }
}