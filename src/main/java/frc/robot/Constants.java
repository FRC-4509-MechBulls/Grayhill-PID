package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class Constants {
    public static XboxController controller = new XboxController(0);

    public static final JoystickButton button = new JoystickButton(controller, XboxController.Button.kA.value);
    public static TalonSRX _motor = new TalonSRX(2);

    double ticksPerRotation = 256;
    double wheelDiameter = 6; //inches
    public double ticksPerInch = (ticksPerRotation)/(wheelDiameter*Math.PI);
    public double ticksPerFoot = ticksPerInch*12;

}