# InnoDB存储引擎

## 简介

InnoDB 是一种平衡高可靠性和高性能的通用存储引擎，在MySQL5.7中 InnoDB是默认的MySQL 存储引擎，
在CREATE TABLE 不带 “ENGINE=” 的子句也会创建InnoDB的表。

### InnoDB 的优势

* 它的DML操作准苏ACID模型，具有提交、回滚和崩溃回复 功能的事务来保护用户数据。
* 行锁定的风格和Oracle风格的一致性读取，提高多用户并发性能
* InnoDB 表将在数据排列的齿盘上，根据主键有虎牙查询，每个InnoDB, 都有一个称为“聚簇索引”的主键索引，主要用于组织数据以最小化主键
查找的I/O。
* 要保持数据的完整性，请InnoDB支持 forEIGN KEY 约束。使用外键，将检查插入，更新和删除，以确保它们不会导致不同表之间的不一致。

### InnoDB 新特征
* VARCHAR 可以使用 in-place 增加列大小，如下所示：


    ALTER TABLE t1 ALGORITHM=INPLACE, CHANGE COLUMN C1 C1 VARCHAR(255);
    
只要VARCHAR 所需的长度字节数保持不变，对于VARCHAR大小0到255字节的列，需要一个长度的字节来编码该值， 对于 VARCAHR 大小大于或者等于
256个字节列，需要两个长度的字节，结果，就 

减少VARCHAR 使用大小 ALTER TABLE 不被支持。减小VARCHAR 大小需要表副本（ALGORITHM=COPY）