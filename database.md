# mysql
## mysql 事务隔离的级别
- 事务隔离是为了解决脏读、不可重复读、幻读。脏读，一个事务读了另一个事务未提交的数据。不可重复读，事务两次读取的数据不一样。幻读，数据两次读取数据的行数不一样。
- 读未提交，指未提交的数据也可以被读取到，最低的隔离级别
- 都提交，只有提交的事务修改的内容才能被读取，解决了脏读。
- 可重复读，数据两次读取的内容是一样的，无论有没有修改，可以解决脏读，不能解决幻读。
- 串行化，一次只能执行一条事务。
## mysql主从同步是如何实现的
- 主服务器把数据更改记录到二进制日志binlog中
- 从服务器把主服务器的把二进制日志复制到自己的中继日志中。
- 从服务器重做中继日志，与主服务器数据保持一致。
## 数据库引擎innodb与MyISAM的区别
- innodb支持事务，行级锁，支持外键，myisam不支持
- innodb不支持全文索引
- innodb增删改查性能更优，MyISAM查询性能更优
## Mysql的索引
- 索引是一种快速定位数据存储位置的数据结构，常用的索引数据结构有哈希表、二叉树，mysql默认使用的是B+树，与二叉树相比，二叉树更加矮宽，更适合存在磁盘里。
## B树与B+树
- B树是二叉树的变种，B+树是B树的变种，B树是平衡多路搜索树，所以更加的矮宽。而B+树与B树的不同是B+树只在叶子节点存放数据，其他节点只存放key。同时B+树叶子节点通过链表相连，可以范围查找和顺序查找。B+树的非叶子节点只存放key，占有更少的存储，查找时磁盘IO次数也会减少。
## 聚簇索引和非聚簇索引
- 聚簇索引是数据和索引存放在一起的索引，非聚簇索引是数据与索引分开存放，索引存放数据行的地址。
- 在Innodb中，一个表只会根据主键建立一个聚簇索引，其他索引都是辅助索引。使用辅助索引访问索引外的字段时需要二次查找。
## 数据库的ACID
- 原子性：原子性是指整个数据的事务是一个不可分割的单位，一个事务内的操作要么全部执行成功，要么全部不执行。
- 一致性：数据库再事务执行前后保持一致的状态。
- 隔离性：一个事务所做的修改在未提交之前对其他事务是不可见的。
- 持久性：一个事务一旦提交，其对数据库的修改是永久的。即使系统发生崩溃，也能通过日志恢复。
## Innodb 的 MVCC
- MVCC是多版本并发控制，逻辑是维护一个数据的多个版本，使得读写操作没有冲突。最大的优点是无锁并发，读不加锁。
# mybatis
## mybatis中 #与$的区别
- 再使用$设置参数时，mybatis会创建普通的mysql语句，然后再执行时将参数拼入SQL，可能会导致注入攻击
- 在使用#设置参数时mybatis会设置预编译的sql语句，然后在执行时会为预编译的sql语句赋值。
## mybatis的缓存机制
# redis
- redis是基于键值对的非关系型数据库，
## redis 的数据结构
- 字符串
- 哈希
- 列表
- 集合
- 有序集合
## redis的持久化策略
- RDB持久化，快照。redis会创建一个经过压缩的二进制文件储存当前数据库的内容。有自动触发和手动触发两种方式。
- AOF持久化，AOF以独立日志的形式记录了每行的日志，重启时再执行AOF日志中的内容来恢复数据。
- 混合持久化，结合以上两种方式。
## redis的主从同步机制
- redis集群，分散查询压力
## redis的缓存淘汰策略
- 惰性删除，用户访问一个key的时候，redis会查询key的过期时间，如果过期就删除。
- 定期删除，redis会将设置了过期时间的key放在一个独立的字典中。并对该字典隔10s进行一次检查，从过期字典中随机选取20个key，删除其中过期的key，如果删除的比例超过了25%，重复此步骤。
- 但缓存的数据超过最大容量是，redis会使用LRU（最近最少未使用）进行删除。
## 缓存穿透、缓存击穿、缓存雪崩
- 缓存穿透是指redis查询了根本不存在的数据，导致大量查询直达存储层导致负载过大。可以通过缓存空值和布隆过滤器还预防。
- 缓存击穿是大量访问的热点数据缓存失效的瞬间，大量访问直达存储层，导致服务崩溃。可以物理设置永不过期解决，或者对访问储存层的数据加锁，一次只能有一个线程访问该数据。
- 缓存雪崩是指缓存层失效指当某一时刻缓存层无法继续提供服务,导致所有的请求直达存储层,造成数据库宕机。可能是缓存中有大量数据同时过期,也可能是Redis节点发生故障,导致大量请求无法得到处理。 缓存雪崩的解决方式有三种；第一种是在设置过期时间时,附加一个随机数,避免大量的key同时过期。第二种是启用降级和熔断措施,即发生雪崩时,若应用访问的不是核心数据,则直接返回预定义信息/空值/错误信息。或者在发生雪崩时,对于访问缓存接口的请求,客户端并不会把请求发给Redis,而是直接返回。第三种是构建高可用的Redis服务,也就是采用哨兵或集群模式,部署多个Redis实例,这样即使个别节点宕机,依然可以保持服务的整体可用。
## redis与数据库保持双写一致性。
- 先redis再数据库，优点是命中率高，数据也是最新的，缺点是影响redis性能。
- 先数据库再redis，可能会导致数据不一致
- 先删除redis再更新数据库，高并发环境下可能导致删除redis失败。
- 先更新数据库再删除redis，redis删除失败时可能会导致数据不一致。最好的方案，失败就重试。
## 如何实现redis高可用
- redis集群，多个redis服务器，分为主节点和从节点，主节点首先会发送快照给从节点，从节点执行快照保持和主节点一致的数据。修改只会在主节点，主节点修改后会将命令同步给从节点。如果网络断开还会执行增量复制。
- 哨兵模式是为了防止主节点挂了，没有节点执行写操作了。哨兵主要做三个功能，监控，ping所有主从节点，判断是否存活。如果没有在规定时间内回复就标记为主观下线。哨兵也不止一个，目的是为了防止网络不好误判的情况。哨兵集群通过投票，判断有没有下线。
