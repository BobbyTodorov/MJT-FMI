package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;

public class VirtualPermanentCard implements Card {

    private final static String CARD_TYPE_STRING = "VIRTUAL ONE TIME";
    private final static String INVALID_PHYSICAL_CARD_STRING = "Invalid Virtual Permanent Card Number!";
    private final static String INVALID_PHYSICAL_CARD_PIN_STRING = "Invalid Virtual Permanent Card PIn!";

    private int pin;
    private String number;
    private LocalDate expirationDate;
    private boolean blocked = false;
    private int attempts = 3;

    public VirtualPermanentCard(String number, int pin, LocalDate expirationDate)
    {
        if(number.length() != 16){
            System.err.println(INVALID_PHYSICAL_CARD_STRING);
            return;
        }
        if(pin < 1000 || pin > 9999){
            System.err.println(INVALID_PHYSICAL_CARD_PIN_STRING);
            return;
        }

        this.pin = pin;
        this.number = number;
        this.expirationDate = expirationDate;
    }
    @Override
    public String getType() {
        return CARD_TYPE_STRING;
    }

    @Override
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public boolean checkPin(int pin) {
        if(this.pin != pin) {
            attempts--;
            if(attempts == 0)
                block();
            return false;
        }
        attempts = 3;
        return true;
    }

    @Override
    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public void block() {
        blocked = true;
    }
}
