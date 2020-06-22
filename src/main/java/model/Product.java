package model;

import java.util.ArrayList;
import java.util.Objects;

public class Product {
    private int id;
    private String name;
    private ArrayList<String> groups;
    private int num;
    private int price;
    private String description;
    private String producer;

    public Product(int id, String name, ArrayList<String> groups, int num, int price, String description, String producer) {
        this.id = id;
        this.name = name;
        this.groups = groups;
        this.num = num;
        this.price = price;
        this.description = description;
        this.producer = producer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", groups=" + groups +
                ", num=" + num +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", producer='" + producer + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
                num == product.num &&
                price == product.price &&
                Objects.equals(name, product.name) &&
                Objects.equals(groups, product.groups) &&
                Objects.equals(description, product.description) &&
                Objects.equals(producer, product.producer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, groups, num, price, description, producer);
    }
}
