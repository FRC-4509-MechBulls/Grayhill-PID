/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private static final double Kp = 0.0; // <- when i say change Kp, it’s this!
  private static final double Ki = 0.0;
  private static final double Kf = 0.0; // no feed-forward on position control
  private static final double Kd = 0;
  private static final int IZone = 100; // IZone, this is explained below

  // I only have one joystick. here it is:
  Joystick joystick = new Joystick(0);

  // I also only have one Talon connected:
  TalonSRX _motor = new TalonSRX(2);

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    _motor.setSelectedSensorPosition(0);
    _motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);

    /*************************************************************************
     * Ramps. The argument is how many seconds it takes to go from 0 to 100% during
     * voltage control. There is a ramp for both open loop and closed (controlled)
     * loop
     */
    _motor.configOpenloopRamp(0.2);
    _motor.configClosedloopRamp(0.2);

    // Talon setup. Setting up + and - output for max and nominal.
    // These should never really change
    _motor.configNominalOutputForward(0, 0);
    _motor.configNominalOutputReverse(0, 0);
    _motor.configPeakOutputForward(1, 0);
    _motor.configPeakOutputReverse(-1, 0);
    _motor.config_kF(0, Kf, 0); // No feed-forward on position control
    _motor.config_kP(0, Kp, 0); // P factor!
    _motor.config_kI(0, Ki, 0);
    _motor.config_kD(0, Kd, 0); // note- these are defined above so they're easy to find and change.

    // Instantiate our RobotContainer. This will perform all our button bindings,
    // and put our
    // autonomous chooser on the dashboard.
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler. This is responsible for polling buttons, adding
    // newly-scheduled
    // commands, running already-scheduled commands, removing finished or
    // interrupted commands,
    // and running subsystem periodic() methods. This must be called from the
    // robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your
   * {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    // m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  int _loops = 0;

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("Current Pos:", _motor.getSelectedSensorPosition(0));
    // Output Encoder Values, joystick values, and velocity in encoder pulses per
    // 100msec
    double velocity = _motor.getSelectedSensorVelocity(0);
    // and put it up on the dashboard

    SmartDashboard.putNumber("Motor Encoder Velocity", velocity);
    double axis = joystick.getY();

    _motor.set(ControlMode.PercentOutput, axis);
    double targetPosition = joystick.getZ() * 4096 * 10;

    // reguluar teleop. Just give the motor power wherever you have the joystick set
    if (!joystick.getRawButton(1)) {
      if (Math.abs(axis) > 0.1) {
        _motor.set(ControlMode.PercentOutput, axis);
      }
      if (++_loops >= 10) {
        _loops = 0;
        SmartDashboard.putNumber("Trigger hit", targetPosition);
      }

    }
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
