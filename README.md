# Cup, 一个ORM框架，开发日志

![cup](cup.png)

### 零，GOOD LUCK

### 一，配置文件读取

cup需要一个可以读取配置文件的方法，他可以通过流的方式根据传入配置信息拿到cup需要的信息，这些信息包括数据库连接信息，环境信息，映射文件位置

eg：

~~~yml
cup:
  environment: development
  datasource:
    driver: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/localmy?characterEncoding=UTF-8&useUnicode=true&serverTimezone=UTC&useSSL=true
    username: root
    password: '123456'
  mapperpath: classpath:mapper/*.yml
~~~

> 注意：cup只会读取yml文件形式的流数据

> 注意：如果是全数字形式需要加 '' 单引号

cup读取这些文件后会把它们解析成环境信息

包 `com.qlu.cup.builder` 是用来存放配置文件和映射文件解析和构建过程的地方

cup拿到流数据后会把配置文件解析到Environment中，之后会通过这个Environment创建Configuration

Configuration创建完成后cup会对Configuration中的数据进行完善，包括其中的Ynode节点信息和mapper映射信息等

### 二，事务工厂

考虑到数据库的数据一致性问题，cup提供了一个默认的事务和事务工厂，它是自动提交的，我们认为这个样子更利于管理。

cup任务每一个sql的执行都是执行一次事务，所以回滚操作只能回滚一条SQL语句

其余事务操作由数据库或其他框架完成

### 三，参数映射

为了更友好的隐藏应用程序和持久层的关系，cup提供了参数映射机制，为了减少BoundSQL和参数映射之间的耦合关系，cup将专门提供一个类用来存放参数映射信息，并将这个类的map集合在Configuration存放一份。

参数映射：

- clazz：类对象
- value：参数名(和后面的映射文件对应)
- 这是一个Map，放在全局

cup不打算引入参数映射器的概念，映射工作需要由参数映射类和BoundSQL自行解决

因为参数映射类和BoundSQL已经分离，所以我们需要在参数映射类中添加命名空间和对应接口方法来标识

这个Map的key是 命名空间+接口方法

### 四，结果映射

结果映射和参数映射同理，创建结果映射类来和BoundSQL减少耦合程度。

结果映射极有可能是一个实体类，这样我们就需要把类对象单独拿出来，考虑到这样严重占用内存，所以cup采用其他形式存储——字符串，在需要的时候去通过反射创建这个类对象

结果映射

- name：对应实体类或者类型类名
- 实体类：属性Map<Class,name>，name属性名，为了和结果对应(如果结果多条就是List)
- 基本类型/包装类：Map<Class,name>,name 是resultType名
- 这是一个Map，放在全局
- 如果为null，就为int

### 五，实装时机

cup的大部分行为都是围绕着配置类来的，所以cup必须在一开始就初始化完成这个类，并完善类中的信息

cup把这个行为放在了解析配置文件之后

在生成配置类时cup需要考虑配置类需要干些什么额外的事情，比如创建一个全局的执行器，这是必要的

关于返回结果这里需要把接口方法的返回类型和yml映射文件中的 resultType 区分开

接口方法的返回类型需要和BoundSql绑定，仅仅作为展示给用户的需要

yml映射文件中的resultType需要进行一步判断

- 基本数据类型及其包装类或者String，就是单一映射
- 如果是实体类就是全部属性映射
- 以上两者通过类加载器区分

### 六，StatementHandler

真正在执行SQL的地方，还包括语句的准备，在这里cup发现需要对BoundSql中的getSql的方法进行一个特殊的处理。

cup不能只得到程序员在yml映射文件中的原始SQL，StatementHandler需要一个和参数进行匹配好了的SQL

所以cup需要在获取BoundSql中的Sql的时候进行参数的匹配

### 七，结果处理

cup对结果的处理依赖于ResultSet这个数据库的返回类，通过这个类的findColumn方法根据实体类的属性名进行一一匹配

cup还需要知道结果映射信息，所以这里还需要我们定制的BoundSql

cup认为只有拿到实体类属性的set方法，我才可以对这个属性进行赋值，这个方法的规范是：针对xxx属性需要方法为`setXxx(Class xxx);`，如果没有这个方法，即使存在这个属性也会因为是私有的属性无法进行赋值

cup会优先去寻找set方法，如果成功就会直接赋值，失败了才会针队这个属性赋值，如果这个属性是`public`的话才会被cup赋值成功，当然cup并不建议把实体类中的属性设置为 `public`，这样并不安全

### 八，参数处理

cup进行参数处理的目的是把参数和SQL进行绑定

在参数映射器中只给定了参数和传入参数的映射关系，这里是要把参数映射和用户真实传入的参数进行整合，方便在getSql的时候拿到真正的SQL，而不是yml中的原始SQL，但是这个参数不能影响真正的原始SQL

cup要求接口方法中的参数名和在yml文件中使用的相同

cup 不会映射static修饰的属性，如果一个类中存在static修饰的属性，cup会自动忽略这个属性，这个属性不应该属于某一个实例属性，而是应该属于这个类对象，cup无权对类对象进行过多的访问

在获取参数的时候cup希望获取的开发者自己写入的参数名，而不是jvm编译后自动生成的诸如var1，var2之类的参数，所以需要对pom.xml中的信息进行修改：

当我们使用maven的时候通常会在pom中写入这样的信息：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

这是idea自动帮开发人员引入的插件，可以通过这个插件把项目工程进行打包，这个结果可能是jar文件，也有可能是war文件，现在再使用cup的时候希望开发人员可以扩展这个配置：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>8</source>
                <target>8</target>
                <compilerArgs>
                    <arg>-parameters</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

其中最重要的是 `compilerArgs` 这个标签，`-parameters`  属性代表着jvm在编译的时候的编译结果，比如接口方法 `Users getUserById(Integer id);` 这个的编译结果是 `Users getUserById(Integer var1);`，这里的参数名应该是id，但是却被编译成了var1,。

cup不会根据参数对应的位置去设置值，所以如果出现这种情况，cup会赋值失败，甚至会出现错误。

