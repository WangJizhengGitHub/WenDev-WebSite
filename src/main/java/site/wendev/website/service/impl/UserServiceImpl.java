package site.wendev.website.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import site.wendev.website.dao.UserRepository;
import site.wendev.website.entities.User;
import site.wendev.website.service.UserService;
import site.wendev.website.util.Md5Utils;

import java.util.Date;

/**
 * 用户相关业务逻辑实现类
 *
 * @author jiangwen
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User login(String username, String password) {
        // 把密码MD5之后再去查询
        password = Md5Utils.encode(password);
        return userRepository.findByUsernameAndPassword(username, password);
    }

    @Override
    public User register(User user) {
        // 根据注册的用户名查找是否有已经用此用户名注册的用户
        User user1 = userRepository.findByUsername(user.getUsername());

        // 用户名已存在
        if (user1 != null) {
            return null;
        } else {
            // 把密码用MD5加密
            var password = Md5Utils.encode(user.getPassword());

            // 设置用户的权限、注册时间、加密后密码
            user.setRole("ROLE_USER").setCreateTime(new Date()).setUpdateTime(new Date())
                    .setPassword(password);

            // 向数据库中插入这个用户
            userRepository.save(user);
            return user;
        }
    }

    @Override
    public User modify(Long id, User user) {
        var userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return null;
        } else {
            var newUser = userOptional.get();
            newUser.setUsername(user.getUsername());
            newUser.setNickname(user.getNickname());
            newUser.setEmail(user.getEmail());
            return userRepository.save(newUser);
        }
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Page<User> list(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(User user) {
        return userRepository.findByUsername(user.getUsername());
    }
}
