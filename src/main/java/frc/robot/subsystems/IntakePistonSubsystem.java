// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class IntakePistonSubsystem extends SubsystemBase {
  /** Creates a new IntakePiston. */
  private DoubleSolenoid s1;
  private DoubleSolenoid s2;
  private int numPresses = 0;
  private long start = 0l;
  private boolean wait;


  public IntakePistonSubsystem() {
    s1 = new DoubleSolenoid(Constants.solinoid1, Constants.solinoid2);
    s2 = new DoubleSolenoid(Constants.solinoid3, Constants.solinoid4);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void IntakePistons(boolean activate) {
    wait = waitTime(start, 250);
    if (wait && activate && numPresses == 0) {
      s1.set(Value.kForward);
      s2.set(Value.kForward);
      numPresses++;
      activate = false;

      restartTime();        
    }
    else if(wait && activate && numPresses == 1){
      s1.set(Value.kReverse);
      s2.set(Value.kReverse);
      numPresses--;
      activate = false;

      restartTime();
    }
  }

  public boolean waitTime(long start, int timeMil){
    if(System.currentTimeMillis() - start > timeMil){
      return true;
    }
    return false;
  }

  public void restartTime(){
    start = System.currentTimeMillis(); 
  }
}
