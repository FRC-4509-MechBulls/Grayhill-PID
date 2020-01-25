/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.Util;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private static final double Kp = 2.0; // <- when i say change Kp, it’s this!
  private static final double Ki = 0.02;
  private static final double Kf = 0.0; // no feed-forward on position control
  private static final double Kd = 200;
  private static final int IZone = 150; // IZone, this is explained below

  // I only have one joystick. here it is:
  Joystick joystick = new Joystick(0);
 
  // I also only have one Talon connected:
  WPI_TalonSRX _motor = new WPI_TalonSRX(2);

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    //_motor.setSelectedSensorPosition(0);
    _motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

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
    _motor.setSensorPhase(true);
    _motor.setInverted(true);

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
    _motor.setSelectedSensorPosition( 0,0,1000);

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

    // while(_motor.getSelectedSensorPosition(0) > -256){
    //   SmartDashboard.putNumber("Current Pos:", _motor.getSelectedSensorPosition(0));

    //   _motor.set(.5);
    // }
    // _motor.set(0);


    // JEREMHY TEST PID STUFF FOR AUTONOMOUS


    double velocity = _motor.getSelectedSensorVelocity(0);
    //and put it up on the dashboard
    SmartDashboard.putNumber("Motor Encoder Position", _motor.getSelectedSensorPosition(0));
    SmartDashboard.putNumber("Motor Encoder Velocity", velocity);

    // if (_motor.getSelectedSensorPosition(0) > -50){
      _motor.set(ControlMode.Position, Constants.target);
    // } 

    // if (_motor.getSelectedSensorPosition(0) < 50){
      // _motor.set(ControlMode.Position, -Constants.target);
    // } 

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

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("Current Pos:", _motor.getSelectedSensorPosition(0));
    
    // Output Encoder Values, joystick values, and velocity in encoder pulses per
    // 100msec
    Constants.button.whenPressed(new ResetEncoderCommand());
    double velocity = _motor.getSelectedSensorVelocity(0);
    // and put it up on the dashboard
    // if(_motor.getSelectedSensorPosition(0) > 256){
    //   _motor.set(ControlMode.PercentOutput, 0);

    // }
    SmartDashboard.putNumber("Motor Encoder Velocity", velocity);
    double axis = joystick.getY();

    _motor.set(ControlMode.PercentOutput, axis);
    double targetPosition = joystick.getZ() * 4096 * 10;

    // reguluar teleop. Just give the motor power wherever you have the joystick set
    if (!joystick.getRawButton(1)) {
      if (Math.abs(axis) > 0.1) {
        _motor.set(ControlMode.PercentOutput, axis);
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
