import datastructure.fullfillingOrdersList.FullfillOrdersList;
import datastructure.Order;
import metricAnalysis.MetricAnalysis;

import java.util.List;

public void main(){
    List<Order> orders = FullfillOrdersList.createOrders();

    System.out.println(STR."Unique cities: \{MetricAnalysis.getUniqueCities(orders)}");
    System.out.println(STR."Total income: \{MetricAnalysis.showTotalIncome(orders)}");
    System.out.println(STR."Most popular product: \{MetricAnalysis.showMostPopularProduct(orders)}");
    System.out.println(STR."Average check of succesfully delivered orders: \{MetricAnalysis.deliveredOrdersCheck(orders)}");
    System.out.print(STR."Customers with 5+ orders: ");
    MetricAnalysis.showCustomers(orders);
}
