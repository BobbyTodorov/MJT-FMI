package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;
import java.time.LocalDateTime;

public final class SmartLamp extends SmartCityDevice {

    private static final DeviceType TYPE = DeviceType.LAMP;
    private static int numberOfInstances = 0;

    public SmartLamp(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime);

        generateID();
        numberOfInstances++;
    }

    @Override
    protected void generateID() {
        ID = TYPE.getShortName() + "-" + getName() + "-" + getNumberOfInstances();
    }

    @Override
    public DeviceType getType() {
        return TYPE;
    }

    public static int getNumberOfInstances() {
        return numberOfInstances;
    }
}
