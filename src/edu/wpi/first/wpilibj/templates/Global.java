/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Ultrasonic;


/**
 *
 * @author TomasR
 */
public class Global {
    private static final DriverStationLCD dlg = DriverStationLCD.getInstance();
    private static final Talon launch1=new Talon(5),launch2=new Talon(6),launch3=new Talon(7),launch4=new Talon(8);
    public static Encoder ShootEnc = new Encoder(1,2);
    public static DigitalInput endStop = new DigitalInput(3);
    public static DigitalInput pickupExtended=new DigitalInput(4);
    public static Victor pickup = new Victor(9);
    public static Relay cameraLight=new Relay(3);
    public static Preferences pref = Preferences.getInstance();
    public static RobotDrive drive = new RobotDrive(1,2,3,4);
    public static XboxController control=new XboxController(1),shootControl=new XboxController(2);
    public static DoubleSolenoid driveShift=new DoubleSolenoid(1,2);
    public static DoubleSolenoid pickupSol=new DoubleSolenoid(3,4);
    public static Compressor comp=new Compressor(7,2);
    public static int errorLine=0;
    public static boolean fastGear=false,pickupExtend=false;
    public static Timer t1=new Timer(),shootT=new Timer(),s5Timer=new Timer();
    public static Ultrasonic frontDist=new Ultrasonic(5,6);
    public static boolean novision=true;
    
    public static class Shooter{
        public static void set(double i){
            if (SmartDashboard.getBoolean("overrideShoot",false)||pickupExtended.get()) {
                launch1.set(-i);
                launch2.set(-i);
                launch3.set(i);
                launch4.set(i);
            }
            else stop();
        }
        public static void stop(){
            launch1.stopMotor();
            launch2.stopMotor();
            launch3.stopMotor();
            launch4.stopMotor();
        }
    }
    public static void UserMessage(int line,String message){
        for (int i = 0; 22<message.length()||i>30; i++) message+=" ";
        if (line==1)dlg.println(DriverStationLCD.Line.kUser1, 1, message);
        else if (line==2)dlg.println(DriverStationLCD.Line.kUser2, 1, message);
        else if (line==3)dlg.println(DriverStationLCD.Line.kUser3, 1, message);
        else if (line==4)dlg.println(DriverStationLCD.Line.kUser4, 1, message);
        else if (line==5)dlg.println(DriverStationLCD.Line.kUser5, 1, message);
        else if (line==6)dlg.println(DriverStationLCD.Line.kUser6, 1, message);
    }
    public static void UserMessageUpdate(){
        dlg.updateLCD();
    }
}
