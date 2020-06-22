package server;

import com.squareup.okhttp.*;
import model.Product;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.naming.AuthenticationException;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class HTTPClient {
    String token;

    public HTTPClient() {
        token = "";
    }

    public void login(String login, String password) throws NoSuchAlgorithmException, IOException, AuthenticationException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter.printHexBinary(digest);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8001/login?login="+login+"&password="+myHash)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code()==200){
            token = response.body().string();
            response.body().close();
        }
        else {
            response.body().close();
            throw new AuthenticationException("wrong user login or password");
        }
    }


    public Product getProduct(int id) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("token", token)
                .url("http://localhost:8001/api/good/"+id)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code()==200){
            JSONObject object = new JSONObject(response.body().string());
            Product product = new Product(object.getInt("id"),
                    object.getString("name"),
                    object.getJSONArray("groups").toList().stream().map(x->(String) x).collect(Collectors.toCollection(ArrayList::new)),
                    object.getInt("num"),
                    object.getInt("price"),
                    object.getString("description"),
                    object.getString("producer"));
            response.body().close();
            return product;
        }
        response.body().close();
        return null;
    }
    public boolean deleteProduct(int id) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("token", token)
                .url("http://localhost:8001/api/good/"+id)
                .delete()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code()==204){
            response.body().close();
            return true;
        }
        response.body().close();
        return false;
    }

    public int insertProduct(String name, ArrayList<String> groups, int price, int num, String description, String producer) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject()
                .put("name", name)
                .put("groups", groups)
                .put("price", price)
                .put("num", num)
                .put("description", description)
                .put("producer", producer);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .header("token", token)
                .url("http://localhost:8001/api/good")
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.code()==201){
            int id = Integer.parseInt(response.body().string());
            response.body().close();
            return id;
        }
        response.body().close();
        return -1;
    }

    public boolean updateProduct(Product product) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject()
                .put("name", product.getName())
                .put("groups", product.getGroups())
                .put("price", product.getPrice())
                .put("num", product.getNum())
                .put("id", product.getId())
                .put("description", product.getDescription())
                .put("producer", product.getProducer());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .header("token", token)
                .url("http://localhost:8001/api/good")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.code()==204){
            response.body().close();
            return true;
        }
        response.body().close();
        return false;
    }

    public ArrayList<Product> getAllProducts() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("token", token)
                .url("http://localhost:8001/api/good")
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code()==200){
            JSONArray objects = new JSONArray(response.body().string());
            ArrayList<Product> products = new ArrayList<>();

            for (int i = 0 ; i< objects.length(); ++i){
                JSONObject object = objects.getJSONObject(i);
                Product product = new Product(object.getInt("id"),
                        object.getString("name"),
                        object.getJSONArray("groups").toList().stream().map(x->(String) x).collect(Collectors.toCollection(ArrayList::new)),
                        object.getInt("num"),
                        object.getInt("price"),
                        object.getString("description"),
                        object.getString("producer"));
                products.add(product);
            }

            response.body().close();
            return products;
        }
        response.body().close();
        return null;
    }
}
