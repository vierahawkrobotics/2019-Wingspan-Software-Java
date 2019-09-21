/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package frc.robot;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
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
  //Declares joystick variables
  private Joystick joystick0=new Joystick(0);
  private Joystick joystick1=new Joystick(1);
  //Declares cargo motors
  VictorSPX cargoRoller=new VictorSPX(2);
  //Declares elevator motors
  VictorSPX Elevator1=new VictorSPX(3);
  VictorSPX Elevator2=new VictorSPX(4);
  //Declares drive motors
  Talon leftMotor=new Talon(0);
  Talon leftFollower=new Talon(1);
  Talon rightMotor=new Talon(2);
  Talon rightFollower=new Talon(3);
  //Declares slide drive motors
  Talon slideMain=new Talon(4);
  Talon slideFollow=new Talon(5);
  //Declares controller groups
  SpeedControllerGroup leftSide;
  SpeedControllerGroup rightSide;
  //Creates drive variable
  DifferentialDrive drive1;
  //Declares the elevator limit switch
  DigitalInput bottomLevel=new DigitalInput(6);
  //Declares power variables
  double slowDrivePower=.4;
  double turboPower=1/slowDrivePower;
  double elevatorPower=.49;
  double elevatorSlowPower=.5;
  double slidePower=.6;
  double cargoArmPower=.4;
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    leftSide=new SpeedControllerGroup(leftMotor,leftFollower);
    rightSide=new SpeedControllerGroup(rightMotor,rightFollower);
    drive1=new DifferentialDrive(leftSide, rightSide);
  }
  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }
  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
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
    //Sets the speed at which the robot is turning from both turning the joystick and the sensitivity slider
    double rotateSpeed=joystick0.getRawAxis(2)*(((joystick0.getRawAxis(3)-1)*-.125)+.5);
    //Sets the forward speed of the robot
    double forwardSpeed=joystick0.getRawAxis(1)*slowDrivePower*-1;
    //If a button is pressed it allows the robot to move faster
    if(joystick0.getRawButton(1)==true){
      forwardSpeed=forwardSpeed*turboPower;
    }
    //Sets the robot to drive at the given speeds
    drive1.arcadeDrive(forwardSpeed,rotateSpeed);
    //Sets the elevator speed
    if(joystick1.getRawAxis(1)>0||(-joystick1.getRawAxis(0)<1&&bottomLevel.get()==true)){
      if(joystick1.getRawButton(8)==true){
        Elevator1.set(ControlMode.PercentOutput, joystick1.getRawAxis(1)*elevatorPower*elevatorSlowPower);
        Elevator2.set(ControlMode.PercentOutput, joystick1.getRawAxis(1)*elevatorPower*elevatorSlowPower);
      }
      else{
        Elevator1.set(ControlMode.PercentOutput, joystick1.getRawAxis(1)*elevatorPower);
        Elevator2.set(ControlMode.PercentOutput, joystick1.getRawAxis(1)*elevatorPower);
      }
    }
    //Controls slide drive
    if(joystick0.getRawButton(11)==true) {
      slideMain.set(slidePower);
      slideFollow.set(slidePower);
    }
    else if(joystick0.getRawButton(12)==true) {
      slideMain.set(-slidePower);
      slideFollow.set(-slidePower);
    }
    else{
      slideMain.set(0);
      slideFollow.set(0); 
    }
    SmartDashboard.putBoolean("DB/Led 3",bottomLevel.get());
    //Controls cargo roller
    if(joystick1.getRawButton(1)==true){
      cargoRoller.set(ControlMode.PercentOutput, 1);
    }
  }
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}