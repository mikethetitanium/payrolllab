# Compiler Configuration Fix

## Issue
Error: `Name for argument of type [java.lang.String] not specified, and parameter name information not available via reflection. Ensure that the compiler uses the '-parameters' flag.`

## Root Cause
The Maven compiler wasn't configured to preserve parameter names at compile time. Spring needs this information to map request parameters to method arguments.

## Solution Applied

### Updated pom.xml
Added the following to the maven-compiler-plugin configuration:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>17</source>
        <target>17</target>
        <parameters>true</parameters>  <!-- This is the key fix -->
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## What This Does

- `<source>17</source>` - Compile source code as Java 17
- `<target>17</target>` - Target Java 17 bytecode
- `<parameters>true</parameters>` - **Preserves parameter names in compiled bytecode** (this is the fix!)

## How to Apply

### Step 1: Clean Previous Build
```bash
./mvnw clean
```

This removes the old compiled classes that don't have parameter information.

### Step 2: Rebuild
```bash
./mvnw install
```

Or just run the application:
```bash
./mvnw spring-boot:run
```

Maven will automatically recompile with the new configuration.

## Verification

After rebuilding, you should see:
```
[INFO] Building jar: target/payroll-reporting-lab-0.0.1-SNAPSHOT.jar
[INFO] BUILD SUCCESS
```

Then run the application and test:
1. Open: `http://localhost:8080/reports/payroll-summary`
2. Apply filters: `?country=Kenya&payrollMonth=2025-01`
3. Should work without errors ✅

## Why This Matters

When you use `@RequestParam` without explicit names:
```java
@RequestParam(required = false) String country
```

Spring needs to know the parameter name is "country". Without the `-parameters` flag, the compiled bytecode loses this information, and Spring can't map the request parameter to the method argument.

With `-parameters=true`, the compiler includes parameter names in the bytecode, so Spring can find them.

## Alternative (Not Recommended)

If you don't want to use `-parameters`, you'd need to explicitly name every parameter:
```java
@RequestParam(name = "country", required = false) String country
@RequestParam(name = "payrollMonth", required = false) String payrollMonth
@RequestParam(name = "department", required = false) String department
```

But using `-parameters=true` is cleaner and is the Spring Boot best practice.

## Files Modified

- `pom.xml` - Added compiler configuration

## Testing Checklist

- [ ] Run `./mvnw clean`
- [ ] Run `./mvnw spring-boot:run`
- [ ] Wait for "Data initialization complete!"
- [ ] Open `http://localhost:8080`
- [ ] Dashboard loads ✅
- [ ] Open `http://localhost:8080/reports/payroll-summary`
- [ ] Report loads ✅
- [ ] Apply filters: `?country=Kenya`
- [ ] Filtered report loads ✅
- [ ] Apply multiple filters: `?country=Kenya&payrollMonth=2025-01&department=Engineering`
- [ ] Multi-filter report loads ✅
- [ ] Export to Excel ✅
- [ ] Export to CSV ✅
- [ ] Validation works ✅

## Common Issues

### Issue: Still getting the same error
**Solution**:
1. Make sure you ran `./mvnw clean` first
2. Delete the `target` folder manually: `rm -rf target`
3. Run `./mvnw spring-boot:run` again

### Issue: Build takes longer
**Solution**: This is normal - the first build after adding `-parameters` will take slightly longer as it recompiles everything with the new flag.

### Issue: IDE shows errors
**Solution**: 
- Refresh Maven project in your IDE
- Rebuild the project
- Restart the IDE if needed

## Reference

- [Maven Compiler Plugin Documentation](https://maven.apache.org/plugins/maven-compiler-plugin/)
- [Spring Boot Parameter Names](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/arguments.html)
- [Java Reflection Parameter Names](https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html)

---

**Status**: ✅ FIXED

**Next Step**: Run `./mvnw clean spring-boot:run`
