package test;

import datastructure.customer.Customer;
import datastructure.fullfillOrdersList.FullfillOrdersList;
import datastructure.order.Order;
import metricAnalysis.MetricAnalysis;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataAnalysisTest {
    private static List<Order> orders;

    @BeforeAll
    public static void setUp(){
        orders = FullfillOrdersList.createOrders();
    }

    @Test
    public void testUniqueCities(){
        List<String> uniqueCities = MetricAnalysis.getUniqueCities(orders);
        assertNotNull(uniqueCities);
        assertTrue(uniqueCities.contains("New York"));
        assertTrue(uniqueCities.contains("Los Angeles"));
        assertEquals(4, uniqueCities.size());
    }

    @Test
    public void testShowTotalIncome(){
        double income = MetricAnalysis.showTotalIncome(orders);
        assertTrue(income > 0);
        // Laptop(1200) + Book(60) + Phone(1600) + Sofa(700) + Phone(1600) + Laptop(1200) + ToyCar(40) + Novel(15) +
        // Jeans(50) + T-shirt(60) + ToyCar(40) + Book(60)+ T-shirt(60) + ToyCar(40) + Book(60) + T-shirt(60) + ToyCar(40)
        double expected = 1200 + 60 + 1600 + 700 + 1600 + 1200 + 40 + 15 + 50 + 60 + 40 + 60 + 60 + 40 + 60 ;
        assertEquals(expected,income);
    }

    @Test
    public void testShowMostPopularProduct(){
        String product = MetricAnalysis.showMostPopularProduct(orders);
        assertNotNull(product);
        assertEquals("Toy Car", product);
    }

    @Test
    public void testDeliverOrdersCheck(){
        double avgCheck = MetricAnalysis.deliveredOrdersCheck(orders);
        assertTrue(avgCheck > 0);
        assertEquals(969.2857, avgCheck, 0.001);
    }

    @Test
    public void testGetCustomerWIthFiveMoreOrders(){
        List<Customer> customers = MetricAnalysis.getCustomersWithFiveMoreOrders(orders);
        assertEquals(1, customers.size());
        assertEquals("Eve", customers.getFirst().getName());
    }
}
