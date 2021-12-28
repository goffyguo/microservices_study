package com.goffy.others.ddd;

import javax.xml.bind.ValidationException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/28/14:09
 * @Description: 一个新应用在全国通过 地推业务员 做推广，需要做一个用户注册系统，
 * 同时希望在用户注册后能够通过用户电话（先假设仅限座机）的地域（区号）对业务员发奖金。
 */
public class RegistrationServiceImpl implements RegistrationService {

    private SalesRepRepository salesRepRepository;

    private UserRepository userRepository;

    /**
     * 1、接口清晰度
     * 在Java代码中，对于一个方法来说所有的参数名在编译时丢失，留下的仅仅是一个参数类型的列表，
     * 所以我们重新看一下以上的接口定义，其实在运行时仅仅是：User register(String, String, String);
     * 2、数据验证和错误处理
     * 业务逻辑异常和数据校验异常被混在了一起，是否是合理的
     * 3、业务代码的清晰度
     * String areaCode = null;
     *         String[] areas = new String[]{"0571", "021", "010"};
     *         for (int i = 0; i < phone.length(); i++) {
     *             String prefix = phone.substring(0, i);
     *             if (Arrays.asList(areas).contains(prefix)) {
     *                 areaCode = prefix;
     *                 break;
     *             }
     *         }
     *  出现了另外一种常见的情况，那就是从一些入参里抽取一部分数据，
     *  然后调用一个外部依赖获取更多的数据，然后通常从新的数据中再抽取部分数据用作其他的作用。
     *  这种代码通常被称作“胶水代码”，其本质是由于外部依赖的服务的入参并不符合我们原始的入参导致的。
     *  4、可测试性
     *  假如一个方法有 N 个参数，每个参数有 M 个校验逻辑，至少要有 N * M  个 TC  。
     *  如果这时候在该方法中加入一个新的入参字段 fax ，即使 fax 和 phone 的校验逻辑完全一致，
     *  为了保证 TC 覆盖率，也一样需要 M 个新的 TC。
     *  而假设有 P 个方法中都用到了 phone 这个字段，这 P 个方法都需要对该字段进行测试，也就是说整体需要：P * N * M 个测试用例才能完全覆盖所有数据验证的问题，
     *  在日常项目中，这个测试的成本非常之高，导致大量的代码没被覆盖到。而没被测试覆盖到的代码才是最有可能出现问题的地方
     * @param name
     * @param phone
     * @param address
     * @return
     * @throws ValidationException
     * 1、将隐形的概念显性化
     * 原来电话号仅仅是用户的一个参数，属于隐形概念，但实际上电话号的区号才是真正的业务逻辑，
     * 而我们需要将电话号的概念显性化，通过写一个Value Object：PhoneNumber
     */
    @Override
    public User register(Name name, PhoneNumber phone, Address address) throws ValidationException {
        /*// 检验逻辑
        if (name == null || name.length() == 0) {
            throw new ValidationException("name");
        }
        if (phone == null || isValidPhoneNumber(phone)){
            throw new ValidationException("phone");
        }*/
        // 省略address的检验
        // 取电话号里的区号，然后通过区号找到区域内的SalesRep
        /*String areaCode = null;
        String[] areas = new String[]{"0571", "021", "010"};
        for (int i = 0; i < phone.length(); i++) {
            String prefix = phone.substring(0, i);
            if (Arrays.asList(areas).contains(prefix)) {
                areaCode = prefix;
                break;
            }
        }*/
        //Name name1 = new Name(name);
        SalesRep rep = salesRepRepository.findRep(phone.getAreaCode());
        // 最后创建用户，落盘，然后返回
        User user = new User();
        user.name = name;
        user.phone = phone;
        user.address = address;
        if (rep != null) {
            user.repId = rep.repId;
        }
        return userRepository.save(user);
    }

    private boolean isValidPhoneNumber(String phone) {
        String pattern = "^0[1-9]{2,3}-?\\d{8}$";
        return phone.matches(pattern);
    }
}
