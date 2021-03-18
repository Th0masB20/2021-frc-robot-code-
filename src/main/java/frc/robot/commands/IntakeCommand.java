// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeMotorSubsystem;
import frc.robot.subsystems.IntakePistonSubsystem;

public class IntakeCommand extends CommandBase {
  IntakePistonSubsystem intakePistons;
  IntakeMotorSubsystem intakeMotor;

  /** Creates a new IntakeCommand. */
  public IntakeCommand(IntakePistonSubsystem intakeP, IntakeMotorSubsystem intakeM) {
    intakePistons = intakeP;
    intakeMotor = intakeM;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(intakePistons, intakeMotor);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    intakePistons.IntakePistons(RobotContainer.xboxController);
    intakeMotor.moveIntake(RobotContainer.xboxController);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    intakeMotor.stopIntake();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
