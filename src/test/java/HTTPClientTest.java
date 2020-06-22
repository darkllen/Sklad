import db.Database;
import io.jsonwebtoken.lang.Assert;
import model.Product;
import model.ProductGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.HTTPClient;
import server.HTTPServer;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HTTPClientTest {
    String login = "darklen";
    String password = "qwerty";

    @BeforeAll
    static  void init() throws IOException {
        HTTPServer server = new HTTPServer();
    }

    @Test
    void loginRight() throws IOException, NoSuchAlgorithmException, AuthenticationException {

        HTTPClient client = new HTTPClient();


        client.login(login,password);
    }

    @Test
    void loginFalse() throws IOException, NoSuchAlgorithmException, AuthenticationException {

        HTTPClient client = new HTTPClient();
            Assertions.assertThrows(AuthenticationException.class, () -> {
                client.login(login,password+"s");
            });
    }

    @Test
    void getProductWithoutLogin() throws IOException {
        HTTPClient client = new HTTPClient();
        Assertions.assertNull(client.getProduct(1));
    }

    @Test
    void getProductExisted() throws IOException, AuthenticationException, NoSuchAlgorithmException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        client.getProduct(1);
    }
    @Test
    void getProductNotExisted() throws IOException, AuthenticationException, NoSuchAlgorithmException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        client.getProduct(20);
    }

    @Test
    void deleteProductWithoutLogin() throws IOException {
        HTTPClient client = new HTTPClient();
        assertFalse(client.deleteProduct(1));
    }

    @Test
    void deleteProductExisted() throws IOException, AuthenticationException, NoSuchAlgorithmException, SQLException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        int id = (client.insertProduct("azxxcv", new ArrayList<>(), 12,21, "q", "w"));
        assertTrue(client.deleteProduct(id));
    }
    @Test
    void deleteProductNotExisted() throws IOException, AuthenticationException, NoSuchAlgorithmException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        assertFalse(client.deleteProduct(20000));
    }


    @Test
    void insertProductWithoutLogin() throws IOException {
        HTTPClient client = new HTTPClient();
        assertEquals(client.insertProduct("asd", new ArrayList<>(), 12, 21, "q", "w"), -1);
    }

    @Test
    void insertProduct() throws IOException, AuthenticationException, NoSuchAlgorithmException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        int id = (client.insertProduct("azxxcv", new ArrayList<>(), 12,21, "q", "w"));
        assertTrue(client.deleteProduct(id));
    }
    @Test
    void insertProductWrongInput() throws IOException, AuthenticationException, NoSuchAlgorithmException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        int id = (client.insertProduct("", new ArrayList<>(), 0,-1, "q", "w"));
        assertEquals(id, -1);
    }
    @Test
    void insertProductDuplicateName() throws IOException, AuthenticationException, NoSuchAlgorithmException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        int id = (client.insertProduct("asd", new ArrayList<>(), 2,2, "q", "w"));
        assertEquals(id, -1);
    }


    @Test
    void updateProduct() throws IOException, AuthenticationException, NoSuchAlgorithmException, SQLException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);

        Database database = new Database();
        Product product = database.getAllProducts().get(0);
        System.out.println(product);
        product.setNum(product.getNum()+1);
        assertTrue(client.updateProduct(product));
    }
    @Test
    void updateProductWrongInput() throws IOException, AuthenticationException, NoSuchAlgorithmException, SQLException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Database database = new Database();
        Product product = database.getAllProducts().get(0);
        product.setNum(-100);
        assertFalse(client.updateProduct(product));
    }
    @Test
    void updateProductWrongId() throws IOException, AuthenticationException, NoSuchAlgorithmException, SQLException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Product product = new Product(3000, "a", new ArrayList<>(), 100,100, "q", "w");
        assertFalse(client.updateProduct(product));
    }


    @Test
    void getAllProducts() throws NoSuchAlgorithmException, IOException, AuthenticationException, SQLException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Database database = new Database();
        ArrayList<Product> productsFromDb = database.getAllProducts();
        ArrayList<Product> products= client.getAllProducts();
        assertIterableEquals(productsFromDb, products);
    }
    @Test
    void incrementProductNum() throws SQLException, NoSuchAlgorithmException, IOException, AuthenticationException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Database database = new Database();
        Product product = database.getAllProducts().get(0);
        System.out.println(product);
        assertTrue(client.incrementProductNum(product.getId(), 1));
    }

    @Test
    void incrementProductNumMinus() throws SQLException, NoSuchAlgorithmException, IOException, AuthenticationException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Database database = new Database();
        Product product = database.getAllProducts().get(0);
        System.out.println(product);
        assertFalse(client.incrementProductNum(product.getId(), -3));
    }
    @Test
    void incrementProductNumWrongId() throws SQLException, NoSuchAlgorithmException, IOException, AuthenticationException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Product product = new Product(3000, "a", new ArrayList<>(), 100,100, "q", "w");
        assertFalse(client.incrementProductNum(product.getId(), 3));
    }

    @Test
    void decrementProductNum() throws SQLException, NoSuchAlgorithmException, IOException, AuthenticationException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Database database = new Database();
        Product product = database.getAllProducts().get(0);
        System.out.println(product);
        assertTrue(client.decrementProductNum(product.getId(), 2));
    }

    @Test
    void decrementProductNumMinus() throws SQLException, NoSuchAlgorithmException, IOException, AuthenticationException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Database database = new Database();
        Product product = database.getAllProducts().get(0);
        System.out.println(product);
        assertFalse(client.decrementProductNum(product.getId(), -3));
    }
    @Test
    void decrementProductNumWrongId() throws SQLException, NoSuchAlgorithmException, IOException, AuthenticationException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Product product = new Product(3000, "a", new ArrayList<>(), 100,100, "q", "w");
        assertFalse(client.decrementProductNum(product.getId(), 3));
    }

    @Test
    void getAllGroups() throws IOException, SQLException, AuthenticationException, NoSuchAlgorithmException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Database database = new Database();
        ArrayList<ProductGroup> productsFromDb = database.getAllGroups();
        ArrayList<ProductGroup> products= client.getAllGroups();
        assertIterableEquals(productsFromDb, products);
    }

    @Test
    void insertGroup() {
    }

    @Test
    void updateGroup() throws IOException, AuthenticationException, NoSuchAlgorithmException, SQLException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Database database = new Database();
        ProductGroup product = database.getAllGroups().get(0);
        System.out.println(product);
        product.setDescription("sd");
        assertTrue(client.updateGroup(product));
    }
    @Test
    void updateGroupWrongInput() throws IOException, AuthenticationException, NoSuchAlgorithmException, SQLException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        Database database = new Database();
        ProductGroup product = database.getAllGroups().get(0);
        product.setName("");
        assertFalse(client.updateGroup(product));
    }
    @Test
    void updateGroupWrongId() throws IOException, AuthenticationException, NoSuchAlgorithmException, SQLException {
        HTTPClient client = new HTTPClient();
        client.login(login,password);
        ProductGroup product = new ProductGroup(3000, "a", "w");
        assertFalse(client.updateGroup(product));
    }
}