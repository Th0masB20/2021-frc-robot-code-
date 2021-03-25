// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DriveSubsystem extends SubsystemBase {
  VictorSP motorR1;
  VictorSP motorR2;
  VictorSP motorL1;
  VictorSP motorL2;

  SpeedControllerGroup rightMotors;
  SpeedControllerGroup leftMotors;
  
  /** Creates a new DriveSubsystem. */
  public DriveSubsystem() {
    motorR1 = new VictorSP(Constants.rPort1);
    motorR2 = new VictorSP(Constants.rPort2);
    motorL1 = new VictorSP(Constants.lPort1);
    motorL2 = new VictorSP(Constants.lPort2);

    rightMotors = new SpeedControllerGroup(motorR1, motorR2);
    leftMotors = new SpeedControllerGroup(motorL1, motorL2);

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void drive(double leftSpeed, double rightSpeed){
    if((leftSpeed < 0.2f && leftSpeed > -0.2f) || (rightSpeed < 0.2f && rightSpeed > -0.2f))  
    {
      stop();
    }

      rightMotors.set(-rightSpeed * 0.75);
      leftMotors.set(leftSpeed * 0.75);
  }


  public void rotateLeft(double speed){
    rightMotors.set(-speed * 0.75);
    leftMotors.set(-speed * 0.75);
  }

  public void rotateRight(double speed){
    leftMotors.set(speed * 0.75);
    rightMotors.set(speed * 0.75);
  }

  //normal overall rotation
  public void rotate(double speed){
    leftMotors.set(speed * 0.30);
    rightMotors.set(speed * 0.30);
  }

  public void stop(){
      rightMotors.set(0);
      leftMotors.set(0);    
  }

  
}
