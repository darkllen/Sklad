package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import db.Database;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import model.Product;
import model.ProductGroup;
import model.User;
import org.json.JSONObject;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


public class HTTPServer {
    private static Set<String> authorised;
    private static String key = "";

    private HttpServer server;

    public HTTPServer() throws IOException {
        if (key.isEmpty()){
            Random random = new Random();
            key = random.ints(64,65,91).map(x -> Character.forDigit(x,10)).collect(StringBuilder::new, StringBuilder::append, (x,y)->x.append(y.toString())).toString();

        }

        authorised = new HashSet<>();
        server = HttpServer.create(new InetSocketAddress("localhost", 8001), 0);
        server.createContext("/login", new  LoginHandler());
        server.createContext("/api/good/", new SpecificGoodHandler());
        server.createContext("/api/good", new GoodHandler());
        server.createContext("/api/good/increment", new IncrementHandler());
        server.createContext("/api/good/decrement", new DecrementHandler());
        server.createContext("/api/group", new GroupHandler());
        server.createContext("/api/group/", new SpecificGroupHandler());
        //    server.createContext("/api/group", new GroupHandler());
        //    server.createContext("/api/group/", new GroupHandler());


        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        server.setExecutor(threadPoolExecutor);
        server.start();
    }

    private void returnError(HttpExchange httpExchange, int code) throws IOException {
        httpExchange.sendResponseHeaders(code, 0);

    }

