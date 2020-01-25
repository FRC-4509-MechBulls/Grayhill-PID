package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class Constants {
    public static XboxController controller = new XboxController(0);

    public static final JoystickButton button = new JoystickButton(controller, XboxController.Button.kA.value);
    public static TalonSRX _motor = new TalonSRX(2);

    static double ticksPerRotation = 256;
    static double wheelDiameter = 4; //inches
    static double targetPosition = 15; //in feet
    public static double ticksPerInch = (ticksPerRotation)/(wheelDiameter*Math.PI);
    public static double ticksPerFoot = ticksPerInch*12;

    // TEST STUFF

    public static double target = 4096*10;

}