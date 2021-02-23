package bg.sofia.uni.fmi.mjt.revolut.account;

public class BGNAccount extends Account {

    private final static String CURRENCY_STRING = "EUR";

    public BGNAccount(String IBAN) {
        super(IBAN);
    }

    public BGNAccount(String IBAN, double amount) {
        super(IBAN, amount);
    }

    @Override
    public String getCurrency() {
        return CURRENCY_STRING;
    }
}
