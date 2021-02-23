package dto;

import com.google.gson.annotations.SerializedName;

public class WeatherCondition {

    @SerializedName("description")
    private final String description;

    public WeatherCondition(String description) {
        if (description == null) {
            throw new IllegalArgumentException("description must not be null");
        }
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WeatherCondition)) {
            return false;
        }
        WeatherCondition other = (WeatherCondition) o;
        return description.equals(other.description);
    }

    @Override
    public String toString() {
        return "Description: " + description;
    }
}
