package com.github.peacetrue.spring.beans;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.*;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * {@link BeanUtils} 测试类
 *
 * @author peace
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BeanUtilsTest {

    private static final EasyRandomParameters PARAMETERS = new EasyRandomParameters()
            .seed(System.currentTimeMillis())
            .objectPoolSize(100)
            .randomizationDepth(3)
            .charset(StandardCharsets.UTF_8)
            .stringLengthRange(5, 50)
            .collectionSizeRange(1, 10)
            .scanClasspathForConcreteTypes(true)
            .overrideDefaultInitialization(false)
            .ignoreRandomizationErrors(true);

    private static final EasyRandom EASY_RANDOM = new EasyRandom(PARAMETERS);
    private static final int SIZE = 10;

    /** 查询 */
    @Order(0)
    @Test
    //tag::query[]
    void query() {
        //模拟从数据库查询出的实体列表
        List<User> entities = EASY_RANDOM.objects(User.class, SIZE).collect(Collectors.toList());
        //实体列表转换成 VO 列表
        List<UserVO> vos = BeanUtils.convert(entities, UserVO.class);
        Assertions.assertEquals(entities.size(), vos.size());
        //随机挑选一个，判断相等
        int index = EASY_RANDOM.nextInt(SIZE);
        User entity = entities.get(index);
        UserVO vo = vos.get(index);
        Assertions.assertEquals(entity.getId(), vo.getId());
        Assertions.assertEquals(entity.getName(), vo.getName());
        Assertions.assertEquals(entity.getPassword(), vo.getPassword());
        Assertions.assertNull(vo.getRole(), "实体类中无此属性，vo 中该属性为 null");

        //如果临时使用，不想声明 VO 类，直接使用 Map，不影响接口返回信息
        List<Map<String, Object>> mapBeans = BeanUtils.getPropertyValues(entities);
        Map<String, Object> mapBean = mapBeans.get(index);
        Assertions.assertEquals(entity.getId(), mapBean.get("id"));
        Assertions.assertEquals(entity.getName(), mapBean.get("name"));
        Assertions.assertEquals(entity.getPassword(), mapBean.get("password"));

        //设置关联
        //查询用户关联的角色对象，需要先取出用户主键集合
        List<Long> ids = BeanUtils.getPropertyValues(vos, "id");
        Assertions.assertEquals(vos.size(), ids.size());
        Assertions.assertEquals(ids.get(index), vos.get(index).getId());
        //根据用户主键集合查询出关联的角色列表
        //模拟从数据库查询出的角色列表
        List<Role> roles = EASY_RANDOM.objects(Role.class, SIZE).collect(Collectors.toList());
        //绑定好角色关联的用户主键
        IntStream.range(0, roles.size()).forEach(i -> roles.get(i).setUserId(ids.get(i)));

        //如果用户和角色是 1对1 关联
        //将角色列表转换成以用户主键为 key，角色为 value 的 Map
        Map<Long, Role> roleMap = BeanUtils.convert(roles, "userId");
        roleMap.forEach((key, value) -> Assertions.assertEquals(key, value.getUserId()));
        //设置用户关联的角色
        vos.forEach(user -> user.setRole(roleMap.get(user.getId())));

        //如果用户和角色是 1对多 关联
        //将角色列表转换成以用户主键为 key，角色列表为 value 的 Map
        Map<Long, List<Role>> groupedRoles = BeanUtils.groupingBy(roles, "userId");
        groupedRoles.forEach((key, values) -> values.forEach(value -> Assertions.assertEquals(key, value.getUserId())));
        //设置用户关联的角色列表
        vos.forEach(user -> user.setRoles(groupedRoles.get(user.getId())));

        //如果用户和角色是 1对多 关联，且只想关联角色名称
        //将角色列表转换成以用户主键为 key，角色名称列表为 value 的 Map
        Map<Long, List<String>> groupedRoleNames = BeanUtils.groupingBy(roles, "userId", "name");
        //设置用户关联的角色列表
        vos.forEach(user -> user.setRoleNames(groupedRoleNames.get(user.getId())));
    }
    //end::query[]

    /** 新增场景 */
    @Order(10)
    @Test
    //tag::insert[]
    void insert() {
        //模拟从前端传入的 DTO 对象
        UserDTO dto = EASY_RANDOM.nextObject(UserDTO.class);
        //DTO 对象转换成实体类
        User entity = BeanUtils.convert(dto, User.class);
        Assertions.assertEquals(dto.getId(), entity.getId());
        Assertions.assertEquals(dto.getName(), entity.getName());
        Assertions.assertEquals(dto.getPassword(), entity.getPassword());
        //设置默认值，如果 entity 中某些属性可以为空，这里为其设置默认值
        entity.setPassword(null);
        User defaults = EASY_RANDOM.nextObject(User.class);
        BeanUtils.setDefaults(entity, defaults);
        Assertions.assertEquals(defaults.getPassword(), entity.getPassword());
        Assertions.assertNotEquals(defaults.getId(), entity.getId());
        Assertions.assertNotEquals(defaults.getName(), entity.getName());
    }
    //end::insert[]

    /** 全量更新，除了个别不可修改的属性，其他属性全部更新，不管传入的属性是否有值 */
    @Order(20)
    @Test
    //tag::fullUpdate[]
    void fullUpdate() {
        //模拟从前端传入的 DTO 对象
        UserDTO dto = EASY_RANDOM.nextObject(UserDTO.class);
        //模拟从后台查出的实体对象
        User entity = EASY_RANDOM.nextObject(User.class);
        assertNotEquals(dto, entity);
        //将 password 设置为 null，测试空值也会覆盖
        dto.setPassword(null);
        //拷贝 DTO 对象中指定属性到实体对象
        //通过包含判断，包含可修改的属性，适用于更新属性较少的场景
        BeanUtils.copyPropertiesInclude(dto, entity, Arrays.asList("name", "password"));
        Assertions.assertNotEquals(dto.getId(), entity.getId(), "id 不同，因为没有拷贝此属性");
        Assertions.assertEquals(dto.getName(), entity.getName(), "name 相同，因为拷贝了此属性");
        Assertions.assertEquals(dto.getPassword(), entity.getPassword(), "password 相同，因为拷贝了此属性");

        entity = EASY_RANDOM.nextObject(User.class);
        assertNotEquals(dto, entity);
        //拷贝 DTO 对象中指定属性到实体对象
        //通过排除判断，排除不可修改的属性，适用于更新属性较多的场景
        BeanUtils.copyPropertiesExclude(dto, entity, Arrays.asList("id", "creatorId"));
        Assertions.assertNotEquals(dto.getId(), entity.getId(), "id 不同，因为没有拷贝此属性");
        Assertions.assertEquals(dto.getName(), entity.getName(), "name 相同，因为拷贝了此属性");
        Assertions.assertEquals(dto.getPassword(), entity.getPassword(), "password 相同，因为拷贝了此属性");
    }
    //end::fullUpdate[]

    /** DTO 对象和实体对象的所有属性都不同 */
    private void assertNotEquals(UserDTO dto, User entity) {
        Assertions.assertNotEquals(dto.getId(), entity.getId());
        Assertions.assertNotEquals(dto.getName(), entity.getName());
        Assertions.assertNotEquals(dto.getPassword(), entity.getPassword());
    }

    /** 局部更新，更新部分有值的属性，不处理无值的属性 */
    @Order(30)
    @Test
    //tag::partUpdate[]
    void partUpdate() {
        //模拟从前端传入的 DTO 对象
        UserDTO dto = EASY_RANDOM.nextObject(UserDTO.class);
        //模拟从后台查出的实体对象
        User entity = EASY_RANDOM.nextObject(User.class);
        // DTO 对象和实体对象属性不同
        assertNotEquals(dto, entity);
        //将 password 设置为 null，测试空值不覆盖
        dto.setPassword(null);
        //拷贝 DTO 对象中指定的非空属性到实体对象
        //通过包含判断，包含可修改的属性，适用于更新属性较少的场景
        BeanUtils.copyPropertiesNotEmptyInclude(dto, entity, Arrays.asList("name", "password"));
        Assertions.assertNotEquals(dto.getId(), entity.getId(), "id 不同，因为没有拷贝此属性");
        Assertions.assertEquals(dto.getName(), entity.getName(), "name 相同，因为拷贝了此属性");
        Assertions.assertNotEquals(dto.getPassword(), entity.getPassword(), "password 不同，因为 DTO 中此属性为空");
        //拷贝 DTO 对象中指定的非空属性到实体对象
        //有值的全部更新，简单粗暴
        //模拟从后台查出的实体对象
        entity = EASY_RANDOM.nextObject(User.class);
        BeanUtils.copyPropertiesNotEmpty(dto, entity);
        Assertions.assertEquals(dto.getId(), entity.getId());
        Assertions.assertEquals(dto.getName(), entity.getName(), "name 相同，因为拷贝了此属性");
        Assertions.assertNotEquals(dto.getPassword(), entity.getPassword(), "password 不同，因为 DTO 中此属性为空");
    }

    //end::partUpdate[]

    //tag::isEmpty[]

    /**
     * 源至 {@link org.springframework.util.ObjectUtils#isEmpty(Object)}，
     * 应该能满足绝大多数使用场景
     */
    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Optional) {
            return !((Optional<?>) obj).isPresent();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }

        // else
        return false;
    }
    //end::isEmpty[]


    @Test
    void sort() {
        PropertyDescriptor[] properties = BeanUtils.getPropertyDescriptors(User.class);
        BeanUtils.sort(properties);
        Assertions.assertArrayEquals(
                new String[]{"class", "id", "name", "password", "creatorId", "createdTime"},
                Arrays.stream(properties).map(PropertyDescriptor::getName).toArray(String[]::new)
        );
    }

    @Test
    void getPropertyValues() {
        User user = new User();
        Map<String, Object> propertyValues = BeanUtils.getPropertyValues(user, true, false);
        Assertions.assertEquals(5, propertyValues.size());
        propertyValues = BeanUtils.getPropertyValues(user, false, true);
        Assertions.assertEquals(0, propertyValues.size());

        user.setId(1L);
        propertyValues = BeanUtils.getPropertyValues(user, true, true);
        Assertions.assertEquals(1, propertyValues.size());
    }

    @Test
    void getPropertyValue() {
        Assertions.assertNull(BeanUtils.getPropertyValue(new Object(), "notExists"));
        Assertions.assertNull(BeanUtils.getPropertyValue(new Object() {
            public void setName(String name) {
            }
        }, "name"));
        Assertions.assertNotNull(BeanUtils.getPropertyValue(new Object(), "class"));
    }

    @Test
    void setPropertyValue() {
        long id = 1L;
        User user = new User();

        BeanUtils.setPropertyValue(user, "notExists", id);
        Assertions.assertNull(user.getId());

        BeanUtils.setPropertyValue(user, "class", id);
        Assertions.assertNull(user.getId());

        BeanUtils.setPropertyValue(user, "id", id);
        Assertions.assertEquals(id, user.getId());
    }

    @Test
    void foreachProperties() {
        BeanUtils.foreachProperties(new Object() {
            public void setName(String name) {
            }
        }, propertyDescriptor -> Assertions.fail());

        BeanUtils.foreachProperties(new Object() {
            public void setName(String name) {
            }
        }, new Object(), (source, target) -> Assertions.fail());

        BeanUtils.foreachProperties(new Object(), new Object() {
            public String getName() {
                return null;
            }
        }, (source, target) -> Assertions.fail("writeMethod == null"));

        BeanUtils.foreachProperties(new Object() {
            public void setName(String name) {
            }
        }, new Object() {
            public void setName(String name) {
            }
        }, (source, target) -> Assertions.fail("readMethod == null"));

        BeanUtils.foreachProperties(new Object() {
            public Integer getName() {
                return null;
            }
        }, new Object() {
            public void setName(String name) {
            }
        }, (source, target) -> Assertions.fail("getParameterTypes != getReturnType"));
    }

    @Test
    void copyProperties() {
        User source = EASY_RANDOM.nextObject(User.class), target = EASY_RANDOM.nextObject(User.class);
        Assertions.assertNotEquals(source.getId(), target.getId());
        Assertions.assertNotEquals(source.getName(), target.getName());

        BeanUtils.copyProperties(source, target, property -> property.getName().equals("name"), null);
        Assertions.assertNotEquals(source.getId(), target.getId());
        Assertions.assertEquals(source.getName(), target.getName());

        BeanUtils.copyProperties(source, target, null, null);
        Assertions.assertEquals(source.getId(), target.getId());
        Assertions.assertEquals(source.getName(), target.getName());

        source.setId(source.getId() + 1);
        source.setPassword(source.getPassword() + "1");
        BeanUtils.copyPropertiesNotEmptyExclude(source, target, Arrays.asList("id", "name"));
        Assertions.assertNotEquals(source.getId(), target.getId());
        Assertions.assertEquals(source.getPassword(), target.getPassword());
    }

    @Test
    void setDefaults() {
        User source = EASY_RANDOM.nextObject(User.class), target = EASY_RANDOM.nextObject(User.class);
        Assertions.assertNotEquals(source.getId(), target.getId());
        Assertions.assertNotEquals(source.getName(), target.getName());

        source.setId(null);
        source.setName(null);
        source.setPassword(null);
        target.setName(null);
        BeanUtils.setDefaults(source, target);
        Assertions.assertEquals(source.getId(), target.getId());
        Assertions.assertEquals(source.getName(), target.getName());
        Assertions.assertEquals(source.getPassword(), target.getPassword());

        BeanUtils.setDefaults(new Object() {
            public void setName(String name) {
            }
        });
        source = new User();
        source.setCreatorId(1L);
        BeanUtils.setDefaults(source);
        Assertions.assertEquals(0L, source.getId());
        Assertions.assertEquals("", source.getName());
        Assertions.assertEquals(1L, source.getCreatorId());

        source = new User();
        BeanUtils.setDefaults(source, (name, type) -> String.class == type ? "value" : null);
        Assertions.assertNull(source.getId());
        Assertions.assertEquals("value", source.getName());

        BeanUtils.setDefaults(new TestBean());
    }

    @Test
    void convert() {
        List<User> users = EASY_RANDOM.objects(User.class, 10).collect(Collectors.toList());
        Map<Long, String> names = BeanUtils.convert(users, "id", "name");
        Assertions.assertEquals(users.size(), names.size());
        users.forEach(user -> Assertions.assertEquals(user.getName(), names.get(user.getId())));
    }
}
