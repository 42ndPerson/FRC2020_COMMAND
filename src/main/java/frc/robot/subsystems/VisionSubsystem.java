package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VisionSubsystem extends SubsystemBase {

    private NetworkTableInstance inst;
    private NetworkTable mlTable;
    private NetworkTableEntry powerCellAngleEntry, powerCellExistsEntry, powerCellPosEntry,
                        powerCellXEntry, powerCellYEntry, 
                        tapeAngleEntry, tapeDistEntry, tapeFoundEntry;
    private double powerCellAngle, tapeAngle, tapeDist, powerCellX, powerCellY;
    private double[] powerCellPos;
    private boolean powerCellExists, camMode, tapeFound;

    private double width = 320, height = 240;

    private Solenoid turretLED = new Solenoid(3);

    private Solenoid intakeLED = new Solenoid(0);

    private Solenoid robotLED = new Solenoid(4);

    public VisionSubsystem() {
        inst = NetworkTableInstance.getDefault();

        mlTable = this.inst.getTable("ML");

        powerCellAngleEntry = this.mlTable.getEntry("power_cell_angle");
        powerCellExistsEntry = this.mlTable.getEntry("power_cell_exists");
        powerCellPosEntry = this.mlTable.getEntry("power_cell_pos");
        tapeAngleEntry = this.mlTable.getEntry("tape_angle");
        tapeDistEntry = this.mlTable.getEntry("tape_dist");
        tapeFoundEntry = this.mlTable.getEntry("tape_found");
        powerCellXEntry = this.mlTable.getEntry("power_cell_x");
        powerCellYEntry = this.mlTable.getEntry("power_cell_y");

        this.inst.startClientTeam(3006);

        this.camMode = false;

    }

    @Override
    public void periodic() {
        tapeFound = this.tapeFoundEntry.getBoolean(false);
        if (camMode) {
            tapeAngle = this.tapeAngleEntry.getDouble(0);
            tapeDist = this.tapeDistEntry.getDouble(0);
        } else {
            powerCellAngle = this.powerCellAngleEntry.getDouble(0);
            powerCellExists = this.powerCellExistsEntry.getBoolean(false);
            powerCellPos = this.powerCellPosEntry.getDoubleArray(new double[] {this.width/2, this.height/2});
            powerCellX = this.powerCellXEntry.getDouble(0);
            powerCellY = this.powerCellYEntry.getDouble(0);
        }

    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setCamMode(boolean mode) {
        if(this.camMode == mode) {
            return;
        } else {
            this.camMode = mode;
            this.inst.getTable("SmartDashboard").getEntry("cam").setBoolean(mode);
        }
    }

    public double getPowerCellAngle() {
        return this.powerCellAngle;
    }

    public boolean getPowerCellExists() {
        return this.powerCellExists;
    }

    public double[] getPowerCellPos() {
        return this.powerCellPos;
    }

    public double getTapeAngle() {
        return this.tapeAngle;
    }

    public double getTapeDist() {
        return this.tapeDist;
    }

    public boolean getTapeFound() {
        return this.tapeFound;
    }
    
    public double getTargetAngle() {
        if (this.camMode) {
            return this.tapeAngle;
        } else {
            return this.powerCellAngle;
        }
    }

    public double getAngleToTurn() {
       // return drive.getHeading() + getTargetAngle();
       return getTargetAngle();
    }

    public double getPowerCellX() {
        return this.powerCellX;
    }

    public double getPowerCellY() {
        return this.powerCellY;
    }

    public void enableIntakeSideLED(boolean enabled) {
        this.intakeLED.set(enabled);
    }
    public void enableTurretLED(boolean enabled) {
        this.turretLED.set(enabled);
    }

    public void enableAllLEDs(boolean enabled) {
        this.turretLED.set(enabled);
        this.intakeLED.set(enabled);
        this.robotLED.set(enabled);
    }
}