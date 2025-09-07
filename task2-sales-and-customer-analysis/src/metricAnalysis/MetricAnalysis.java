package metricAnalysis;

import datastructure.Customer;
import datastructure.Order;
import datastructure.OrderItem;
import datastructure.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MetricAnalysis {


    public static List<String> getUniqueCities(List<Order> orders) {
        return orders.stream()
            .map(order -> Optional.ofNullable(order)
                    .map(Order::getCustomer)
                    .map(Customer::getCity)
                    .orElse(null))
            .distinct()
            .toList();
    }

    public static double showTotalIncome(List<Order> orders){
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> order.getItems().stream())
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public static String showMostPopularProduct(List<Order> orders){
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(OrderItem::getProductName, Collectors.summingInt(OrderItem::getQuantity)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No products found");
    }

    public static double deliveredOrdersCheck(List<Order> orders){
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(order -> order.getItems().stream()
                        .mapToDouble(item -> item.getQuantity()*item.getPrice()).sum())
                .average().orElse(0);
    }

    public static List<Customer> getCustomersWithFiveMoreOrders(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(Order::getCustomer, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= 5)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static void showCustomers(List<Order> orders){
        for(Customer customer : getCustomersWithFiveMoreOrders(orders)){
            System.out.println(customer.getName());
        }
    }
}
