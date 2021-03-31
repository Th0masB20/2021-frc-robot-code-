// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.vision.Vision;

public class AutonomousVisionSubsystem extends SubsystemBase {
  /** Creates a new AutonomusVision. */
  Thread thread;
  NetworkTable gripTable;
  NetworkTable gripTableGreen;


  int centerX1 = -1, centerX2 = -1, centerX3 = -1;
  int centerY1 = -1, centerY2 = -1;

  //position relative to center of string
  double ballPosition;
  int path = -1;
  //for updatePath();
  double count = 0;
  double pathValue = 0;
  boolean startTimerRun = false;


  //for average ball length at the start
  int ballLength;
  int ballCount = 0;

  //array lengths
  int lengthX, lengthY, initialBallLength;

  //to get values from network table
  double [] getValues = new double [0];

  int [] xPosition = new int [3];
  int [] initialYPosition = new int [2];


  DriveSubsystem drive;
  BeltSubsystem belt;
  IntakeMotorSubsystem intake;

  boolean inCenter =false;
  boolean ballHasCollected;

  long startTime = 0l;
  //for stoping time
  boolean doneWaiting = false;
  boolean startTimer = false;
  //for update wait
  long startWaitUpdateTime = 0l;

  //so that the code doesn't get wrong data from latency 
  long startWaitToContinue = 0l;
  boolean waitAndContinue = false;

  int ballsCollected = 0;
  boolean done = false;
  boolean complete = false;
  boolean doneRotation1 = false, doneRotation2 = false;

  boolean HaltAndRunBelt = false;
  long startBeltTimer = 0l;
  

  public AutonomousVisionSubsystem(UsbCamera camera, DriveSubsystem d, BeltSubsystem belt, IntakeMotorSubsystem intake) {
    gripTable = NetworkTableInstance.getDefault().getTable("GRIP/myContoursReport");
    drive = d;
    this.belt = belt;
    this.intake = intake;

    thread = new VisionThread(camera, new Vision(), pipeline -> {
      update();
    });

    thread.start();
  }
  
  public void printStuff()
  {
      SmartDashboard.putBoolean("timer", doneWaiting);
      SmartDashboard.putBoolean("done", done);

      SmartDashboard.putBoolean("Collected ball green", ballHasCollected);
      SmartDashboard.putNumber("left motor speed", drive.getLMotorSpeed());
      SmartDashboard.putNumber("right motor speed", drive.getRMotorSpeed());
      SmartDashboard.putNumber("collected balls", ballsCollected);
  }
  
  
  public void updateCenterXYPosition(){
    /**update ball values */

    lengthX = gripTable.getEntry("centerX").getDoubleArray(getValues).length;

    //x values
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

    if(lengthX == 0){
      xPosition[2] = -1;
      xPosition[1] = -1;
      xPosition[0] = -1;
    }
    
    centerX1 = xPosition[0];
    centerX2 = xPosition[1];
    centerX3 = xPosition[2];


    // y values
    lengthY = gripTable.getEntry("centerY").getDoubleArray(getValues).length;
      if(lengthY > 2){
        lengthY = 2;
      }
    
      if(lengthY != 0){
        for(int i = lengthY - 1; i >= 0; i--){
          initialYPosition[i] = (int)gripTable.getEntry("centerY").getDoubleArray(getValues)[i];
        }
      }

      centerY1 = initialYPosition[0];
      centerY2 = initialYPosition[1];

      if(lengthY == 1 && initialYPosition[1] != -2){
        initialYPosition[1] = -1;
      }
    

     /**then process them to get their position relative to the center */
     if(gripTable.getEntry("centerX").getDoubleArray(getValues).length >= 1){
      ballPosition = centerX1 - (Constants.width/2);
      inCenter = ((ballPosition * 2) / Constants.width > -Constants.rotationDeadZOne) && ((ballPosition * 2) / Constants.width < Constants.rotationDeadZOne);

      if(!inCenter){
        drive.rotate((ballPosition * 2) / Constants.width);
      }
    }
    else{  
      inCenter = false;
    }  

    /**then check if a ball was collected */
    if((int)gripTable.getEntry("centerY").getDoubleArray(getValues).length != 0 && (int)gripTable.getEntry("centerY").getDoubleArray(getValues)[0] >= (Constants.height - 20) 
     && inCenter && !waitTime(startWaitUpdateTime, 1000))
    {
      ballHasCollected = true;
      startWaitUpdateTime = System.currentTimeMillis();
    }
  }


