/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
/*
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 *TreeExpansionListener you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private final SendableChooser<String> m_chooser = new SendableChooser<>();
	// Declares joystick variables
	private Joystick joystick0 = new Joystick(0);
	private Joystick joystick1 = new Joystick(1);
	// Declares elevator motors
	VictorSPX elevator1 = new VictorSPX(constants.elevator1Id);
	VictorSPX elevator2 = new VictorSPX(constants.elevator2Id);
	// Declares drive motors
	Talon leftMotor = new Talon(constants.leftMotorId);
	Talon leftFollower = new Talon(constants.leftFollowerId);
	Talon rightMotor = new Talon(constants.rightMotorId);
	Talon rightFollower = new Talon(constants.rightFollowerId);
	// Declares slide drive motors
	Talon slideMain = new Talon(constants.slideMainId);
	Talon slideFollow = new Talon(constants.slideFollowerId);
	// Declares controller groups
	SpeedControllerGroup leftSide;
	SpeedControllerGroup rightSide;
	// Creates drive variable
	DifferentialDrive drive1;
	// Declares the elevator limit switch
	DigitalInput bottomLevel = new DigitalInput(constants.bottomLevelId);
	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
		m_chooser.addOption("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		leftSide = new SpeedControllerGroup(leftMotor, leftFollower);
		rightSide = new SpeedControllerGroup(rightMotor, rightFollower);
		drive1 = new DifferentialDrive(leftSide, rightSide);
		CameraServer.getInstance().startAutomaticCapture();
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
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString line to get the
	 * auto name from the text box below the Gyro
	 *
	 * <p>
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the SendableChooser
	 * make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
		case kCustomAuto:
			teleopPeriodic();
			break;
		case kDefaultAuto:
		default:
			teleopPeriodic();
			break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		// Sets the speed at which the robot is turning from both turning the joystick
		// and the sensitivity slider
		double rotateSpeed = joystick0.getRawAxis(2) * (((joystick0.getRawAxis(3) - 1) * -.125) + .5);
		// Sets the forward speed of the robot
		double forwardSpeed = joystick0.getRawAxis(1) * constants.slowDrivePower * -1;
		// If a button is pressed it allows the robot to move faster
		if (joystick0.getRawButton(1) == true) {
			forwardSpeed = forwardSpeed * constants.turboPower;
		}
		// Sets the robot to drive at the given speeds
		drive1.curvatureDrive(forwardSpeed, rotateSpeed, true);
		// Sets the elevator speed
		if (joystick1.getRawButton(8) == true) {
			elevator1.set(ControlMode.PercentOutput, joystick1.getRawAxis(1) * constants.elevatorPower * constants.elevatorSlowPower);
			elevator2.set(ControlMode.PercentOutput, joystick1.getRawAxis(1) * constants.elevatorPower * constants.elevatorSlowPower);
		} else {
			elevator1.set(ControlMode.PercentOutput, joystick1.getRawAxis(1) * constants.elevatorPower);
			elevator2.set(ControlMode.PercentOutput, joystick1.getRawAxis(1) * constants.elevatorPower);
		}
		// Controls slide drive
		if (joystick0.getRawButton(11) == true) {
			slideMain.set(constants.slidePower);
			slideFollow.set(constants.slidePower);
		} else if (joystick0.getRawButton(12) == true) {
			slideMain.set(-constants.slidePower);
			slideFollow.set(-constants.slidePower);
		} else {
			slideMain.set(0);
			slideFollow.set(0);
		}
		SmartDashboard.putBoolean("DB/Led 3", bottomLevel.get());
	}
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}