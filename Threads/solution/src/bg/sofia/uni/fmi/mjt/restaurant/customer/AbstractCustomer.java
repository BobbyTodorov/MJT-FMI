package bg.sofia.uni.fmi.mjt.restaurant.customer;

import bg.sofia.uni.fmi.mjt.restaurant.Meal;
import bg.sofia.uni.fmi.mjt.restaurant.Order;
import bg.sofia.uni.fmi.mjt.restaurant.Restaurant;

public abstract class AbstractCustomer extends Thread {

    protected Restaurant restaurant;

    public AbstractCustomer(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("restaurant must not be null");
        }

        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        restaurant.submitOrder(new Order(Meal.chooseFromMenu(), this));
    }

    public abstract boolean hasVipCard();

}