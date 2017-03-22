package com.tlxxm.learing;

public class Main {

    public static void main(String[] args) {
	// write your code here
    }
}
interface Monster{
    void menace();
}
interface DangerousMonster extends Monster{
    void destroy();

}
class DragonZilla implements DangerousMonster{
    public void menace(){

    }
    public void destroy(){

    }
}
interface Lethal{
    void kill();
}

interface Vampire extends DangerousMonster,Lethal{
    void drinkBlood();
}
class VeryBadVampire implements Vampire{
    public void menace(){}
    public void destroy(){}
    public void kill(){}
    public void drinkBlood(){}
}
