package com.example.demo;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class Category {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private long id;

@NotNull
private String carCategory;

@OneToMany(mappedBy = "category", orphanRemoval = true,
        cascade = CascadeType.REMOVE)
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<Car> cars;

    public Category() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCarCategory() {
        return carCategory;
    }

    public void setCarCategory(String carCategory) {
        this.carCategory = carCategory;
    }

    public Set<Car> getCars() {
        return cars;
    }

    public void setCars(Set<Car> cars) {
        this.cars = cars;
    }
}
