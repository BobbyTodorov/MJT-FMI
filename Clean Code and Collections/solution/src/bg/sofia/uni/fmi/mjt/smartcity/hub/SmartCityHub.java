package bg.sofia.uni.fmi.mjt.smartcity.hub;

import bg.sofia.uni.fmi.mjt.smartcity.device.SmartCityDevice;
import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class SmartCityHub {

    Map<String, SmartDevice> smartDevices;
    int numberOfSmartCameras, numberOfSmartLamps, numberOfSmartTrafficLights;

    public SmartCityHub() {
        smartDevices = new LinkedHashMap<>();
    }

    /**
     * Adds a @device to the SmartCityHub.
     *
     * @throws IllegalArgumentException         in case @device is null.
     * @throws DeviceAlreadyRegisteredException in case the @device is already registered.
     */
    public void register(SmartDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException("Smart device to be registered must not be null.");
        }
        if (smartDevices.containsKey(device.getId())) {
            throw new DeviceAlreadyRegisteredException("Smart device " + device.getId() + " was already registered.");
        }

        try {
            smartDevices.put(device.getId(), device);
            switch (device.getType()) {
                case CAMERA -> numberOfSmartCameras++;
                case LAMP -> numberOfSmartLamps++;
                case TRAFFIC_LIGHT -> numberOfSmartTrafficLights++;
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Cannot register device "
                + device.getId() + " because addition to smart devices collection failed.");
        }
    }

    /**
     * Removes the @device from the SmartCityHub.
     *
     * @throws IllegalArgumentException in case null is passed.
     * @throws DeviceNotFoundException  in case the @device is not found.
     */
    public void unregister(SmartDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException("Smart device to be unregistered must not be null.");
        }
        if (!smartDevices.containsKey(device.getId())) {
            throw new DeviceNotFoundException("Smart device " + device.getId() + " is not registered.");
        }

        try {
            smartDevices.remove(device.getId());
            switch (device.getType()) {
                case CAMERA -> numberOfSmartCameras--;
                case LAMP -> numberOfSmartLamps--;
                case TRAFFIC_LIGHT -> numberOfSmartTrafficLights--;
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Cannot unregister device "
                + device.getId() + " because removing from smart devices collection failed.");
        }
    }

    /**
     * Returns a SmartDevice with an ID @id.
     *
     * @throws IllegalArgumentException in case @id is null.
     * @throws DeviceNotFoundException  in case device with ID @id is not found.
     */
    public SmartDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("Smart device ID must not be null.");
        }
        if (!smartDevices.containsKey(id)) {
            throw new DeviceNotFoundException("Smart device " + id + " is not registered.");
        }

        try {
            return smartDevices.get(id);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Cannot return device "
                + id + " because getting from smart devices collection failed.");
        }
    }

    /**
     * Returns the total number of devices with type @type registered in SmartCityHub.
     *
     * @throws IllegalArgumentException in case @type is null.
     */
    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null) {
            throw new IllegalArgumentException("Smart device type must not be null.");
        }

        try {
            switch (type) {
                case CAMERA -> { return numberOfSmartCameras; }
                case LAMP -> { return numberOfSmartLamps; }
                case TRAFFIC_LIGHT -> { return numberOfSmartTrafficLights; }
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Could not get " + type.toString() + " device quantity.");
        }

        return -1;
    }

    /**
     * Returns a collection of IDs of the top @n devices which consumed
     * the most power from the time of their installation until now.
     * <p>
     * The total power consumption of a device is calculated by the hours elapsed
     * between the two LocalDateTime-s: the installation time and the current time (now)
     * multiplied by the stated nominal hourly power consumption of the device.
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Argument must be positive number.");
        }

        try {
            Map<Double, String> workingTreeMap = new TreeMap<>();
            int i = 0;
            for (Map.Entry<String, SmartDevice> device : smartDevices.entrySet()) {
                if (i == n) {
                    break;
                }
                i++;
                workingTreeMap.put(((SmartCityDevice) (device.getValue())).getConsumptedPower(), device.getKey());
            }
            return workingTreeMap.values();
        } catch (Exception e) {
            throw new UnsupportedOperationException("Cannot get TopNDevicesByPowerConsumption due to: " + e.getMessage());
        }
    }

    /**
     * Returns a collection of the first @n registered devices, i.e the first @n that were added
     * in the SmartCityHub (registration != installation).
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<SmartDevice> getFirstNDevicesByRegistration(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Argument must be positive number.");
        }

        try {
            Map<LocalDateTime, SmartDevice> workingTreeMap = new TreeMap<>();
            int i = 0;
            for (Map.Entry<String, SmartDevice> device : smartDevices.entrySet()) {
                if (i == n) {
                    break;
                }
                i++;
                workingTreeMap.put(device.getValue().getInstallationDateTime(), device.getValue());
            }

            return workingTreeMap.values();
        } catch (Exception e) {
            throw new UnsupportedOperationException("Cannot get FirstNDevicesByRegistration due to: " + e.getMessage());
        }
    }
}