
create table sys_org (
    id varchar(64) PRIMARY KEY,
    name varchar(255) DEFAULT NULL,
    deleted INTEGER DEFAULT NULL
);
create table sys_org2 (
    id varchar(64) PRIMARY KEY,
    name varchar(255) DEFAULT NULL,
    delete_time TIMESTAMP DEFAULT NULL
);

create table sys_company (
    id varchar(64) PRIMARY KEY,
    name varchar(255) DEFAULT NULL,
    start_date DATE DEFAULT NULL,
    unified_code varchar(255) DEFAULT NULL
);

create table sys_user (
    id varchar(64) PRIMARY KEY,
    name varchar(255)
);

create table sys_auto_user (
    id identity PRIMARY KEY,
    first_name varchar(64),
    last_name varchar(64)
);

create table sys_role (
    id varchar(64) PRIMARY KEY,
    name varchar(255)
);

create table sys_menu (
    id varchar(64) PRIMARY KEY,
    name varchar(255),
    parent_id varchar(64),
    integer_list varchar(255),
    long_list varchar(255),
    string_list varchar(255),
    integer_set varchar(255),
    long_set varchar(255),
    string_set varchar(255),
    integer_linkedhashset varchar(255),
    long_linkedhashset varchar(255),
    string_linkedhashset varchar(255),
    bytes1 binary(16),
    bytes2 binary(16),
    uuid binary(16),
    with_dash_uuid varchar(36),
    no_dash_uuid varchar(32)
);

create table sys_role_menu (
    id identity PRIMARY KEY,
    role_id varchar(64),
    menu_id varchar(64)
);

create table sys_user_role (
    id identity PRIMARY KEY,
    user_id varchar(64),
    role_id varchar(64)
);