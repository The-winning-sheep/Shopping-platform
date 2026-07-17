package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public void updatePassword(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException("用户不存在");
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException("原密码错误");
        }
        dbUser.setPassword(account.getNewPassword());
        userMapper.updateById(dbUser);
    }

    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize,String name){
        PageHelper.startPage(pageNum,pageSize);
        List<User> list = userMapper.selectAll(name);
        return PageInfo.of(list);
    }


    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    public void add(User user) {
        String username = user.getUsername();
        //校验账户是否重复
        User dbUser= userMapper.selectByUsername(username);
        if(dbUser!=null){
            throw new CustomException("账号重复!新增失败！");
        }
        if(user.getPassword()==null||user.getPassword().equals("")){
            user.setPassword("123");
        }
        if(user.getName()==null||user.getName().equals("")){
            user.setName(user.getUsername());
        }
        user.setRole("普通用户");
        user.setAccount(BigDecimal.ZERO);
        userMapper.insert(user);
    }

    public void update(User user) {
        userMapper.updateById(user);
    }

    public Account login(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if(ObjectUtil.isNull(dbUser)){
            throw new CustomException("用户不存在");
        }
        if(!account.getPassword().equals(dbUser.getPassword())){
            throw new CustomException("账号或者密码错误");
        }
        return dbUser;
    }

    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    public List<User> selectAll(String name) {
        return userMapper.selectAll(name);
    }
}
