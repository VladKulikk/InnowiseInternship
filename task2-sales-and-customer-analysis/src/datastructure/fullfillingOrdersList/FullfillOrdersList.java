package datastructure.fullfillingOrdersList;

import datastructure.Category;
import datastructure.Customer;
import datastructure.Order;
import datastructure.OrderItem;
import datastructure.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class FullfillOrdersList {
    public static List<Order> createOrders() {
        // Customers
        Customer c1 = new Customer("C1", "Alice", "alice@mail.com",
                LocalDateTime.now().minusMonths(6), 25, "New York");

        Customer c2 = new Customer("C2", "Bob", "bob@mail.com",
                LocalDateTime.now().minusYears(1), 32, "Los Angeles");

        Customer c3 = new Customer("C3", "Charlie", "charlie@mail.com",
                LocalDateTime.now().minusYears(2), 40, "Chicago");

        Customer c4 = new Customer("C4", "Diana", "diana@mail.com",
                LocalDateTime.now().minusDays(200), 28, "San Francisco");

        Customer c5 = new Customer("C5", "Eve", "eve@mail.com",
                LocalDateTime.now().minusYears(3), 35, "New York");

        // Order Items
        OrderItem laptop = new OrderItem("Laptop", 1, 1200.0, Category.ELECTRONICS);
        OrderItem phone = new OrderItem("Smartphone", 2, 800.0, Category.ELECTRONICS);
        OrderItem tshirt = new OrderItem("T-Shirt", 3, 20.0, Category.CLOTHING);
        OrderItem jeans = new OrderItem("Jeans", 1, 50.0, Category.CLOTHING);
        OrderItem book = new OrderItem("Java Book", 2, 30.0, Category.BOOKS);
        OrderItem novel = new OrderItem("Novel", 1, 15.0, Category.BOOKS);
        OrderItem sofa = new OrderItem("Sofa", 1, 700.0, Category.HOME);
        OrderItem cream = new OrderItem("Face Cream", 2, 25.0, Category.BEAUTY);
        OrderItem toyCar = new OrderItem("Toy Car", 4, 10.0, Category.TOYS);

        // Orders
        Order o1 = new Order("O1", LocalDateTime.now().minusDays(10),
                c1, List.of(laptop, book), OrderStatus.DELIVERED);

        Order o2 = new Order("O2", LocalDateTime.now().minusDays(5),
                c1, List.of(tshirt, jeans), OrderStatus.PROCESSING);

        Order o3 = new Order("O3", LocalDateTime.now().minusDays(20),
                c2, List.of(phone), OrderStatus.DELIVERED);

        Order o4 = new Order("O4", LocalDateTime.now().minusDays(15),
                c2, List.of(novel, toyCar), OrderStatus.CANCELLED);

        Order o5 = new Order("O5", LocalDateTime.now().minusDays(7),
                c3, List.of(sofa), OrderStatus.DELIVERED);

        Order o6 = new Order("O6", LocalDateTime.now().minusDays(1),
                c4, List.of(cream, tshirt), OrderStatus.NEW);

        Order o7 = new Order("O7", LocalDateTime.now().minusDays(3),
                c5, List.of(phone, laptop, toyCar), OrderStatus.DELIVERED);

        Order o8 = new Order("O8", LocalDateTime.now().minusDays(2),
                c5, List.of(book, cream), OrderStatus.SHIPPED);

        Order o9 = new Order("O9", LocalDateTime.now().minusDays(30),
                c5, List.of(novel, jeans), OrderStatus.DELIVERED);

        Order o10 = new Order("O10", LocalDateTime.now().minusDays(12),
                c5, List.of(tshirt, toyCar, book), OrderStatus.DELIVERED);

        Order o11 = new Order("O11", LocalDateTime.now().minusDays(100),
                c5, List.of(tshirt, toyCar, book), OrderStatus.DELIVERED);

        Order o12 = new Order("O12", LocalDateTime.now().minusDays(50),
                c5, List.of(tshirt, toyCar), OrderStatus.NEW);

        return List.of(o1, o2, o3, o4, o5, o6, o7, o8, o9, o10, o11, o12);
    }
}
