from locust import HttpUser, task, between
import random
import string
from datetime import datetime

class MyUser(HttpUser):
    wait_time = between(1, 3)  # Wait time between tasks

    host = "http://localhost:8080"  # Base URL for the tests


    @task
    def createUser(self):
        username = ''.join(random.choices(string.ascii_lowercase, k=8))
        payload = {
            "userId": random.randint(1, 1000),
            "firstName": username.capitalize(),
            "lastName": "Test",
            "imageUrl": "https://placehold.co/100x100",
            "email": f"{username}@test.com",
            "phone": f"+34{random.randint(600000000, 699999999)}",
            "credential": {
                "credentialId": random.randint(1, 1000),
                "username": username,
                "password": ''.join(random.choices(string.ascii_letters + string.digits, k=10)),
                "roleBasedAuthority": "ROLE_USER",
                "isEnabled": True,
                "isAccountNonExpired": True,
                "isAccountNonLocked": True,
                "isCredentialsNonExpired": True,
                
            }
        }
        self.client.post("/user-service/api/users/", json=payload)

    @task
    def getProducts(self):
        self.client.get("/product-service/api/products/")

    @task
    def updateProduct(self):
        payload = {
            "productId": 1,
            "productTitle": "Producto actualizado",
            "sku": "SKU1234",
            "priceUnit": random.randint(10, 100),
            "quantity": random.randint(1, 50),
            "imageUrl": "https://placehold.co/100x100"
        }
        self.client.put("/product-service/api/products/1", json=payload)

    @task
    def getOrders(self):
        self.client.get("/order-service/api/orders/")
    

    @task
    def createShipping(self):
        payload = {
            "productId": random.randint(1, 1000),
            "orderId": random.randint(1, 1000),
            "orderedQuantity": random.randint(1, 10),
            "product": {
                "productId": random.randint(1, 1000),
                "productTitle": "Producto de prueba",
                "imageUrl": "https://placehold.co/100x100",
                "sku": "SKU1234",
                "priceUnit": 10.0,
                "quantity": random.randint(1, 50), 
            },
            "order":{
                "orderId": random.randint(1, 1000),
                "orderDate": datetime.now().strftime("%d-%m-%Y__%H:%M:%S:%f"),
                "orderDesc": "Pedido de prueba",
                "orderFee": random.randint(20, 200),
            }
        }
        self.client.post("/shipping-service/api/shippings/", json=payload)



    @task
    def createPayment(self):
        payload = {
            "paymentId": random.randint(1, 1000),
            "isPayed": random.choice([True, False]),
            "paymentStatus": "IN_PROGRESS",
            "order":{
                "orderId": random.randint(1, 1000),
                "orderDate": datetime.now().strftime("%d-%m-%Y__%H:%M:%S:%f"),
                "orderDesc": "Pago de prueba",
                "orderFee": random.randint(20, 200),
            }
        }
        self.client.post("/payment-service/api/payments/", json=payload)