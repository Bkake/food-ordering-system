# launch postgres container 
docker run --name postgres-dev -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres

# curl post orders
curl --location 'localhost:8881/orders' \
--header 'Content-Type: application/json' \
--data '{
"customerId": "d215b5f8-0249-4dc5-89a3-51fd148cfb41",
"restaurantId": "d215b5f8-0249-4dc5-89a3-51fd148cfb45",
"deliveryAddress": {
"street": "street_1",
"postalCode": "1000AB",
"city": "Amsterdam"
},
"price": 200.00,
"items": [
{
"productId": "d215b5f8-0249-4dc5-89a3-51fd148cfb48",
"quantity": 1,
"price": 50.00,
"subTotal": 50.00
},
{
"productId": "d215b5f8-0249-4dc5-89a3-51fd148cfb48",
"quantity": 3,
"price": 50.00,
"subTotal": 150.00
}
]
}'