
create table sys_user (
    id varchar(64),
    name varchar(255)
);

create table sys_role (
    id varchar(64),
    name varchar(255)
);

create table sys_menu (
    id varchar(64),
    name varchar(255),
    parent_id varchar(64)
);

create table sys_role_menu (
    id identity,
    role_id varchar(64),
    menu_id varchar(64)
);

create table sys_user_role (
    id identity,
    user_id varchar(64),
    role_id varchar(64)
);