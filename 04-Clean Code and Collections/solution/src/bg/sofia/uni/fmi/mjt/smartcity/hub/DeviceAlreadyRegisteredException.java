package bg.sofia.uni.fmi.mjt.smartcity.hub;

public class DeviceAlreadyRegisteredException extends Exception {
    public DeviceAlreadyRegisteredException(String msg){
        super(msg);
    }
}
