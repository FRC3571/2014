/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 *
 * @author TomasR
 */
public class Teleop {

    static double driveX=0,driveY=0;
    static int driveDelay=0,dmode=0; // Stops getting joystick input for couple of rounds
    static boolean previousX=false,previousY=false,previousSLB=false,previousSStart=false,previousStart=false,previousBack=false,pReset=false,previousRS=false,previousDBack=false;
    static double sensitivity=0; //inverted multiplier for drive
    static double shootSpeed=0,asp=0.1,asls=0.5,ashs=0.9;
    static boolean sensitivityControl=false;
    static boolean stop=false,shoot=false,reverseShoot=false,resetEnc=false,shooting=false,run1=false,pickupRun=false,lpickup=false,lightON=false;
    
    
    public static void teleopStart(){
        SmartDashboard.putNumber("Checkpoint", 1);
        Global.Shooter.stop();
        Global.pickup.stopMotor();
        Global.comp.start();
        sensitivity=Global.pref.getDouble("sensitivity",0);
        SmartDashboard.putNumber("Slider 1", (1-sensitivity)*100);
        Global.UserMessage(1, "Low Gear");
        Global.ShootEnc.start();
        SmartDashboard.putBoolean("TeleopInit", true);
        SmartDashboard.putBoolean("CosShooter", Global.pref.getBoolean("cosShooter",false));
        SmartDashboard.putBoolean("overridePickup", false);
        if (Global.pickupExtended.get() && !Global.endStop.get()) {
            lpickup=true;
            reverseShoot=true;
        }
        else Global.pickupSol.set(DoubleSolenoid.Value.kReverse);
        Global.fastGear=false;
        run1=true;
        try {
            Vision.VisionStart();
        } catch (Exception ex) {
            ex.printStackTrace();
                SmartDashboard.putString("error", "TeleopInit: "+ex.toString());
        }
        
    }
    public static void teleop() {
        try{
            if (Global.control.back()&&!previousDBack) {
                dmode=(dmode+1)%2;
            }
            SmartDashboard.putNumber("Checkpoint", 2);
            SmartDashboard.putNumber("Distance", Global.frontDist.getRangeInches());
            sensitivity=1-(Math.floor(Math.ceil(SmartDashboard.getNumber("Slider 1")))/100);
            SmartDashboard.putNumber("Slider 1", (1-sensitivity)*100);
            Global.UserMessage(2, "Sensitivity"+(sensitivityControl?"C ":" ")+"= "+(sensitivityControl?(1-sensitivity):(Global.control.LS()?1:0.8)));
            if (Global.control.Y() && !previousY) {
                sensitivityControl=!sensitivityControl;
            }
            stop=false;
            SmartDashboard.putNumber("DriveMode", dmode);
            if (driveDelay==0 && !Global.control.B()) {
                if (dmode==0) {
                    driveX= (Global.fastGear? Global.control.leftStickX()*0.75:Global.control.leftStickX())*(sensitivityControl?(1-(sensitivity)):1);
                    driveY= (Global.control.A() ? Global.control.leftStickY() : -Global.control.leftStickY())*(sensitivityControl?(1-sensitivity):(Global.control.LS()?1:0.8));
                }
                else if (dmode==1) {
                    driveX=(Global.fastGear? Global.control.leftStickX()*0.75:Global.control.leftStickX())*(sensitivityControl?(1-(sensitivity)):1);
                    driveY=-Global.control.trigger();
                }
            }
            else if(driveDelay>0) driveDelay--;
            if (Global.control.B() || (Global.control.A() && dmode==1) || Math.sqrt((Global.control.leftStickX()*Global.control.leftStickX())+(Global.control.leftStickY()*Global.control.leftStickY())+4*(Global.control.trigger()*Global.control.trigger()))<0.2) {
                stop=true;
            }
            if (pickupRun) {
                Global.pickup.set(Global.pref.getDouble("pickupExtendMotor", 0.25));
            }
            else Global.pickup.set(Math.abs(Global.shootControl.rightStickY())>0.2?Global.shootControl.rightStickY():0);
            if (pickupRun && Global.pickupExtended.get()) {
                pickupRun=false;
            }
        SmartDashboard.putNumber("Checkpoint", 3);
            SmartDashboard.putNumber("driveDelay", driveDelay);
            if (Global.control.X() && !previousX) {
                if (Global.fastGear) {
                    Global.fastGear=false;
                    Global.driveShift.set(DoubleSolenoid.Value.kReverse);
                    driveDelay=5;
                    driveX/=2.27;
                    driveY/=2.27;
                    Global.UserMessage(1, "Low Gear");
                }
                else{
                    Global.fastGear=true;
                    Global.driveShift.set(DoubleSolenoid.Value.kForward);
                    driveDelay=5;
                    driveX=Math.min(driveX*2.27, 1);
                    driveY=Math.min(driveY*2.27, 1);
                    Global.UserMessage(1, "High Gear");
                    
                }
            }
            if ((Global.shootControl.LB()&& !previousSLB)||((lpickup && Global.endStop.get()))) {
                if (Global.pickupExtend) {
                    if (Global.endStop.get()|| SmartDashboard.getBoolean("overridePickup")) {
                        Global.pickupExtend=false;
                        Global.pickupSol.set(DoubleSolenoid.Value.kReverse);
                        lpickup=false;
                    }
                    else {
                        reverseShoot=true;
                        lpickup=true;
                    }
                }
                else{
                    Global.pickupExtend=true;
                    Global.pickupSol.set(DoubleSolenoid.Value.kForward);
                    pickupRun=true;
                }
            }
            SmartDashboard.putBoolean("endStop", Global.endStop.get());
            //if (Global.endStop.get())Global.ShootEnc.reset();
            if (Global.endStop.get()&&!pReset) {
                Global.ShootEnc.reset();
            }
            asp=SmartDashboard.getNumber("AutoShootLPerc");
            asls=SmartDashboard.getNumber("AutoShootLS", 0.5);
            ashs=SmartDashboard.getNumber("AutoShootHS", 0.9);
            SmartDashboard.putNumber("Checkpoint", 4);
            shootSpeed=0;
            if (Global.shootControl.start()&& !previousSStart) {
                shoot=true;
            }
            if (Math.abs(Global.shootControl.trigger())>0.1) {
                shootSpeed=Global.shootControl.trigger();
            }
            else shootSpeed=0;
            if (shootSpeed>0) {
                shootSpeed=1;
            }
            if (shoot && Global.ShootEnc.get()<SmartDashboard.getNumber("ShootAngle")) {
                shootSpeed=(Global.ShootEnc.get()<((int)(SmartDashboard.getNumber("ShootAngle")*asp))?asls:ashs);
            }
            else if ((shoot  && Global.ShootEnc.get()>=SmartDashboard.getNumber("ShootAngle"))|| (Global.shootControl.back()&&!previousBack)) {
                shoot=false;
                reverseShoot=true;
            }
            if (reverseShoot && !Global.endStop.get()) {
                shootSpeed=(Global.pref.getBoolean("cosShooter",false)?-1:-0.5);
            }
            else if (reverseShoot && Global.endStop.get()) {
                shootSpeed=0;
                reverseShoot=false;
            }
            SmartDashboard.putBoolean("ProgramShooting", shoot);
            if (shootSpeed<0 && Global.endStop.get()) {
                shootSpeed=0;
            }
            if (run1) {
                resetEnc=true;
                run1=false;
            }
            if (resetEnc && !Global.endStop.get()) {
                shootSpeed=-0.5;
            }
            else if (resetEnc && Global.endStop.get()) {
                shootSpeed=0;
                Global.ShootEnc.reset();
                resetEnc=false;
            }
            if (shootSpeed>0&& Global.ShootEnc.get()>125) {
                shootSpeed=0;
            }
            SmartDashboard.putBoolean("resetEnc", resetEnc);
            if(shootSpeed!=0 && Global.pickupExtended.get()){
                Global.Shooter.set(shootSpeed<0 ?
                        shootSpeed*
                        (Global.pref.getBoolean("cosShooter",false)?
                                (0.2*Math.cos(Math.PI+(Global.ShootEnc.get()*(Math.PI/SmartDashboard.getNumber("maxShootAngle"))))+0.3)
                                :0.1) 
                        : shootSpeed);
            }
            else Global.Shooter.stop();
                    
            SmartDashboard.putNumber("ShootEnc", Global.ShootEnc.get());
            SmartDashboard.putString("drive", "X="+driveX+" Y="+driveY);
            SmartDashboard.putBoolean("pickupExtended", Global.pickupExtended.get());
            Global.pref.putDouble("sensitivity", sensitivity);
            if (Global.control.start() && !previousStart) {
                if (!lightON) {
                    Global.cameraLight.set(Relay.Value.kForward);
                }
                else Global.cameraLight.set(Relay.Value.kOff);
                lightON=!lightON;
            }
            if ((Global.control.RS()||Global.shootControl.RS())&& !previousRS) {
                Global.s5Timer.reset();
                Global.s5Timer.start();
            }
            if (Global.s5Timer.get()>5) {
                Global.s5Timer.stop();
            }
            SmartDashboard.putNumber("5s countdown", 5-Global.s5Timer.get());
            previousX=Global.control.X();
            previousY=Global.control.Y();
            previousSLB=Global.shootControl.LB();
            previousSStart=Global.shootControl.start();
            previousStart=Global.control.start();
            previousBack=Global.shootControl.back();
            previousDBack=Global.control.back();
            previousRS=Global.control.RS()||Global.shootControl.RS();
            pReset=Global.endStop.get();
            
        SmartDashboard.putNumber("CheckpointGo", 1);
            if (!stop) {
            Global.drive.arcadeDrive(driveY, driveX);
            }
            else Global.drive.stopMotor();
            Global.UserMessageUpdate();
            SmartDashboard.putBoolean("TeleopPeriodic", true);
        }
        catch(Exception ex){
            Global.UserMessage(1, ex.toString());
            Global.UserMessageUpdate();
            SmartDashboard.putString("error", "TeleopPeriodic: "+ex.toString());
        }
    }

}
