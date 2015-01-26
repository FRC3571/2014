/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author TomasR
 */
public class Vision {
    
    private static ColorImage image;
    private static VisionObjects vo[];
    private static AxisCamera camera;
    private static CriteriaCollection cc;
    private static TargetReport tr;
    private static BinaryImage ti,fi;
    private static ParticleAnalysisReport report;
    private static VisionObjects[] noVo=new VisionObjects[1];
    private static boolean visionStarted=false;
    public static void VisionStart() throws Exception{
        if (visionStarted && !Global.novision) {
            try{
                cc = new CriteriaCollection();
                cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_AREA, 250, 12000, false);
                tr = new Vision.TargetReport();
                camera=AxisCamera.getInstance("10.35.71.11");
                camera.writeResolution(AxisCamera.ResolutionT.k320x240);
                camera.writeBrightness(30);
                VisionProcessing();
                camera.getImage();
                visionStarted=true;
            }
            catch(Exception ex){
                Global.novision=true;
                throw ex;
            }
        }
    }

    public static VisionObjects[] VisionProcessing(){
        if (!Global.novision) {
            try {
                if (!visionStarted) VisionStart();
                fi=camera.getImage().thresholdHSV(105, 137, 146, 255, 133, 183).particleFilter(cc);
                if (fi.getNumberParticles()<1) return novo();
                vo=new VisionObjects[fi.getNumberParticles()];
                for (int i = 0; i < fi.getNumberParticles(); i++) {
                    vo[i]=new VisionObjects();
                    report=fi.getParticleAnalysisReport(i);
                    vo[i].aspectRatioV=scoreAspectRatio(fi,report,i,true);
                    vo[i].aspectRatioH=scoreAspectRatio(fi,report,i,false);
                    vo[i].centerX=report.center_mass_x;
                    vo[i].centerY=report.center_mass_y;
                    vo[i].height=report.boundingRectHeight;
                    vo[i].width=report.boundingRectWidth;
                    vo[i].area=report.particleArea;
                }
                fi.free();
                return vo;
            } catch (Exception ex) {
                SmartDashboard.putString("error", "VisionProcessing "+ex.toString());
                Global.novision=true;
                return novo();
            }
        }
        else return novo();
    }
    private static class TargetReport {

        int verticalIndex;
	int horizontalIndex;
	boolean Hot;
	double totalScore;
	double leftScore;
	double rightScore;
	double tapeWidthScore;
	double verticalScore;
    }
    private static VisionObjects[] novo(){ //No Visual Object
        noVo[0]=new VisionObjects();
        noVo[0].area=0;
        noVo[0].aspectRatioH=0;
        noVo[0].aspectRatioV=0;
        noVo[0].centerX=0;
        noVo[0].centerY=0;
        noVo[0].height=0;
        noVo[0].width=0;
        return noVo;
    }
    public static class VisionObjects
    {
        double aspectRatioV;
        double aspectRatioH;
        double area;
        int centerX;
        int centerY;
        int height;
        int width;
    }
    private static double scoreAspectRatio(BinaryImage image, ParticleAnalysisReport report, int particleNumber, boolean vertical) throws NIVisionException
    {
        double rectLong, rectShort, aspectRatio, idealAspectRatio;

        rectLong = NIVision.MeasureParticle(image.image, particleNumber, false, NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
        rectShort = NIVision.MeasureParticle(image.image, particleNumber, false, NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
        idealAspectRatio = vertical ? (4.0/32) : (23.5/4);	//Vertical reflector 4" wide x 32" tall, horizontal 23.5" wide x 4" tall
        
        //Divide width by height to measure aspect ratio
        if(report.boundingRectWidth > report.boundingRectHeight){
            //particle is wider than it is tall, divide long by short
            aspectRatio = ratioToScore((rectLong/rectShort)/idealAspectRatio);
        } else {
            //particle is taller than it is wide, divide short by long
            aspectRatio = ratioToScore((rectShort/rectLong)/idealAspectRatio);
        }
	return aspectRatio;
    }
    static double ratioToScore(double ratio)
	{
		return (Math.max(0, Math.min(100*(1-Math.abs(1-ratio)), 100)));
	}
    
}
