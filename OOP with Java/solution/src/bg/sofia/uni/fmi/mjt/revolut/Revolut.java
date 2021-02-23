package bg.sofia.uni.fmi.mjt.revolut;

import bg.sofia.uni.fmi.mjt.revolut.account.Account;
import bg.sofia.uni.fmi.mjt.revolut.account.BGNAccount;
import bg.sofia.uni.fmi.mjt.revolut.account.EURAccount;

import bg.sofia.uni.fmi.mjt.revolut.card.Card;
import bg.sofia.uni.fmi.mjt.revolut.card.PhysicalCard;
import bg.sofia.uni.fmi.mjt.revolut.card.VirtualOneTimeCard;
import bg.sofia.uni.fmi.mjt.revolut.card.VirtualPermanentCard;

import java.time.LocalDate;

public class Revolut implements RevolutAPI {

    private final Account[] accounts;
    private final Card[] cards;
    private final double rateEURtoBGN = 1.95583;

    public Revolut(Account[] accounts, Card[] cards){
        this.accounts = accounts;
        this.cards = cards;
    }


    private boolean IsCardAvailable(Card card){
        if(card == null)
            return false;

        for (Card cardIt : cards) {
            if(cardIt.equals(card)
                && cardIt.getExpirationDate() != null
                && cardIt.getExpirationDate().isAfter(LocalDate.now()))
                return true;
        }
        return false;
    }

    private Account GetSuitableAccount(double amount, String currency){
        for (Account acc : accounts) {
            if(acc.getAmount() >= amount && acc.getCurrency().equals(currency))
                return acc;
        }
        return null;
    }


    public boolean pay(Card card, int pin, double amount, String currency) {
        if(!IsCardAvailable(card) || !card.getType().equals("PHYSICAL") || card.isBlocked() || !card.checkPin(pin))
            return false;
        Account accToChange = GetSuitableAccount(amount,currency);
        if(accToChange == null)
            return false;
        return accToChange.PayAmount(amount);
    }


    public boolean payOnline(Card card, int pin, double amount, String currency, String shopURL) {
        if(!IsCardAvailable(card) || card.isBlocked() || !card.checkPin(pin) || shopURL.contains(".biz"))
            return false;
        Account accToChange = GetSuitableAccount(amount,currency);
        if(accToChange == null)
            return false;
        if(card.getType().equals("VIRTUAL ONE TIME"))
            card.block();
        return accToChange.PayAmount(amount);
    }


    public boolean addMoney(Account account, double amount) {
        for (Account accIt : accounts) {
            if(accIt.equals(account)) {
                accIt.GainAmount(amount);
                return true;
            }
        }
        return false;
    }


    public boolean transferMoney(Account from, Account to, double amount) {
        int existingAccounts = 0;
        for (Account accIt: accounts){
            if(accIt.equals(from)){
                existingAccounts++;
            }
            if(accIt.equals(to)){
                existingAccounts++;
            }
        }
        if(existingAccounts != 2 || amount <= 0 || from.GetIBAN().equals(to.GetIBAN()) || !from.PayAmount(amount))
            return false;



        switch (from.getCurrency() + "->" + to.getCurrency()){
            case "BGN->BGN", "EUR->EUR" -> to.GainAmount(amount);
            case "BGN->EUR" -> to.GainAmount(amount/rateEURtoBGN);
            case "EUR->BGN" -> to.GainAmount(amount*rateEURtoBGN);
        }
        return true;
    }


    public double getTotalAmount() {
        double result = 0;
        for (Account acc : accounts) {
            switch (acc.getCurrency()) {
                case "BGN" -> result += acc.getAmount();
                case "EUR" -> result += acc.getAmount()*rateEURtoBGN;
            }
        }
        return result;
    }
    public static void main(String[] args) {
        LocalDate testDate = LocalDate.of(2021,10,28);
        //LocalDate errDate = LocalDate.of(2020,10,20);

        Card c1 = new PhysicalCard("1234567890123456",1234,null);
        Card c2 = new VirtualPermanentCard("0987654321123456",4321,testDate);
        Card c3 = new VirtualOneTimeCard("0987654321654321",5412,testDate);
        Account a1 = new BGNAccount("123",0.0);
        Account a2 = new EURAccount("321",50.0);
        Account a3 = new EURAccount("234",100.0);
        Account a4 = new BGNAccount("432",45.5);

        Revolut r = new Revolut( new Account[] {a1,a2,a3,a4}, new Card[]{c1,c2,c3});
        System.out.println(r.getTotalAmount());
        System.out.println(r.pay(c1,1234,45,"BGN"));
    }
}