    private class LoginHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            if("GET".equals(httpExchange.getRequestMethod())) {
                Map<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());
                try {
                    ArrayList<User> users = new Database().getAllUsers();
                    if (users.stream().anyMatch(user -> user.getLogin().equals(params.getOrDefault("login", "")) && user.getPassword().equals(params.getOrDefault("password", "")))){
                        returnToken(httpExchange);
                    }
                } catch (SQLException ignored) {
                }
            }
            returnError(httpExchange, 401);
        }
        private Map<String, String> queryToMap(String query) {
            Map<String, String> result = new HashMap<>();
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                }else{
                    result.put(entry[0], "");
                }
            }
            return result;
        }
        private void returnToken(HttpExchange httpExchange)  throws  IOException {
            OutputStream outputStream = httpExchange.getResponseBody();


            // encode HTML content
            Map<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());
            String htmlResponse = createJWT("0", "server", params.getOrDefault("login", ""));

            // this line is a must
            httpExchange.sendResponseHeaders(200, htmlResponse.length());

            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        //Sample method to construct a JWT
        private String createJWT(String id, String issuer, String subject) {

            //The JWT signature algorithm we will be using to sign the token
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            //We will sign our JWT with our ApiKey secret
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());


            //Let's set the JWT Claims
            JwtBuilder builder = Jwts.builder().setId(id)
                    .setIssuedAt(now)
                    .setSubject(subject)
                    .setIssuer(issuer)
                    .signWith(signatureAlgorithm, signingKey);
            //if it has been specified, let's add the expiration

            //Builds the JWT and serializes it to a compact, URL-safe string
            authorised.add(subject);
            return builder.compact();
        }
    }
    private class GoodHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            Claims claims;
            try{
                claims = Jwts.parser()
                        .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                        .parseClaimsJws(httpExchange.getRequestHeaders().getOrDefault("token", new ArrayList<>()).get(0)).getBody();
            }catch (IllegalArgumentException e){
                returnError(httpExchange,403 );
                return;
            }
            if (!authorised.contains(claims.getSubject())){
                returnError(httpExchange, 403);
                return;
            }
            if("PUT".equals(httpExchange.getRequestMethod())){
                JSONObject object= new JSONObject(new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")));
                try {
                    Database database = new Database();
                    ArrayList<String> groups = object.getJSONArray("groups").toList().stream().map(x->(String)x).collect(Collectors.toCollection(ArrayList::new));
                    String name = object.getString("name");
                    int num = object.getInt("num");
                    int price = object.getInt("price");
                    String description = object.getString("description");
                    String producer = object.getString("producer");

                    if (name.isEmpty() || num<0 || price<1){
                        returnError(httpExchange, 409);
                        return;
                    }
                    long id = database.createProduct(
                            name,
                            num,
                            price,
                            groups,
                            description,
                            producer);
                    returnId(httpExchange, id);
                } catch (SQLException ignored) { ;
                }

                returnError(httpExchange,409);
            }
            else if("POST".equals(httpExchange.getRequestMethod())){
                JSONObject object= new JSONObject(new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")));
                try {
                    Database database = new Database();
                    ArrayList<String> groups = object.getJSONArray("groups").toList().stream().map(x->(String)x).collect(Collectors.toCollection(ArrayList::new));
                    String name = object.getString("name");
                    int num = object.getInt("num");
                    int price = object.getInt("price");
                    int id = object.getInt("id");
                    String description = object.getString("description");
                    String producer = object.getString("producer");


                    if (name.isEmpty() || num<0 || price<1){
                        returnError(httpExchange, 409);
                        return;
                    }
                    Product product = new Product(id, name, groups, num, price, description, producer);
                    int res = database.updateProduct(product);
                    if (res!=1) returnError(httpExchange,404);
                    httpExchange.sendResponseHeaders(204, -1);
                } catch (SQLException ignored) { ;
                }
                returnError(httpExchange,404);
            }
            else if ("GET".equals(httpExchange.getRequestMethod())) {
                try {
                    Database database = new Database();
                    ArrayList<Product> product = database.getAllProducts();
                    returnProducts(httpExchange, product);
                    return;
                }catch (Exception ignored){
                }
                returnError(httpExchange, 404);
            }

        }
        private void returnId(HttpExchange httpExchange, long id)  throws  IOException {
            OutputStream outputStream = httpExchange.getResponseBody();

            // encode HTML content
            String htmlResponse = String.valueOf(id);

            // this line is a must
            httpExchange.sendResponseHeaders(201, htmlResponse.length());

            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        private void returnProducts(HttpExchange httpExchange, ArrayList<Product> product) throws IOException {
            OutputStream outputStream = httpExchange.getResponseBody();
            ObjectMapper mapper = new ObjectMapper();
            String htmlResponse = mapper.writeValueAsString(product);
            httpExchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }
    private class SpecificGoodHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Claims claims;
            try{
                claims = Jwts.parser()
                        .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                        .parseClaimsJws(httpExchange.getRequestHeaders().getOrDefault("token", new ArrayList<>()).get(0)).getBody();
            }catch (IllegalArgumentException e){
                returnError(httpExchange,403 );
                return;
            }
            if (!authorised.contains(claims.getSubject())){
                returnError(httpExchange, 403);
                return;
            }

            if ("GET".equals(httpExchange.getRequestMethod())) {
                try {
                    int id = Integer.parseInt(httpExchange.getRequestURI().toString().split("/")[3]);
                    Database database = new Database();
                    Product product = database.getProductById(id);
                    returnProduct(httpExchange, product);
                    return;
                }catch (Exception ignored){
                }
                returnError(httpExchange, 404);
            }
            else if("DELETE".equals(httpExchange.getRequestMethod())){
                try {
                    int id = Integer.parseInt(httpExchange.getRequestURI().toString().split("/")[3]);
                    Database database = new Database();
                    try{
                        database.deleteProduct(id);
                        httpExchange.sendResponseHeaders(204, -1);
                        return;
                    } catch (Exception ignored){
                    }
                }catch (NumberFormatException | SQLException ignored){
                }
                returnError(httpExchange,404);
            }
        }

        private void returnProduct(HttpExchange httpExchange, Product product) throws IOException {
            OutputStream outputStream = httpExchange.getResponseBody();

            ObjectMapper mapper = new ObjectMapper();

            String htmlResponse = mapper.writeValueAsString(product);

            httpExchange.sendResponseHeaders(200, htmlResponse.length());

            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }


    }
    private class IncrementHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Claims claims;
            try{
                claims = Jwts.parser()
                        .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                        .parseClaimsJws(httpExchange.getRequestHeaders().getOrDefault("token", new ArrayList<>()).get(0)).getBody();
            }catch (IllegalArgumentException e){
                returnError(httpExchange,403 );
                return;
            }
            if (!authorised.contains(claims.getSubject())){
                returnError(httpExchange, 403);
                return;
            }
            if("POST".equals(httpExchange.getRequestMethod())){
                JSONObject object= new JSONObject(new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")));
                try {
                    Database database = new Database();
                    int num = object.getInt("num");
                    int id = object.getInt("id");

                    if (num<=0){
                        returnError(httpExchange, 409);
                        return;
                    }
                    int res = database.incrementProductQuantity(id, num);
                    if (res!=1) returnError(httpExchange,404);
                    httpExchange.sendResponseHeaders(204, -1);
                } catch (SQLException ignored) { ;
                }
                returnError(httpExchange,404);
            }
        }
    }
    private class DecrementHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Claims claims;
            try{
                claims = Jwts.parser()
                        .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                        .parseClaimsJws(httpExchange.getRequestHeaders().getOrDefault("token", new ArrayList<>()).get(0)).getBody();
            }catch (IllegalArgumentException e){
                returnError(httpExchange,403 );
                return;
            }
            if (!authorised.contains(claims.getSubject())){
                returnError(httpExchange, 403);
                return;
            }
            if("POST".equals(httpExchange.getRequestMethod())){
                JSONObject object= new JSONObject(new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")));
                try {
                    Database database = new Database();
                    int num = object.getInt("num");
                    int id = object.getInt("id");
                    if (num<=0){
                        returnError(httpExchange, 409);
                        return;
                    }
                    int res = database.decrementProductQuantity(id, num);
                    if (res!=1) returnError(httpExchange,404);
                    httpExchange.sendResponseHeaders(204, -1);
                } catch (SQLException ignored) { ;
                }
                returnError(httpExchange,404);
            }
        }
    }

    private class GroupHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            Claims claims;
            try{
                claims = Jwts.parser()
                        .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                        .parseClaimsJws(httpExchange.getRequestHeaders().getOrDefault("token", new ArrayList<>()).get(0)).getBody();
            }catch (IllegalArgumentException e){
                returnError(httpExchange,403 );
                return;
            }
            if (!authorised.contains(claims.getSubject())){
                returnError(httpExchange, 403);
                return;
            }

            if("PUT".equals(httpExchange.getRequestMethod())){
                JSONObject object= new JSONObject(new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")));
                try {
                    Database database = new Database();
                    String name = object.getString("name");
                    String description = object.getString("description");
                    if (name.length() == 0) {
                        returnError(httpExchange, 409);
                        return;
                    }
                    long id = database.createProductGroup(
                            name,
                            description);
                    returnId(httpExchange, id);
                } catch (SQLException ignored) { ;
                }

                returnError(httpExchange,409);
            }
            else if("POST".equals(httpExchange.getRequestMethod())){
                JSONObject object= new JSONObject(new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")));
                try {
                    Database database = new Database();
                    String name = object.getString("name");
                    int id = object.getInt("id");
                    String description = object.getString("description");
                    if (name.length() == 0) {
                        returnError(httpExchange, 409);
                        return;
                    }
                    ProductGroup group = new ProductGroup(id, name, description);
                    int res = database.updateGroup(group);
                    if (res!=1) returnError(httpExchange,404);
                    httpExchange.sendResponseHeaders(204, -1);
                } catch (SQLException ignored) { ;
                }
                returnError(httpExchange,404);
            }
            else if ("GET".equals(httpExchange.getRequestMethod())) {
                try {
                    Database database = new Database();
                    ArrayList<ProductGroup> groups = database.getAllGroups();
                    returnGroups(httpExchange, groups);
                    return;
                }catch (Exception ignored){
                }
                returnError(httpExchange, 404);
            }

        }

        private void returnId(HttpExchange httpExchange, long id)  throws  IOException {
            OutputStream outputStream = httpExchange.getResponseBody();

            // encode HTML content
            String htmlResponse = String.valueOf(id);

            // this line is a must
            httpExchange.sendResponseHeaders(201, htmlResponse.length());

            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        private void returnGroups(HttpExchange httpExchange, ArrayList<ProductGroup> groups) throws IOException {
            OutputStream outputStream = httpExchange.getResponseBody();
            ObjectMapper mapper = new ObjectMapper();
            String htmlResponse = mapper.writeValueAsString(groups);
            httpExchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }

    private class SpecificGroupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Claims claims;
            try{
                claims = Jwts.parser()
                        .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                        .parseClaimsJws(httpExchange.getRequestHeaders().getOrDefault("token", new ArrayList<>()).get(0)).getBody();
            }catch (IllegalArgumentException e){
                returnError(httpExchange,403 );
                return;
            }
            if (!authorised.contains(claims.getSubject())){
                returnError(httpExchange, 403);
                return;
            }

            if ("GET".equals(httpExchange.getRequestMethod())) {
                try {
                    int id = Integer.parseInt(httpExchange.getRequestURI().toString().split("/")[3]);
                    Database database = new Database();
                    ProductGroup group = database.getGroupById(id);
                    returnGroup(httpExchange, group);
                    return;
                }catch (Exception ignored){
                }
                returnError(httpExchange, 404);
            }
            else if("DELETE".equals(httpExchange.getRequestMethod())){
                try {
                    int id = Integer.parseInt(httpExchange.getRequestURI().toString().split("/")[3]);
                    Database database = new Database();
                    try{
                        database.deleteGroupById(id);
                        httpExchange.sendResponseHeaders(204, -1);
                        return;
                    } catch (Exception ignored){
                    }
                }catch (NumberFormatException | SQLException ignored){
                }
                returnError(httpExchange,404);
            }
        }

        private void returnGroup(HttpExchange httpExchange, ProductGroup group) throws IOException {
            OutputStream outputStream = httpExchange.getResponseBody();

            ObjectMapper mapper = new ObjectMapper();

            String htmlResponse = mapper.writeValueAsString(group);

            httpExchange.sendResponseHeaders(200, htmlResponse.length());

            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }


    }
}
