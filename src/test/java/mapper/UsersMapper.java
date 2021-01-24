package mapper;

import com.myb.pojo.Person;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author shidacaizi
 * @date 2020/4/13 21:30
 */
public interface UsersMapper {
    List<Person> getUserList();

//    @Select("select * from person where id = #{id}")
    Person getUserById(@Param("id") int id);

//    int addUser(@Param("name") String name, @Param("age") int age);

    int addUser(Person person);

    int addPerson2(Map<String, Object> map);

    int updateUser(@Param("id") int id, @Param("name") String name, @Param("age") int age);

    int deleteUser(@Param("id") int id);
}
