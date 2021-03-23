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

  double centerX = 0;
  private final Object imgLock = new Object();

  public AutonomusVision(UsbCamera camera) {
    gripTable = NetworkTableInstance.getDefault().getTable("GRIP/myContoursReport");
    
    thread = new VisionThread(camera, new Vision(), (pipeline) -> {
       if(!pipeline.findContoursOutput().isEmpty()){
        Rect r = Imgproc.boundingRect(pipeline.findContoursOutput().get(0));
        synchronized(imgLock){
          centerX = r.x + r.width/2;
        }
       }
     });
  thread.start();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
  
  public void printStuff(){

  }
}