  public void runPath(){
    //always move intake motors
    if(HaltAndRunBelt){
        belt.moveBelt(0.5);
        drive.driveFor(0.5, startBeltTimer, 1500);
        HaltAndRunBelt = waitTime(startBeltTimer, 2000);
    }
    else{
      belt.stopBelt();
    }

    if(!done && !HaltAndRunBelt){
      if(ballHasCollected){
        ballsCollected++;
        doneWaiting = false;
        startTimer = false;
        ballHasCollected = false;
        HaltAndRunBelt = true;

        startWaitToContinue = System.currentTimeMillis();
        startBeltTimer = System.currentTimeMillis();
      }

      
      //if red path
      if(path == 1){
        //ball is not to the right
        if(ballsCollected == 1 && !doneRotation1){
          //rotate right
            drive.rotate(1);

          //if there is a ball to the right
          if(centerX1 != -1 && ballPosition > Constants.width/2 - 30){
            doneRotation1 = true;
          }
        }

        if(ballsCollected == 2 && !doneRotation2){
          //rotate left
          drive.rotate(-1);
          //if there is a ball to the left
          if(centerX1 != -1 && ballPosition < -Constants.width/2 + 30){
            doneRotation2 = true;
          }
        }

        if(ballsCollected == initialBallLength){
          done = true;
        }
      }
      else if(path == 0){
        if(ballsCollected == 1 && !doneRotation1){
          //rotate left
            drive.rotate(-1);
          
           //if there is a ball to the left
           if(centerX1 != -1 && ballPosition < -Constants.width/2 + 30){
            doneRotation1 = true;
          }
        }

        if(ballsCollected == 2 && !doneRotation2){
          //rotate right
            drive.rotate(1);
          
          //if there is a ball to the right
          if(centerX1 != -1 && ballPosition > Constants.width/2 - 30){
            doneRotation2 = true;
          }
        }

        if(ballsCollected == initialBallLength){
          done = true;
        }
      }
      

    if(inCenter) {    
      //detected first ball
      if(ballsCollected == 0 && !doneWaiting && !waitAndContinue){
        SmartDashboard.putNumber("First Timer: ", System.currentTimeMillis() - startTime);
        if(!startTimer){
          startTime = System.currentTimeMillis();
          startTimer = true;
        }
        doneWaiting = drive.stopForTime(startTime,1000);/**make a stop with time method */
      }

      //detected second ball
      if(ballsCollected == 1 && !doneWaiting && !waitAndContinue && doneRotation1){
        SmartDashboard.putNumber("Second Timer: ", System.currentTimeMillis() - startTime);
        if(!startTimer){
          startTime = System.currentTimeMillis();
          startTimer = true;
        }
        doneWaiting = drive.stopForTime(startTime,1000);
      }

      //detected 3rd ball
      if(ballsCollected == 2 && !doneWaiting && !waitAndContinue && doneRotation2){
        if(!startTimer){
          startTime = System.currentTimeMillis();
          startTimer = true;
        }
        doneWaiting = drive.stopForTime(startTime,1000);
      }

       if(doneWaiting){
         //drive forward
          drive.drive(0.5);
        }
      }
    }

    if(done && !HaltAndRunBelt){
      if(path == 1){
        //ball is not to the right
        if(gripTableGreen.getEntry("centerX").getDoubleArray(getValues).length == 0){
          //rotate right
          drive.rotate(0.5);
        }
        else{
          calculateCenterDisplacement();

          if(!inCenter){
            drive.rotate((ballPosition * 2) / Constants.width);
          }

          if(!doneWaiting){
            if(!startTimer){
              startTime = System.currentTimeMillis();
              startTimer = true;
            }
            doneWaiting = drive.stopForTime(startTime,1000);/**make a stop with time method */
          }
        }
      }

      if(path == 0){
        //ball is not to the right
         if(gripTableGreen.getEntry("centerX").getDoubleArray(getValues).length == 0){
          //rotate left
          drive.rotate(-0.5);
        }
        else{
          calculateCenterDisplacement();
          if(!inCenter){
            drive.rotate((ballPosition * 2) / Constants.width);
          }
        }

        if(!doneWaiting){
            if(!startTimer){
              startTime = System.currentTimeMillis();
              startTimer = true;
            }
            doneWaiting = drive.stopForTime(startTime,3000);/**make a stop with time method */
          }
      }

      if(doneWaiting){
        drive.drive(0.5);
        if(gripTableGreen.getEntry("centerX").getDoubleArray(getValues).length == 0){
          complete = true;
        }
      }
    }


  }

  public void calculateCenterDisplacement(){
    if(gripTableGreen.getEntry("centerX").getDoubleArray(getValues).length >= 1){
      ballPosition = gripTableGreen.getEntry("centerX").getDoubleArray(getValues)[0] - (Constants.width/2);
      inCenter = ((ballPosition * 2) / Constants.width > -0.25f) && ((ballPosition * 2) / Constants.width < 0.25f); 
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

  if(gripTable.getEntry("centerX").getDoubleArray(getValues).length > 0 && ballCount < 1000){
    ballLength += gripTable.getEntry("centerX").getDoubleArray(getValues).length;
    ballCount++;
    initialBallLength = (int)Math.round(ballLength/ballCount);
  } 


  if(getPath() > -1 && count < 1000){
    pathValue += getPath();
    count++;
    path = (int)Math.round(pathValue/count);
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

    SmartDashboard.putNumber("Number of balls", initialBallLength);
  }

  public boolean waitTime(long start, int timeMil){
    if(System.currentTimeMillis() - start < timeMil){
      return true;
    }
    return false;
  }


  public void update(){
    updateCenterXYPosition();
  }

  public  boolean isComplete() {
    return complete;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
