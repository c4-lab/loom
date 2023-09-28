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
        properties {
            hibernateInterceptor = new HibernateFlushInterceptor()
        }

    }

    // environment specific settings
    environments {
        development {
            dataSource {
                //logSql = true
                dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
                //dialect = "edu.msu.mi.loom.utils.ImprovedH2Dialect"
                url = "jdbc:mysql://localhost:3306/loom"
                driverClassName = "org.mariadb.jdbc.Driver"//"com.mysql.cj.jdbc.Driver"//"com.mysql.jdbc.Driver"
                dbCreate = "create"
                username = "loom"
                password = "loom"
                validationQuery = "SELECT 1"
                testOnBorrow = true
                testWhileIdle = true
                timeBetweenEvictionRunsMillis = 3 * 60 * 60 * 1000 // 3 hours in milliseconds
                removeAbandoned = true
                removeAbandonedTimeout = 3600 // 1 hour

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
                dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
                url = "jdbc:mysql://localhost:3306/loom"
                driverClassName = "org.mariadb.jdbc.Driver"//"com.mysql.cj.jdbc.Driver"//"com.mysql.jdbc.Driver"
                dbCreate = "update"
                username = "loom"
                password = "loom"
            }
        }
    }
