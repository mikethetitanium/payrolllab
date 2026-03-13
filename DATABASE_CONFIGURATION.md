# Database Configuration - Quick Reference

## Switch Databases in 30 Seconds

Edit `src/main/resources/application.properties` and uncomment your database:

### Option 1: H2 (Default - In-Memory)
```properties
# H2 Database (Default - In-Memory)
spring.datasource.url=jdbc:h2:mem:payrolldb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Pros**: Zero setup, fast startup, in-memory  
**Cons**: Data lost on restart  
**Best for**: Learning, testing, demos  
**Access**: `http://localhost:8080/h2-console`

---

### Option 2: SQL Server
```properties
# SQL Server Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=payrolldb;encrypt=true;trustServerCertificate=true
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=sa
spring.datasource.password=YourPassword123!
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
```

**Pros**: Production-ready, persistent, scalable  
**Cons**: Requires installation  
**Best for**: Production, enterprise  
**Setup**: See [SQL_SERVER_SETUP.md](SQL_SERVER_SETUP.md)

---

### Option 3: PostgreSQL
```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/payrolldb
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

**Pros**: Open source, reliable, scalable  
**Cons**: Requires installation  
**Best for**: Production, open source projects  
**Setup**: Install PostgreSQL, create database `payrolldb`

---

## Connection String Reference

### H2
```
jdbc:h2:mem:payrolldb                    (in-memory)
jdbc:h2:file:./payrolldb                 (file-based)
```

### SQL Server
```
jdbc:sqlserver://localhost:1433;databaseName=payrolldb
jdbc:sqlserver://server.domain.com:1433;databaseName=payrolldb
```

### PostgreSQL
```
jdbc:postgresql://localhost:5432/payrolldb
jdbc:postgresql://server.domain.com:5432/payrolldb
```

---

## Hibernate Dialects

| Database | Dialect |
|----------|---------|
| H2 | `org.hibernate.dialect.H2Dialect` |
| SQL Server | `org.hibernate.dialect.SQLServerDialect` |
| PostgreSQL | `org.hibernate.dialect.PostgreSQLDialect` |
| MySQL | `org.hibernate.dialect.MySQL8Dialect` |
| Oracle | `org.hibernate.dialect.Oracle12cDialect` |

---

## Complete Configuration Examples

### H2 (Learning)
```properties
spring.datasource.url=jdbc:h2:mem:payrolldb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
```

### SQL Server (Production)
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=payrolldb;encrypt=true;trustServerCertificate=true
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=sa
spring.datasource.password=YourPassword123!
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.ddl-auto=validate
spring.datasource.hikari.maximum-pool-size=20
```

### PostgreSQL (Production)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/payrolldb
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.datasource.hikari.maximum-pool-size=20
```

---

## DDL Auto Options

| Option | Behavior |
|--------|----------|
| `create-drop` | Create on startup, drop on shutdown (testing) |
| `create` | Create on startup (testing) |
| `update` | Update schema if needed (development) |
| `validate` | Validate schema matches entities (production) |
| `none` | Do nothing (production) |

---

## Quick Setup Checklist

### For H2 (Default)
- [x] Already configured
- [x] No installation needed
- [x] Just run: `./mvnw spring-boot:run`

### For SQL Server
- [ ] Install SQL Server
- [ ] Create database `payrolldb`
- [ ] Update connection string
- [ ] Update username/password
- [ ] Run: `./mvnw spring-boot:run`

### For PostgreSQL
- [ ] Install PostgreSQL
- [ ] Create database `payrolldb`
- [ ] Update connection string
- [ ] Update username/password
- [ ] Run: `./mvnw spring-boot:run`

---

## Troubleshooting

### Connection Failed
1. Verify database is running
2. Check connection string
3. Verify username/password
4. Check firewall/port access

### Wrong Dialect
1. Verify `spring.jpa.database-platform` matches database
2. Check Hibernate dialect table above
3. Restart application

### Schema Mismatch
1. Check `spring.jpa.hibernate.ddl-auto` setting
2. For production, use `validate` or `none`
3. For development, use `update` or `create-drop`

### Performance Issues
1. Add connection pooling:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

2. Add indexes to database
3. Check query performance in logs

---

## Environment-Specific Configurations

### Development (H2)
```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

### Staging (PostgreSQL)
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=INFO
```

### Production (SQL Server)
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=WARN
spring.datasource.hikari.maximum-pool-size=30
```

---

## Switching Databases

### Step 1: Edit application.properties
Comment out current database, uncomment new one

### Step 2: Restart Application
```bash
./mvnw spring-boot:run
```

### Step 3: Verify Connection
- Check console logs for "Data initialization complete!"
- Access application at `http://localhost:8080`
- Verify data in dashboard

---

## Additional Resources

- **H2 Documentation**: http://www.h2database.com/
- **SQL Server Documentation**: https://learn.microsoft.com/en-us/sql/
- **PostgreSQL Documentation**: https://www.postgresql.org/docs/
- **Hibernate Dialects**: https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html

---

## Support

For database-specific issues:
- **H2**: Check H2 console at `http://localhost:8080/h2-console`
- **SQL Server**: See [SQL_SERVER_SETUP.md](SQL_SERVER_SETUP.md)
- **PostgreSQL**: Check PostgreSQL logs and psql console

For application issues:
- Check application logs
- Review [ARCHITECTURE.md](ARCHITECTURE.md)
- Check [README.md](README.md) troubleshooting section

---

**Choose your database and get started!** 🚀
