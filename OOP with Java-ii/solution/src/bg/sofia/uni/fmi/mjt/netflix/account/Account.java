package bg.sofia.uni.fmi.mjt.netflix.account;

import java.time.LocalDateTime;
import java.time.Period;

public record Account(String username, LocalDateTime birthdayDate) {
    public int getAge(){
        return Math.abs(Period.between(birthdayDate.toLocalDate(), LocalDateTime.now().toLocalDate()).getYears());
    }
}
