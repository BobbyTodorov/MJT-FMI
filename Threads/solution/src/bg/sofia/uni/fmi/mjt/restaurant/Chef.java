package bg.sofia.uni.fmi.mjt.restaurant;

public class Chef extends Thread {

    private final int id;
    private final Restaurant restaurant;
    private int totalCookedMeals;

    public Chef(int id, Restaurant restaurant) {
        if (id < 0) {
            throw new IllegalArgumentException("id must be non-negative int");
        }
        if (restaurant == null) {
            throw new IllegalArgumentException("restaurant must not be null");
        }

        this.id = id;
        this.restaurant = restaurant;
    }

    public int getChefId() {
        return id;
    }

    @Override
    public void run() {
        cookMeal();
    }

    private void cookMeal() {
        Order order;

        while ((order = restaurant.nextOrder()) != null) {
            try {
                Thread.sleep(order.meal().getCookingTime());
                totalCookedMeals++;
            } catch (InterruptedException e) {
                System.err.print("Unexpected exception was thrown: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.printf("Chef %d cooked %s meals.%n", id, totalCookedMeals);
    }

    /**
     * Returns the total number of meals that this chef has cooked.
     **/
    public int getTotalCookedMeals() {
        return totalCookedMeals;
    }
}
