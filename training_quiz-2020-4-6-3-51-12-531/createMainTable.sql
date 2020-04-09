CREATE TABLE parking_lot_main (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    parking_lot_name VARCHAR(20) NOT NULL,
    spaces_number INT NOT NULL
);

/* 
下面的表格根据程序的初始化输入，自动生成对应的表。最初只需要建立上面的主表即可
CREATE TABLE parking_lot_A (
    id INT NOT NULL PRIMARY KEY,
    car_number VARCHAR(6) NOT NULL
);
*/