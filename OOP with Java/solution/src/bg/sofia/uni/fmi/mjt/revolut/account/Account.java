package bg.sofia.uni.fmi.mjt.revolut.account;

public abstract class Account {

    private double amount;
    private String IBAN;

    public Account(String IBAN) {
        this(IBAN, 0);
    }

    public Account(String IBAN, double amount) {
        this.IBAN = IBAN;
        this.amount = amount;
    }

    public abstract String getCurrency();

    public double getAmount() {
        return amount;
    }

    public boolean PayAmount(double amountToPay){
        if (amount - amountToPay < 0)
            return false;
        amount -= amountToPay;
        return true;
    }

    public void GainAmount(double amountToGain){
        this.amount += amountToGain;
    }

    public String GetIBAN(){
        return IBAN;
    }

}
