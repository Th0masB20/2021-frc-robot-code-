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

  int centerX1, centerX2, centerX3;
  int centerY1, centerY2;

  //position relative to center of string
  double ballPosition;
  int path = -1;

  //array lengths
  int lengthX, lengthY, initialBallLength;

  //to get values from network table
  double [] getValues = new double [0];

  int [] xPosition = new int [3];
  int [] initialYPosition = new int [2];


  DriveSubsystem drive;
  BeltSubsystem belt;
  IntakeMotorSubsystem intake;

  boolean doRotation = false, hasDoneRotation = false;
  boolean inCenter =false;

  long startTime = 0l;
  boolean doneWaiting = false;

  //for updatePath();
  double count = 0;
  double pathValue = 0;

  public AutonomousVisionSubsystem(UsbCamera camera, DriveSubsystem d, BeltSubsystem belt, IntakeMotorSubsystem intake) {
    gripTable = NetworkTableInstance.getDefault().getTable("GRIP/myContoursReport");
    drive = d;
    this.belt = belt;
    this.intake = intake;

    thread = new VisionThread(camera, new Vision(), pipeline -> {
      update();
    });

    //thread.start();
  }
  
  public void printStuff()
  {
      SmartDashboard.putBoolean("timer", doneWaiting);
      SmartDashboard.putNumber("balls collected", ballsCollected);
      SmartDashboard.putBoolean("done", done);

      SmartDashboard.putNumber("Path after enable", path);
      SmartDashboard.putNumber("left motor speed", drive.getLMotorSpeed());
      SmartDashboard.putNumber("right motor speed", drive.getRMotorSpeed());

      SmartDashboard.putNumber("Number of balls", initialBallLength);
  }
  
  
  public void updateCenterXYPosition(){
    /**-2 means absolutely empty, -1 means there was previously a value */

    //x values
    lengthX = gripTable.getEntry("centerX").getDoubleArray(getValues).length;
    if(lengthX > 3){
      lengthX = 3;
    }

    for(int i = lengthX - 1; i >= 0; i--){
      if(gripTable.getEntry("centerX").getDoubleArray(getValues).length == lengthX){
        xPosition[i] = (int)gripTable.getEntry("centerX").getDoubleArray(getValues)[i]; 
      }
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

      if(lengthY == 1 && initialYPosition[1] != -2){
        initialYPosition[1] = -1;
      }
    }

  }

  int stops = 0;
  int ballsCollected = 0;
  boolean done = false;
  public void runPath(){
    if(ballsCollected == 3){
      done = true;
    }
    intake.moveIntake(1);

    if(gripTable.getEntry("centerX").getDoubleArray(getValues).length >= 1){
        ballPosition = centerX1 - (600/2);
        inCenter = ((ballPosition * 2) / 600 > -0.25f) && ((ballPosition * 2) / 600 < 0.25f);
        if(!inCenter){
          drive.rotate((ballPosition * 2) / 600);
          startTime = System.currentTimeMillis();
        }


      if(inCenter) {
        //detectedd first ball
        if(ballsCollected == 0 && !doneWaiting){
          doneWaiting = drive.stopForTime(startTime,3000);/**make a stop with time method */
        }

        //detected second ball
        if(ballsCollected == 1 && !doneWaiting){
          doneWaiting = drive.stopForTime(startTime,3000);
        }

        //detected 3rd ball
        if(ballsCollected == 2 && !doneWaiting){
          doneWaiting = drive.stopForTime(startTime,3000);
        }

        if(doneWaiting){
          drive.drive(-0.5);
        }

      }
    }

      
      if(doRotation && !hasDoneRotation){
        if(lengthX >= 1){
          doRotation = false;
          hasDoneRotation = true;
        }
        if(path == 1){
          //rotate left
          drive.rotate(0.5f);
          }

        if(path == 0){
          //rotate right
          drive.rotate(-0.5f);
          }
        }
    }


  //generates path based on second ball's position
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
  

//in disable periodic
public void updatePath() {

  initialBallLength = gripTable.getEntry("centerX").getDoubleArray(getValues).length;

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

  public boolean waitTime(long start, int timeMil){
    if(System.currentTimeMillis() - start > timeMil){
      return true;
    }
    return false;
  }


  public void update(){
    updateCenterXYPosition();

    if(!done){
      //if collected 1 ball
    if(lengthX == 2 && ballsCollected == 0){
      ballsCollected = 1;
      doneWaiting = false;
    }

    if(lengthX == 1 && ballsCollected == 1 && initialBallLength == 3){
      if(path == 1){
        drive.rotate(-0.5);
      }   
      if(path == -1){
        drive.rotate(0.5);
      }      
    }
    //if collected 2'nd ball but not detecting 3rd
    if(lengthX == 0 && initialBallLength == 3 && ballsCollected == 1 && !hasDoneRotation){
      ballsCollected = 2;
      doneWaiting = false;
      doRotation = true;
    }//if collected 2nd ball and finished
    else if (lengthX == 0 && initialBallLength == 2 && ballsCollected == 1){
      ballsCollected = 2;
      doneWaiting = false;
    }

    //gets last ball and finishes 
    if(lengthX == 0 && ballsCollected == 2 ){
      ballsCollected = 3;
      doneWaiting = false;
    }
    }

    if(ballsCollected == initialBallLength){
      done = true;
    }

    if(lengthX == 0 && ballsCollected == initialBallLength){

    }
  }

 
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
