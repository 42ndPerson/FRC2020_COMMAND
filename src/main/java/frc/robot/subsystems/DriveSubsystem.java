//package edu.wpi.first.wpilibj.examples.ramsetecommand.subsystems;
package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.SPI;

import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {
  // The motors on the left side of the drive.

  WPI_TalonFX frontRight = new WPI_TalonFX(DriveConstants.kRightMotor1Port);
  WPI_TalonFX backRight = new WPI_TalonFX(DriveConstants.kRightMotor2Port);
  WPI_TalonFX frontLeft = new WPI_TalonFX(DriveConstants.kLeftMotor1Port);
  WPI_TalonFX backLeft = new WPI_TalonFX(DriveConstants.kLeftMotor2Port);
/*
  private final SpeedControllerGroup m_leftMotors =
      new SpeedControllerGroup(frontLeft, backLeft);

  // The motors on the right side of the drive.
  private final SpeedControllerGroup m_rightMotors =
      new SpeedControllerGroup(frontRight, backRight);
*/
  // The robot's drive
  private final DifferentialDrive m_drive;
  
  // The gyro sensor
  private final AHRS m_gyro = new AHRS(SPI.Port.kMXP);

  // Odometry class for tracking robot pose
  private final DifferentialDriveOdometry m_odometry;

  private double maxSpeed = 0.8;

  public DriveSubsystem() {
    // Sets the distance per pulse for the encoders
    this.frontLeft.configFactoryDefault();
    this.frontRight.configFactoryDefault();
    this.backLeft.configFactoryDefault();
    this.backRight.configFactoryDefault();

    this.backLeft.follow(frontLeft);
    this.backRight.follow(frontRight);

    this.frontRight.setInverted(true);
    this.frontLeft.setInverted(false);

    this.backRight.setInverted(InvertType.FollowMaster);
    this.backLeft.setInverted(InvertType.FollowMaster);

    this.frontRight.setNeutralMode(NeutralMode.Brake);
    this.frontLeft.setNeutralMode(NeutralMode.Brake);
    this.backRight.setNeutralMode(NeutralMode.Brake);
    this.backLeft.setNeutralMode(NeutralMode.Brake);

    this.m_drive = new DifferentialDrive(frontLeft, frontRight);
    this.m_drive.setRightSideInverted(false);

    resetEncoders();
    this.m_odometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(getHeading()));
  }

  @Override
  public void periodic() {
    // Update the odometry in the periodic block
    this.m_odometry.update(Rotation2d.fromDegrees(getHeading()), getLeftEncoderDistance(),
                      getRightEncoderDistance());
    SmartDashboard.putNumber("Gyro", getHeading());
    SmartDashboard.putNumber("PosX", getPose().getTranslation().getX());
    SmartDashboard.putNumber("PosY", getPose().getTranslation().getY());
    SmartDashboard.putNumber("Encoder Left", getLeftEncoderDistance());
    SmartDashboard.putNumber("Encoder Right", getRightEncoderDistance());


  }

  public Pose2d getPose() {
    return this.m_odometry.getPoseMeters();
  }

  public DifferentialDriveWheelSpeeds getWheelSpeeds() {
    return new DifferentialDriveWheelSpeeds(getLeftEncoderVelocity(), getRightEncoderVelocity());
  }

  public void resetOdometry(Pose2d pose) {
    resetEncoders();
    this.m_odometry.resetPosition(pose, Rotation2d.fromDegrees(getHeading()));
  }

  public void arcadeDrive(double fwd, double rot) {
    this.m_drive.arcadeDrive(fwd, rot);
  }

  public void tankDriveVolts(double leftVolts, double rightVolts) {
    this.frontLeft.setVoltage(leftVolts);
    this.frontRight.setVoltage(rightVolts);
    this.m_drive.feed();
  }

  public void resetEncoders() {
    this.frontLeft.getSensorCollection().setIntegratedSensorPosition(0,0);
    
    this.frontRight.getSensorCollection().setIntegratedSensorPosition(0, 0);
  }

  public double getLeftEncoderDistance() {
    double frontLeftEncoder = this.frontLeft.getSensorCollection().getIntegratedSensorPosition() * DriveConstants.kEncoderDistancePerPulse;
    return frontLeftEncoder;
    //return ((frontLeft.getSensorCollection().getQuadraturePosition() + backLeft.getSensorCollection().getQuadraturePosition())/2);
  }

  public double getRightEncoderDistance() {
    double frontRightEncoder = this.frontRight.getSensorCollection().getIntegratedSensorPosition()* DriveConstants.kEncoderDistancePerPulse;
    return frontRightEncoder * -1;
   //return ((frontRight.getSensorCollection().getQuadraturePosition() + backRight.getSensorCollection().getQuadraturePosition())/2);
  }

  public void setMaxOutput(double maxOutput) {
    this.m_drive.setMaxOutput(maxOutput);
  }

  public void zeroHeading() {
    this.m_gyro.reset();
  }

  public double getHeading() {
    return Math.IEEEremainder(m_gyro.getAngle(), 360) * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }

  public double getTurnRate() {
    return this.m_gyro.getRate() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }

  public double getLeftEncoderVelocity()
  {
    //For reversed left, call frontRight
    double frontLeftEncoder = this.frontLeft.getSensorCollection().getIntegratedSensorVelocity() * DriveConstants.kEncoderDistancePerPulse;
    return frontLeftEncoder;
     // return (frontLeft.getSensorCollection().getQuadratureVelocity() + backLeft.getSensorCollection().getQuadratureVelocity())/2.0;
  }

  public double getRightEncoderVelocity()
  {
    //For revsered right, call frontLeft
    double frontRightEncoder = this.frontRight.getSensorCollection().getIntegratedSensorVelocity() * DriveConstants.kEncoderDistancePerPulse;
    return frontRightEncoder*-1;
    // return (frontRight.getSensorCollection().getQuadratureVelocity() + backRight.getSensorCollection().getQuadratureVelocity())/2.0;
  }

  public void tankDrive(double leftPower, double rightPower)
  {
    //ax^3 + (1-a)x
    /*double leftOutput = DriveConstants.joystickGain * Math.pow(leftPower, 3) + DriveConstants.joystickGain * leftPower;
    double rightOutput = DriveConstants.joystickGain * Math.pow(rightPower, 3) + DriveConstants.joystickGain * rightPower;
    */ 
    this.m_drive.tankDrive(leftPower, rightPower);
     // System.out.println(leftPower + " " + rightPower);
  }

  public void drive(double leftPower, double rightPower) {
    tankDrive(DriveConstants.joystickGain * Math.pow(-maxSpeed * leftPower, 3) + DriveConstants.joystickGain * -maxSpeed * leftPower,
            DriveConstants.joystickGain * Math.pow(-maxSpeed * rightPower, 3) + DriveConstants.joystickGain * -maxSpeed * rightPower);
  }

  public void setMaxPower(double power) {
    maxSpeed = power;
  }
}