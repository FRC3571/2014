/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author TomasR
 */
public class XboxController {
    private final Joystick joy;
    public double leftStickX(){
        return joy.getAxis(Joystick.AxisType.kX);
    }
    public double leftStickY(){
        return joy.getAxis(Joystick.AxisType.kY);
    }
    public double trigger(){
        return joy.getRawAxis(3);
    }
    public double rightStickX(){
        return joy.getRawAxis(4);
    }
    public double rightStickY(){
        return joy.getRawAxis(5);
    }
    public boolean A(){
        return joy.getRawButton(1);
    }
    public boolean B(){
        return joy.getRawButton(2);
    }
    public boolean X(){
        return joy.getRawButton(3);
    }
    public boolean Y(){
        return joy.getRawButton(4);
    }
    public boolean LB(){
        return joy.getRawButton(5);
    }
    public boolean RB(){
        return joy.getRawButton(6);
    }
    public boolean back(){
        return joy.getRawButton(7);
    }
    public boolean start(){
        return joy.getRawButton(8);
    }
    public boolean LS(){
        return joy.getRawButton(9);
    }
    public boolean RS(){
        return joy.getRawButton(10);
    }

    public XboxController(int i) {
        joy=new Joystick(i);
    }
    public XboxController() {
        joy=new Joystick(1);
    }
}
