# Sklad
final project kcz
api
http://localhost:8001/login - Get - params: login:login and password:MDA5 password - return String token, should be in all request headers as token - 200
http://localhost:8001/api/good - Get - get all products as JSON array of products - 200
http://localhost:8001/api/good - Post - update product - body: json with params groups(arrayList<String>), name, num, price, description, producer, id - 204
http://localhost:8001/api/good - Put - create new Product - body: json with params groups(arrayList<String>), name, num, price, description, producer - 201 
http://localhost:8001/api/good/{id} - Get - get product with this id - return json of product with all params - 200
http://localhost:8001/api/good/{id} - Delete - remove product with this id - 204
http://localhost:8001/api/good/increment - Post - increment number of product with id by num - body: json with id, num - 200
http://localhost:8001/api/good/decrement - Post - decrement number of product with id by num - body: json with id, num - 200
