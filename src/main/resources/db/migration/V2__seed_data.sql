-- 初始化管理员账号（密码 123456，BCrypt 加密入库）
INSERT INTO users (username, password, enabled)
VALUES ('admin', '{bcrypt}$2a$10$8uGg3D7Z76oGD12KIZSecuYjBCkZ6BpG8VL6Bw8yM3n8iER6O1Y0O', 1);

INSERT INTO authorities (username, authority)
VALUES ('admin', 'ROLE_USER'),
       ('admin', 'ROLE_ADMIN');
