-- Initialize the stadisticts database
CREATE DATABASE IF NOT EXISTS stadisticts_dev;
CREATE DATABASE IF NOT EXISTS stadisticts_test;

-- Create user and grant permissions
CREATE USER IF NOT EXISTS 'stadisticts_user'@'%' IDENTIFIED BY 'userpassword';
GRANT ALL PRIVILEGES ON stadisticts_dev.* TO 'stadisticts_user'@'%';
GRANT ALL PRIVILEGES ON stadisticts_test.* TO 'stadisticts_user'@'%';
FLUSH PRIVILEGES;

-- Use the development database
USE stadisticts_dev;

-- Set default charset
ALTER DATABASE stadisticts_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
