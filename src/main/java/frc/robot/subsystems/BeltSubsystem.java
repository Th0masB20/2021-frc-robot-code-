// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class BeltSubsystem extends SubsystemBase {
  VictorSP beltMotor;
  double speed;
  /** Creates a new BeltSubsystem. */
  public BeltSubsystem() {
    beltMotor = new VictorSP(Constants.beltPort);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void moveBelt(XboxController x){
    speed = x.getY(Hand.kRight);
    if(speed > -Constants.deadZone && speed < Constants.deadZone) speed = 0;

    beltMotor.set(speed);
  }

  public void moveBeltNormal(double speed){
    beltMotor.set(speed);
  }

  public void stopBelt(){
    beltMotor.set(0);
  }
}
