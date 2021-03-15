// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.DriveTrain;
import frc.robot.commands.IntakeCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakePistonSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final DriveSubsystem driveSub;
  private final DriveTrain driveCommand;
  private final IntakePistonSubsystem intakePSubsystem;
  private final IntakeCommand intake;
  private final Compressor c;

  public static Joystick rightStick;
  public static Joystick leftStick;
  public static XboxController xboxController;
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    driveSub = new DriveSubsystem();
    driveCommand = new DriveTrain(driveSub);

    intakePSubsystem = new IntakePistonSubsystem();
    intake = new IntakeCommand(intakePSubsystem);

    rightStick = new Joystick(Constants.rStickPort);
    leftStick = new Joystick(Constants.lStickPort);
    xboxController = new XboxController(Constants.xboxPort);

    driveSub.setDefaultCommand(driveCommand);
    intakePSubsystem.setDefaultCommand(intake);


    c = new Compressor(0);
    c.start();
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {}

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return null;
  }
}
