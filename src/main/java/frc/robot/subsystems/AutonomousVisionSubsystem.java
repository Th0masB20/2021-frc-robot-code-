// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.vision.Vision;

public class AutonomousVisionSubsystem extends SubsystemBase {
  /** Creates a new AutonomusVision. */
  VisionThread thread;
  NetworkTable gripTable;
  boolean hasDoneRotation = false;

  int centerX1, centerX2, centerX3;
  int centerY1, centerY2;
  //position relative to center of string
  double ballPosition;
  int path = -1;

  //to get values from network table
  double [] getValues = new double [0];

  int [] xPosition = new int [3];
  int [] initialYPosition = new int [2];

  int lengthX, lengthY;

  DriveSubsystem drive;

  public AutonomousVisionSubsystem(UsbCamera camera, DriveSubsystem d) {
    gripTable = NetworkTableInstance.getDefault().getTable("GRIP/myContoursReport");
    drive = d;

    thread = new VisionThread(camera, new Vision(), pipeline -> {
    });

    //thread.start();
  }
  
  public void printStuff(){

      SmartDashboard.putNumber("X1", centerX1);
      SmartDashboard.putNumber("X2", centerX2);
      SmartDashboard.putNumber("X3", centerX3);

      SmartDashboard.putNumber("Y1", centerY1);
      SmartDashboard.putNumber("Y2", centerY2);
      SmartDashboard.putBoolean("has done rotation", hasDoneRotation);

      drive.printSpeed();

      SmartDashboard.putNumber("Path after enable", path);
  }
  
  public void updateCenterXYPosition(){

    //x values
    lengthX = gripTable.getEntry("centerX").getDoubleArray(getValues).length;
    if(lengthX > 3){
      lengthX = 3;
    }

    for(int i = lengthX - 1; i >= 0; i--){
      xPosition[i] = (int)gripTable.getEntry("centerX").getDoubleArray(getValues)[i];
    }
    if(lengthX == 2){
      xPosition[2] = -1;
    }

    if(lengthX == 1){
      xPosition[2] = -1;
      xPosition[1] = -1;
    }
    
    centerX1 = xPosition[0];
    centerX2 = xPosition[1];
    centerX3 = xPosition[2];


    // y values
    lengthY = gripTable.getEntry("centerY").getDoubleArray(getValues).length;
    if(lengthY < 2){
      if(lengthY > 2){
        lengthY = 2;
      }
    
      for(int i = lengthY - 1; i >= 0; i--){
        initialYPosition[i] = (int)gripTable.getEntry("centerY").getDoubleArray(getValues)[i];
      }

      centerY1 = initialYPosition[0];
      centerY2 = initialYPosition[1];
    }

  }
  public int getPath(){

    if(centerX1 != -1 && centerX2 != -1){
      //ball 2 to the right of first ball
      if(centerX2 > centerX1){
        if(centerY2 < centerY1){
          return 1;
        }
      }
      else if(centerX2 < centerX1){
        if(centerY2 < centerY1){
          return 0;
        }
      }

    }
    return -1;
  }

  public void runPath(){
    if(gripTable.getEntry("centerX").getDoubleArray(getValues).length >= 1){
        ballPosition = centerX1 - (600/2);
        if((ballPosition * 2) / 600 > -0.15f && (ballPosition * 2) / 600 < 0.15f) 
          drive.stop();

        else    
          drive.rotate((ballPosition * 2) / 600);
      }

      System.out.println(hasDoneRotation);
      if(!hasDoneRotation){
        if(path == 1 && centerX1 == -1 && centerX2 == -1){
          drive.rotateRight(0.5f);
          if(centerX1 > -1){
            hasDoneRotation = true;
          }
        }
      }
    }


    public void run(){
      updateCenterXYPosition();
      runPath();
    }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

double count = 0;
double pathValue = 0;

public void updatePath() {
  if(getPath() > -1 && count < 500){
    pathValue += getPath();
    count++;
    this.path = (int)Math.round(pathValue/count);
  }

  if(path == 1)
    {
      SmartDashboard.putString("Path", "red");
    }
    else if(path == 0){
      SmartDashboard.putString("Path", "blue");
    }
    else{
      SmartDashboard.putString("Path", "none");
    }
  }
  
}
