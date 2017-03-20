package com.tlxxm.learing;

public class SpaceShipDelegation {

    private String name;
    private SpaceShipControls controls=new SpaceShipControls();
    public SpaceShipDelegation(String name){
        this.name=name;
    }
    public void up(int velocity){
        controls.up(velocity);
    }
    public void down(int velocity){
        controls.down(velocity);
    }
    public void right(int velocity){
        controls.right(velocity);
    }
    public void left(int velocity){
        controls.left(velocity);
    }
    public void forward(int velocity){
        controls.forward(velocity);
    }
    public void back(int velocity){
        controls.back(velocity);
    }
    public void turboBoost(){
        controls.turboBoost();
    }
    public static void main(String[] args) {
	// write your code here
    }
}
class SpaceShipControls{
    void up(int velocity){}
    void down(int velocity){}
    void right(int velocity){}
    void left(int velocity){}
    void forward(int velocity){}
    void back(int velocity){}
    void turboBoost(){}
}