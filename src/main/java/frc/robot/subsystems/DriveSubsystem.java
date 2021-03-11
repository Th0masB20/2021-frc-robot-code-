// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DriveSubsystem extends SubsystemBase {
  VictorSP motorR1;
  VictorSP motorR2;
  VictorSP motorL1;
  VictorSP motorL2;
  
  /** Creates a new DriveSubsystem. */
  public DriveSubsystem() {
    motorR1 = new VictorSP(Constants.rPort1);
    motorR2 = new VictorSP(Constants.rPort2);
    motorL1 = new VictorSP(Constants.lPort1);
    motorL2 = new VictorSP(Constants.lPort2);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void drive(double leftJoy, double rightJoy){
    if((leftJoy > 0.2f || leftJoy < -0.2f) || (rightJoy > 0.2f || rightJoy < -0.2f)){
      motorR1.set(rightJoy);
      motorR2.set(rightJoy);
      motorL1.set(-leftJoy);
      motorL2.set(-leftJoy);
    }
    else
    {
      stop();
    }
  }

  public void stop(){
      motorR1.set(0);
      motorR2.set(0);
      motorL1.set(0);
      motorL2.set(0);
  }
}
