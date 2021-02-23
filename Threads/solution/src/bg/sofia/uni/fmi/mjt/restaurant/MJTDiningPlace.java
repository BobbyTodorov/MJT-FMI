package bg.sofia.uni.fmi.mjt.restaurant;

import bg.sofia.uni.fmi.mjt.restaurant.customer.AbstractCustomer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.Customer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.VipCustomer;

import java.util.PriorityQueue;
import java.util.Queue;

public class MJTDiningPlace implements Restaurant {

    private int ordersCount;
    private boolean closed = false;

    private Chef[] chefs;
    private Queue<Order> orders;

    public MJTDiningPlace(int numberOfChefs) {
        if (numberOfChefs <= 0) {
            throw new IllegalArgumentException("MJTDiningPlace must have chef(s).");
        }

        createChefs(numberOfChefs);

        orders = new PriorityQueue<>((o1, o2) -> {
            if (o1.customer().hasVipCard() && !o2.customer().hasVipCard()) {
                return -1;
            }

            if (!o1.customer().hasVipCard() && o2.customer().hasVipCard()) {
                return 1;
            }

            return Integer.compare(o2.meal().getCookingTime(), o1.meal().getCookingTime());
        });

        getChefsToWork();
    }

    @Override
    public void submitOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("order must not be null");
        }

        if (closed) {
            return;
        }

        synchronized (this) {
            orders.offer(order);
            this.notifyAll();
            ordersCount++;
        }

    }

    @Override
    public synchronized Order nextOrder() {
        if (closed && orders.isEmpty()) {
            return null;
        }

        if (orders.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return orders.poll();
    }

    @Override
    public int getOrdersCount() {
        return ordersCount;
    }

    @Override
    public Chef[] getChefs() {
        return chefs;
    }

    @Override
    public void close() {
        closed = true;

        synchronized (this) {
            this.notifyAll(); //if any chef is stuck waiting
        }
    }

    private void createChefs(int numberOfChefs) {
        chefs = new Chef[numberOfChefs];

        for (int i = 0; i < numberOfChefs; ++i) {
            chefs[i] = new Chef(i, this);
        }
    }

    private void getChefsToWork() {
        for (Chef chef : chefs) {
            chef.start();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        MJTDiningPlace dp = new MJTDiningPlace(10);
        AbstractCustomer[] customers = new Customer[250];
        AbstractCustomer[] vipCustomers = new VipCustomer[250];
        for (int i = 0; i < customers.length; ++i) {
            customers[i] = new Customer(dp);
            vipCustomers[i] = new VipCustomer(dp);
        }
        for (int i = 0; i < customers.length; ++i) {
            customers[i].start();
            vipCustomers[i].start();
        }
        Thread.sleep(4000);
        dp.close();

        Chef[] chefs = dp.getChefs();
        for (Chef chef : chefs) {
            System.out.println("Chef with id " + chef.getChefId() + " cooked " + chef.getTotalCookedMeals() + " meals");
        }
        System.out.println(dp.ordersCount);
    }
}
