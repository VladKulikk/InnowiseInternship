package metricAnalysis;

import datastructure.customer.Customer;
import datastructure.fullfillOrdersList.FullfillOrdersList;
import datastructure.order.Order;
import datastructure.orderItem.OrderItem;
import datastructure.orderStatus.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricAnalysis {
    public static List<Order> orders = FullfillOrdersList.createOrders();

    public static List<String> getUniqueCities(){
        return orders.stream()
            .map(order -> order.getCustomer().getCity())
            .distinct()
            .toList();
    }

    public static double showTotalIncome(){
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> order.getItems().stream())
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public static String showMostPopularProduct(){
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(OrderItem::getProductName, Collectors.summingInt(OrderItem::getQuantity)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No products found");
    }

    public static double deliveredOrdersCheck(){
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(order -> order.getItems().stream()
                        .mapToDouble(item -> item.getQuantity()*item.getPrice()).sum())
                .average().orElse(0);
    }

    public static List<Customer> getCustomersWithFiveMoreOrders() {
        return orders.stream()
                .collect(Collectors.groupingBy(Order::getCustomer, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= 5)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static void showCustomers(){
        for(Customer customer : getCustomersWithFiveMoreOrders()){
            System.out.println(customer.getName());
        }
    }
}
