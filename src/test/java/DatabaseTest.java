import db.Database;
import model.Product;
import model.ProductGroup;
import org.junit.jupiter.api.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseTest {
    static Database database;
    @BeforeAll
    static void setUp() throws SQLException {
        database = new Database("jdbc:mysql://localhost:3306/store_test?useUnicode=true&serverTimezone=UTC");
        PreparedStatement st = database.getConnection().prepareStatement("create table if not exists product (\n" +
                "  product_id   INT           NOT NULL AUTO_INCREMENT,\n" +
                "  product_name VARCHAR(100) NOT NULL,\n" +
                "  price INT NOT NULL DEFAULT 1,\n" +
                "  num INT NOT NULL DEFAULT 0,\n" +
                "  description  VARCHAR(100),\n" +
                "  producer  VARCHAR(100),\n" +
                "  PRIMARY KEY (product_id),\n" +
                "  UNIQUE INDEX `product_name_UNIQUE` (`product_name` ASC) VISIBLE\n" +
                ");\n");
        st.executeUpdate();

        st = database.getConnection().prepareStatement("create table if not exists p_group (\n" +
                "  p_group_id    INT           NOT NULL AUTO_INCREMENT,\n" +
                "  p_group_name  VARCHAR(100) NOT NULL,\n" +
                "  description  VARCHAR(100),\n" +
                "  PRIMARY KEY (p_group_id),\n" +
                "  UNIQUE INDEX `p_group_name_UNIQUE` (`p_group_name` ASC) VISIBLE\n" +
                ");\n");
        st.executeUpdate();

        st = database.getConnection().prepareStatement("create table if not exists product_group (\n" +
                "  pg_id     INT NOT NULL AUTO_INCREMENT,\n" +
                "  pg_product INT NOT NULL,\n" +
                "  pg_group  INT NOT NULL,\n" +
                "  PRIMARY KEY (pg_id),\n" +
                "  FOREIGN KEY (pg_product) REFERENCES product (product_id) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "  FOREIGN KEY (pg_group) REFERENCES p_group (p_group_id) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ");\n");
        st.executeUpdate();
    }

    @Test
    @Order(1)
    void createProductGroup() {
        database.createProductGroup("a", "s");
        database.createProductGroup("b", "w");
    }

    @Test
    @Order(3)
    void createProduct1() {
        database.createProduct("b",10,5, Stream.of("a").collect(Collectors.toCollection(ArrayList::new)), "a", "x");
        database.createProduct("a",5,10,Stream.of("b").collect(Collectors.toCollection(ArrayList::new)), null, null);
    }

    @Test
    @Order(4)
    void createProduct2() {
        ArrayList<String> s =new ArrayList<>();
        s.add("a");
        s.add("b");
        database.createProduct("c",15,15,s, "w", null);
    }

    @Test
    @Order(5)
    void getAllGroups() {
        System.out.println(database.getAllGroups());
    }

    @Test
    @Order(6)
    void getGroupByName() {
        System.out.println(database.getGroupByName("a"));
    }

    @Test
    @Order(6)
    void getGroupById() {
        System.out.println(database.getGroupById(1));
    }

    @Test
    @Order(7)
    void getAllProducts() {
        System.out.println(database.getAllProducts());
    }

    @Test
    @Order(8)
    void getProductWithName() {
        System.out.println(database.getProductByName("a"));
    }

    @Test
    @Order(9)
    void getProductWithPriceMore() {
        System.out.println(database.getProductWithPriceMore(5));
    }

    @Test
    @Order(10)
    void getProductWithPriceLess() {
        System.out.println(database.getProductWithPriceLess(15));
    }

    @Test
    @Order(11)
    void getProductWithPriceBeetween() {
        System.out.println(database.getProductWithPriceBeetween(5,15));
    }

    @Test
    @Order(12)
    void getProductWithNumMore() {
        System.out.println(database.getProductWithNumMore(5));
    }

    @Test
    @Order(13)
    void getProductWithNumLess() {
        System.out.println(database.getProductWithNumLess(15));
    }

    @Test
    @Order(14)
    void getProductWithNumBeetween() {
        System.out.println(database.getProductWithNumBeetween(5,15));
    }

    @Test
    @Order(15)
    void updateGroup() {
        ProductGroup g =database.getGroupByName("a");
        g.setName("d");
        g.setDescription("qw");
        database.updateGroup(g);
        System.out.println(database.getGroupByName("d"));
    }

    @Test
    @Order(16)
    void updateProduct() {
        Product p = database.getProductByName("a");
        p.setName("d");
        p.setNum(8);
        p.setPrice(9);
        ArrayList<String> s = new ArrayList<>();
        s.add("d");
        s.add("b");
        p.setGroups(s);
        database.updateProduct(p);
        System.out.println(database.getProductByName("d"));
    }


    @Test
    @Order(17)
    void deleteProduct() {
        database.deleteProduct("d");
    }


    @Test
    @Order(19)
    void deleteGroup() {
        for (ProductGroup g: database.getAllGroups()){
            database.deleteGroup(g);
        }
    }


    @Test
    @Order(18)
    void deleteProduct2() {
        database.deleteProduct(database.getProductByName("c"));
    }

    @AfterAll
    static void tearDown() throws SQLException {
        PreparedStatement st = database.getConnection().prepareStatement("drop table product_group");
        st.executeUpdate();

         st = database.getConnection().prepareStatement("drop table product");
        st.executeUpdate();

         st = database.getConnection().prepareStatement("drop table p_group ");
        st.executeUpdate();

    }
}


/*
CREATE TABLE product (
  product_id   INT           NOT NULL AUTO_INCREMENT,
  product_name VARCHAR(100) NOT NULL,
  price INT NOT NULL DEFAULT 1,
  num INT NOT NULL DEFAULT 0,
  PRIMARY KEY (product_id),
  UNIQUE INDEX `product_name_UNIQUE` (`product_name` ASC) VISIBLE
);

CREATE TABLE p_group (
  p_group_id    INT           NOT NULL AUTO_INCREMENT,
  p_group_name  VARCHAR(100) NOT NULL,
  PRIMARY KEY (p_group_id),
  UNIQUE INDEX `p_group_name_UNIQUE` (`p_group_name` ASC) VISIBLE
);

CREATE TABLE product_group (
  pg_id     INT NOT NULL AUTO_INCREMENT,
  pg_product INT NOT NULL,
  pg_group  INT NOT NULL,
  PRIMARY KEY (pg_id),
  FOREIGN KEY (pg_product) REFERENCES product (product_id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (pg_group) REFERENCES p_group (p_group_id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE `store`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `password` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE);
* */