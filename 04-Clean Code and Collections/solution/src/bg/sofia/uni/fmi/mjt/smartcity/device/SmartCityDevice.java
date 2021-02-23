package bg.sofia.uni.fmi.mjt.smartcity.device;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class SmartCityDevice implements SmartDevice {
    protected String name;
    protected double powerConsumption;
    protected LocalDateTime installationDateTime;
    protected String ID;

    SmartCityDevice(String name, double powerConsumption, LocalDateTime installationDateTime){
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
    }

    protected abstract void generateID();

    public double getConsumptedPower(){
        return Duration.between(getInstallationDateTime(), LocalDateTime.now()).toHours()*getPowerConsumption();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPowerConsumption() {
        return powerConsumption;
    }

    @Override
    public LocalDateTime getInstallationDateTime() {
        return installationDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SmartCityDevice)) return false;
        SmartCityDevice device = (SmartCityDevice) o;
        return this.getId().equals(device.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
