package db;

import model.Product;
import model.ProductGroup;
import model.User;
import org.json.JSONArray;

import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/store?useUnicode=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "йцукен";
    private Connection connection;

    public Database() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public void close() throws SQLException {
        connection.close();
    }

    public Database(String url) throws SQLException {
        connection = DriverManager.getConnection(url, USER, PASSWORD);
    }
    public Connection getConnection() {
        return connection;
    }

    public long createProductGroup(String name, String description) {
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO p_group(p_group_name, description) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setString(2, description);
            int result = statement.executeUpdate();
            long id;
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = (generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating failed, no ID obtained.");
                }
            }
            statement.close();
            return id;
        }catch (SQLException e) {
            return -1;
        }
    }
    public long createProduct(String name, int num, int price, ArrayList<String> groups, String description, String producer) {
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO product(product_name, num, price, description, producer) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setInt(2, num);
            statement.setInt(3, price);
            statement.setString(4, description);
            statement.setString(5, producer);
            int result = statement.executeUpdate();
            long id;
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = (generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating failed, no ID obtained.");
                }
            }
            statement.close();
            for (String s: groups){
                PreparedStatement st = connection.prepareStatement("Select product_id, p_group_id from \n" +
                        "\tproduct\n" +
                        "    inner join p_group\n" +
                        "    on p_group_name = ? " +
                        "    where product_name = ?");
                st.setString(1, s);
                st.setString(2, name);
                ResultSet res = st.executeQuery();
                while (res.next()) {
                    int p_id = res.getInt("product_id");
                    int g_id = res.getInt("p_group_id");

                    statement = connection.prepareStatement("INSERT INTO product_group(pg_product, pg_group) VALUES (?, ?)");
                    statement.setInt(1,p_id);
                    statement.setInt(2,g_id);
                    statement.executeUpdate();
                }
            }
            return id;
        } catch (SQLException ignored) {}
        return -1;
    }

    public ArrayList<ProductGroup> getAllGroups(){
        try{
            ArrayList<ProductGroup> groups = new ArrayList<ProductGroup>();
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM p_group");
            while (res.next()) {
                String name = res.getString("p_group_name");
                String description = res.getString("description");
                int id = res.getInt("p_group_id");
                groups.add(new ProductGroup(id, name, description));
            }
            res.close();
            st.close();
            return groups;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
}
    public ProductGroup getGroupByName(String name){
        return getAllGroups().stream().filter(x->x.getName().equals(name)).collect(Collectors.toList()).get(0);
    }
    public ProductGroup getGroupById(int id){
        return getAllGroups().stream().filter(x->x.getId() == id).collect(Collectors.toList()).get(0);
    }

    public ArrayList<Product> getAllProducts(){
        try{
            ArrayList<Product> products = new ArrayList<Product>();
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery("SELECT\n" +
                    "    product_id                                                                                              AS product_id,\n" +
                    "    max(product_name)                                                                                       AS product_name,\n" +
                    "    price,\n" +
                    "    num,\n" +
                    "    product.description,\n" +
                    "    producer,\n" +
                    "    cast(concat('[', group_concat(json_quote(p_group_name) ORDER BY p_group_name SEPARATOR ','), ']') as json) AS group_array\n" +
                    "  FROM\n" +
                    "    product\n" +
                    "    INNER JOIN product_group\n" +
                    "      ON product_id = product_group.pg_product\n" +
                    "    INNER JOIN p_group\n" +
                    "      ON product_group.pg_group = p_group.p_group_id\n" +
                    "GROUP BY\n" +
                    "    product_id;");
            while (res.next()) {
                String name = res.getString("product_name");
                int id = res.getInt("product_id");
                int price = res.getInt("price");
                int num = res.getInt("num");
                String description = res.getString("description");
                String producer = res.getString("producer");
                JSONArray array = new JSONArray((String)res.getObject("group_array"));
                ArrayList<String> groups = new ArrayList<String>();
                for (Object s : array) {
                    groups.add((String) s);
                }
                Product product = new Product(id, name, groups, num, price, description, producer);
                products.add(product);
            }
            res.close();
            st.close();
            return products;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    public ArrayList<Product> getAllProductsForGroup(String name){
        return getAllProducts().stream().filter(x->x.getGroups().contains(name)).collect(Collectors.toCollection(ArrayList::new));
    }
    public Product getProductById (int id){
        return getAllProducts().stream().filter(x->x.getId() == id).collect(Collectors.toList()).get(0);
    }
    public Product getProductByName(String name){
        return getAllProducts().stream().filter(x->x.getName().equals(name)).collect(Collectors.toList()).get(0);
    }

    public int incrementProductQuantity(int id, int num){
        try{
            PreparedStatement statement = connection.prepareStatement("UPDATE product SET num =num + ? WHERE product_id = ?");
            //statement.setInt(1, 1);
            statement.setInt(1, num);
            statement.setInt(2, id);
            int result = statement.executeUpdate();
            statement.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int decrementProductQuantity(int id, int num){
        try{
            PreparedStatement statement = connection.prepareStatement("UPDATE product SET num =case\n" +
                    "                  when num - ? < 0 then null\n" +
                    "                  else num - ?\n" +
                    "                 end WHERE product_id = ?");
            //statement.setInt(1, 1);
            statement.setInt(1, num);
            statement.setInt(2, num);
            statement.setInt(3, id);
            int result = statement.executeUpdate();
            statement.close();
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ArrayList<User> getAllUsers(){
        try{
            ArrayList<User> users = new ArrayList<User>();
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM users");
            while (res.next()) {
                String name = res.getString("name");
                String password = res.getString("password");
                users.add(new User(name, password));
            }
            res.close();
            st.close();
            return users;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }



    public ArrayList<Product> getProductWithPriceMore(int price){
        return (ArrayList<Product>) getAllProducts().stream().filter(x->x.getPrice()>price).collect(Collectors.toList());
    }
    public ArrayList<Product> getProductWithPriceLess(int price){
        return (ArrayList<Product>) getAllProducts().stream().filter(x->x.getPrice()<price).collect(Collectors.toList());
    }
    public ArrayList<Product> getProductWithPriceBeetween(int priceLo, int priceHi){
        return (ArrayList<Product>) getAllProducts().stream().filter(x->x.getPrice()>priceLo && x.getPrice()<priceHi).collect(Collectors.toList());
    }
    public ArrayList<Product> getProductWithNumMore(int num){
        return (ArrayList<Product>) getAllProducts().stream().filter(x->x.getNum()>num).collect(Collectors.toList());
    }
    public ArrayList<Product> getProductWithNumLess(int num){
        return (ArrayList<Product>) getAllProducts().stream().filter(x->x.getNum()<num).collect(Collectors.toList());
    }
    public ArrayList<Product> getProductWithNumBeetween(int numLo, int numHi){
        return (ArrayList<Product>) getAllProducts().stream().filter(x->x.getNum()>numLo && x.getNum()<numHi).collect(Collectors.toList());
    }



    public int updateGroup(ProductGroup productGroup){
        try{
            PreparedStatement statement = connection.prepareStatement("UPDATE p_group SET `p_group_name` = ?, `description`= ? WHERE p_group_id = ?");
            //statement.setInt(1, 1);
            statement.setString(1, productGroup.getName());
            statement.setInt(3, productGroup.getId());
            statement.setString(2, productGroup.getDescription());

            int result = statement.executeUpdate();
            statement.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int updateProduct(Product product){
        try{
            PreparedStatement statement = connection.prepareStatement("UPDATE product SET product_name = ?, num = ?, price = ?, description =?, producer = ? WHERE product_id = ?");
            //statement.setInt(1, 1);
            statement.setString(1, product.getName());
            statement.setInt(2, product.getNum());
            statement.setInt(3, product.getPrice());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getProducer());
            statement.setInt(6, product.getId());
            int result = statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement("DELETE from product_group where pg_product = ?");
            statement.setInt(1,product.getId());
            statement.executeUpdate();

            for (String s: product.getGroups()){
                PreparedStatement st = connection.prepareStatement("Select product_id, p_group_id from \n" +
                        "\tproduct\n" +
                        "    inner join p_group\n" +
                        "    on p_group_name = ? " +
                        "    where product_name = ?");
                st.setString(1, s);
                st.setString(2, product.getName());
                ResultSet res = st.executeQuery();
                while (res.next()) {
                    int p_id = res.getInt("product_id");
                    int g_id = res.getInt("p_group_id");

                    statement = connection.prepareStatement("INSERT INTO product_group(pg_product, pg_group) VALUES (?, ?)");
                    statement.setInt(1,p_id);
                    statement.setInt(2,g_id);
                    statement.executeUpdate();
                }
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteGroup(ProductGroup productGroup){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT pg_product from product_group where pg_group = ?");
            statement.setInt(1,productGroup.getId());
            ResultSet res = statement.executeQuery();
            while (res.next()){
             int i =  res.getInt("pg_product");
             deleteProduct(i);
            }


         statement = connection.prepareStatement("DELETE from p_group where p_group_id = ?");
        //statement.setInt(1, 1);
        statement.setInt(1,productGroup.getId());
        int result = statement.executeUpdate();
        statement.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
    public void deleteGroupById(int id){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT pg_product from product_group where pg_group = ?");
            statement.setInt(1,id);
            ResultSet res = statement.executeQuery();
            while (res.next()){
                int i =  res.getInt("pg_product");
                deleteProduct(i);
            }


            statement = connection.prepareStatement("DELETE from p_group where p_group_id = ?");
            //statement.setInt(1, 1);
            statement.setInt(1,id);
            int result = statement.executeUpdate();
            statement.close();
            if (result==0)
                throw new IllegalArgumentException("no group");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(Product product){
        try{
            PreparedStatement statement = connection.prepareStatement("DELETE from product where product_id = ?");
            //statement.setInt(1, 1);
            statement.setInt(1,product.getId());
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteProduct(int id){
        try{
            PreparedStatement statement = connection.prepareStatement("DELETE from product where product_id = ?");
            //statement.setInt(1, 1);
            statement.setInt(1,id);
            int result = statement.executeUpdate();
            statement.close();
            if (result==0)
                throw new IllegalArgumentException("no product");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteProduct(String name){
        try{
            PreparedStatement statement = connection.prepareStatement("DELETE from product where product_name = ?");
            //statement.setInt(1, 1);
            statement.setString(1,name);
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}













/*    public void createProduct(String name, int num, int price, String group) {
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO product(product_name, num, price) VALUES (?, ?, ?)");
            statement.setString(1, name);
            statement.setInt(2, num);
            statement.setInt(3, price);
            int result = statement.executeUpdate();
            statement.close();
            PreparedStatement st = connection.prepareStatement("Select product_id, p_group_id from \n" +
                    "\tproduct\n" +
                    "    inner join p_group\n" +
                    "    on p_group_name = ? " +
                    "    where product_name = ?");
            st.setString(1, group);
            st.setString(2, name);
            ResultSet res = st.executeQuery();
            while (res.next()) {
                int p_id = res.getInt("product_id");
                int g_id = res.getInt("p_group_id");

                statement = connection.prepareStatement("INSERT INTO product_group(pg_product, pg_group) VALUES (?, ?)");
                statement.setInt(1,p_id);
                statement.setInt(2,g_id);
                statement.executeUpdate();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/