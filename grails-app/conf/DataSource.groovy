dataSource {
    pooled = true
    jmxExport = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
//    cache.region.factory_class = 'org.hibernate.cache.SingletonEhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {
    development {
        dataSource {
            //logSql = true
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
            url = "jdbc:mysql://localhost:3307/loom"
            driverClassName = "org.mariadb.jdbc.Driver"//"com.mysql.cj.jdbc.Driver"//"com.mysql.jdbc.Driver"
            dbCreate = "create-drop"
            username = "root"
            password = "root"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
        dataSource {
            url = "jdbc:mysql://localhost/loom"
            driverClassName = "com.mysql.jdbc.Driver"
            dbCreate = "create"
            username = "loom"
            password = "loom"
        }
    }
}
