// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.vision.Vision;

public class AutonomusVision extends SubsystemBase {
  /** Creates a new AutonomusVision. */
  VisionThread thread;
  NetworkTable gripTable;
  int desplacement;

  private final Object imgLock = new Object();
  int centerX1, centerX2, centerX3;

  double [] getValues = new double [0];
  int [] position = new int [3];

  int length;

  public AutonomusVision(UsbCamera camera) {
    gripTable = NetworkTableInstance.getDefault().getTable("GRIP/myContoursReport");

    
    thread = new VisionThread(camera, new Vision(), pipeline -> {
    });

    //thread.start();
    int path = getPath();
  }
  
  public void printStuff(){
    updateCenterPosition();

      if(getPath() == 1)SmartDashboard.putString("Path", "red");
      else if(getPath() == 0)SmartDashboard.putString("Path", "blue");
      else SmartDashboard.putString("Path", "none");


      SmartDashboard.putNumber("X1", centerX1);
      SmartDashboard.putNumber("X2", centerX2);
      SmartDashboard.putNumber("X3", centerX3);
  }
  
  public void updateCenterPosition(){
    length = gripTable.getEntry("centerX").getDoubleArray(getValues).length;
    if(length > 3){
      length = 3;
    }

    for(int i = length - 1; i >= 0; i--){
      position[i] = (int)gripTable.getEntry("centerX").getDoubleArray(getValues)[i];
    }
    if(length == 2){
      position[2] = 0;
    }

    if(length == 1){
      position[2] = 0;
      position[1] = 0;
    }
    
    centerX1 = position[0];
    centerX2 = position[1];
    centerX3 = position[2];
  }

  int area1, area2;

  public int getPath(){
    if(centerX1 != -1 && centerX2 != -1){
      //ball 2 to the right of first ball
      if(centerX2 > centerX1){
        return 1;
      }
      else {
        return 0;
      }
    }
    return -1;
  }

  public void doPath(){
    //if(centerX3)
  }


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
