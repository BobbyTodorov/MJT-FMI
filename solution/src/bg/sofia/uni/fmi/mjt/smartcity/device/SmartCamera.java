package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;
import java.time.LocalDateTime;

public final class SmartCamera extends SmartCityDevice{

    private static final DeviceType TYPE = DeviceType.CAMERA;
    private static int numberOfInstances = 0;

    public SmartCamera(String name, double powerConsumption, LocalDateTime installationDateTime) {
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
