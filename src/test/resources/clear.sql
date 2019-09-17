DROP TABLE payment_request;
CREATE TABLE payment_request(
id int NOT NULL AUTO_INCREMENT,
route_id int,
client_id int ,
ticket_id int ,
departure_date_time varchar(100),
request_status varchar (20),
PRIMARY KEY(id));