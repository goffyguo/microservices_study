package com.goffy.others.ddd;

import javax.xml.bind.ValidationException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/28/14:32
 * @Description:
 * 1、通过 private final String number 确保 PhoneNumber 是一个（Immutable）Value Object。
 * （一般来说 VO 都是 Immutable 的，这里只是重点强调一下）
 * 2、校验逻辑都放在了 constructor 里面，确保只要 PhoneNumber 类被创建出来后，一定是校验通过的。
 * 3、之前的 findAreaCode 方法变成了 PhoneNumber 类里的 getAreaCode ，
 * 突出了 areaCode 是  PhoneNumber 的一个计算属性。
 *
 * 把 PhoneNumber 显性化之后，其实是生成了一个 Type（数据类型）和一个 Class（类）
 * 1、Type 指我们在今后的代码里可以通过 PhoneNumber 去显性的标识电话号这个概念
 * 2、Class 指我们可以把所有跟电话号相关的逻辑完整的收集到一个文件里
 * 这两个概念加起来，构造成了本文标题的 Domain Primitive（DP）。
 */
public class PhoneNumber {

    private final String number;

    public PhoneNumber(String number) throws ValidationException {
        if (number==null){
            throw new ValidationException("number不能为空");
        }else if (isValid(number)) {
            throw new ValidationException("number格式错误");
        }
        this.number = number;
    }

    public String getAreaCode() {
        for (int i = 0; i < number.length(); i++) {
            String prefix = number.substring(0, i);
            if (isAreaCode(prefix)) {
                return prefix;
            }
        }
        return null;
    }

    private boolean isAreaCode(String prefix) {
        String[] areas = new String[]{"0571", "021", "010"};
        return Arrays.asList(areas).contains(prefix);
    }

    private boolean isValid(String number) {
        String pattern = "^0?[1-9]{2,3}-?\\d{8}$";
        return number.matches(pattern);
    }
}
