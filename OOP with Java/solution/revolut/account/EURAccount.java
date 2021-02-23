package bg.sofia.uni.fmi.mjt.revolut.account;

public class EURAccount extends Account {

    private final static String CURRENCY_STRING = "EUR";

    public EURAccount(String IBAN) {
        super(IBAN);
    }

    public EURAccount(String IBAN, double amount) {
        super(IBAN, amount);
    }

    @Override
    public String getCurrency() { return CURRENCY_STRING; }
}
