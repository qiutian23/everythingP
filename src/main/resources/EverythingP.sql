-- 创建数据库
-- create database if not exists everythingP;
-- 创建表
drop table if exists file_index;
create table if not exists file_index(
    name varchar (256) not null comment '文件名称',
    path varchar (1024) not null comment '文件路径',
    depth int not null comment '文件深度',
    file_type varchar (20) not null comment '文件类型',
    length long not null comment '文件大小',
    last_modify_time varchar(100) not null comment '文件最近修改时间'
);

-- 对文件名进行检索，所以对文件名建索引查询会比较快
create index file_name on file_index(name);