// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class IntakeMotorSubsystem extends SubsystemBase {
  /** Creates a new IntakeMotorSubsystem. */
  VictorSP intakeMotor;
  double inSpeed, outSpeed;

  public IntakeMotorSubsystem() {
    intakeMotor = new VictorSP(Constants.intakeP);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void moveIntake(XboxController x){
    inSpeed = x.getTriggerAxis(Hand.kRight);
    outSpeed = x.getTriggerAxis(Hand.kLeft);

    intakeMotor.set((inSpeed - outSpeed) * Constants.intakeSpeed);
  }

  public void stopIntake(){
    intakeMotor.set(0);
  }
}
