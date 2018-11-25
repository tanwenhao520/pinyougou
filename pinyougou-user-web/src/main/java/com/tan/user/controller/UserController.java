package com.tan.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tan.common.PhoneFormatCheckUtils;
import com.tan.pojo.TbUser;
import com.tan.user.service.UserService;
import com.tan.vo.PageResult;
import com.tan.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference
    private UserService userService;



    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return userService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbUser user,String smsCode) {
        try {
            if(PhoneFormatCheckUtils.isChinaPhoneLegal(user.getPhone())){
                if(userService.checkSmsCode(user.getPhone(),smsCode)){
                    user.setCreated(new Date());
                    user.setUpdated(user.getCreated());
                    user.setPassword(DigestUtils.md5Hex(user.getPassword()));
                    userService.add(user);
                    return Result.ok("注册成功！");
                }else{
                    return Result.fail("验证码错误;注册失败！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            userService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param user 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbUser user, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return userService.search(page, rows, user);
    }

    @GetMapping("/sendSmsCode")
    public Result sendSmsCode(String phone){
        try {
            if(PhoneFormatCheckUtils.isChinaPhoneLegal(phone)){
                userService.sendSmsCode(phone);
                return Result.ok("发送验证码成功！");
            }else{
                return Result.fail("手机号码格式有误，发送验证码失败！");
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        return Result.fail("发送验证码失败！");
    }

    @GetMapping("/getUsername")
    public Map<String,Object> getUserName(){
        Map<String,Object> map = new HashMap<>();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",name);
        return map;
    }

}
