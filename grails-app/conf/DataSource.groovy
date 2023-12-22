import edu.msu.mi.loom.utils.HibernateFlushInterceptor

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
            pooled = true
            jmxExport = true
            driverClassName = "org.mariadb.jdbc.Driver"
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
            url = "jdbc:mysql://localhost:3306/loom"
            username = "loom"
            password = "loom"
            dbCreate = "update"
            properties {
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 3000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
                logValidationErrors = true
            }

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
            pooled = true
            jmxExport = true
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
            url = "jdbc:mysql://localhost:3306/loom"
            driverClassName = "org.mariadb.jdbc.Driver"//"com.mysql.cj.jdbc.Driver"//"com.mysql.jdbc.Driver"
            dbCreate = "update"
            username = "loom"
            password = "loom"
            properties {
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 3000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
                logValidationErrors = true
            }
        }
    }
}

