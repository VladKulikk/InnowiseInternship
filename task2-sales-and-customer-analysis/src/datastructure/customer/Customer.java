package datastructure.customer;

import java.time.LocalDateTime;

public class Customer {
    private String customerId;
    private String name;
    private String email;
    private LocalDateTime registeredAt;
    private int age;
    private String city;

    public Customer(String customerId, String name, String email, LocalDateTime registeredAt, int age, String city) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.registeredAt = registeredAt;
        this.age = age;
        this.city = city;
    }


    public String getCity() {
        return city;
    }

    public int getAge() {
        return age;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getCustomerId() {
        return customerId;
    }
}
