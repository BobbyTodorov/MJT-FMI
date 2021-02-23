package bg.sofia.uni.fmi.mjt.restaurant;

import bg.sofia.uni.fmi.mjt.restaurant.customer.AbstractCustomer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.Customer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.VipCustomer;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MJTDiningPlaceTest {

    private MJTDiningPlace dp;

    @Test(expected = IllegalArgumentException.class)
    public void testConstructingWithNegativeNumberOfChefs() {
        dp = new MJTDiningPlace(-3);
    }

    @Test
    public void testGetChefsCountCorrectness() {
        int expected = 5;
        dp = new MJTDiningPlace(expected);

        assertEquals("getChefs count must return correct value", expected, dp.getChefs().length);
    }

    @Test
    public void testOrdersCountWithOneOrderAndOneChef() throws InterruptedException {
        dp = new MJTDiningPlace(1);
        AbstractCustomer customer = new Customer(dp);
        customer.start();
        customer.join();

        assertEquals("ordersCount with one order and one chef must be 1", 1, dp.getOrdersCount());
    }

    @Test
    public void testOrdersCountWithOneOrderAndManyChefs() throws InterruptedException {
        dp = new MJTDiningPlace(5);
        AbstractCustomer customer = new Customer(dp);
        customer.start();
        customer.join();

        assertEquals("ordersCount with one order and many chefs must be 1", 1, dp.getOrdersCount());
    }

    @Test
    public void testOrdersCountWithNoOrders() {
        dp = new MJTDiningPlace(3);

        assertEquals("ordersCount with no orders must be 0", 0, dp.getOrdersCount());
    }

    @Test
    public void testCloseNoTakingOrdersAfterClosure() {
        dp = new MJTDiningPlace(1);
        AbstractCustomer customer = new Customer(dp);
        dp.close();
        customer.start();

        assertEquals("test ordering after closure - cooked meals must be less than actual orders",
                0, dp.getOrdersCount());
    }

    @Test
    public void testChefCookedMealsCountVsSubmittedOrders() throws InterruptedException {
        dp = new MJTDiningPlace(500);
        final int NUMBER_OF_CUSTOMERS_PER_TYPE = 1000;

        AbstractCustomer[] customers = new Customer[NUMBER_OF_CUSTOMERS_PER_TYPE];
        AbstractCustomer[] vipCustomers = new VipCustomer[NUMBER_OF_CUSTOMERS_PER_TYPE];
        for (int i = 0; i < NUMBER_OF_CUSTOMERS_PER_TYPE; ++i) {
            customers[i] = new Customer(dp);
            vipCustomers[i] = new VipCustomer(dp);
        }

        for (int i = 0; i < NUMBER_OF_CUSTOMERS_PER_TYPE; ++i) {
            customers[i].start();
            vipCustomers[i].start();
        }

        Thread.sleep(3000);
        dp.close();

        assertEquals("the number of cooked meals is not equal to the number of submitted orders",
                dp.getOrdersCount(),
                Arrays.stream(dp.getChefs()).map(Chef::getTotalCookedMeals).mapToInt(Integer::intValue).sum());
    }

}
