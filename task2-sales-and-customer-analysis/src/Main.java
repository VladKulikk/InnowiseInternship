import metricAnalysis.MetricAnalysis;

public void main(){
    System.out.println(STR."Unique cities: \{MetricAnalysis.getUniqueCities()}");
    System.out.println(STR."Total income: \{MetricAnalysis.showTotalIncome()}");
    System.out.println(STR."Most popular product: \{MetricAnalysis.showMostPopularProduct()}");
    System.out.println(STR."Average check of succesfully delivered orders: \{MetricAnalysis.deliveredOrdersCheck()}");
    System.out.print(STR."Customers with 5+ orders: ");
    MetricAnalysis.showCustomers();
}
