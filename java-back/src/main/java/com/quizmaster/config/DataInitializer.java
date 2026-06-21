package com.quizmaster.config;

import com.quizmaster.entity.Category;
import com.quizmaster.entity.User;
import com.quizmaster.mapper.CategoryMapper;
import com.quizmaster.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initAdmin();
        initCategories();
    }

    private void initAdmin() {
        User admin = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                .eq("username", "admin"));

        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userMapper.insert(admin);
        } else if (!passwordEncoder.matches("admin123", admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userMapper.updateById(admin);
        }

        if (admin.getRole() == null || !admin.getRole().equals("ADMIN")) {
            admin.setRole("ADMIN");
            userMapper.updateById(admin);
        }
    }

    private void initCategories() {
        long count = categoryMapper.selectCount(null);
        if (count == 0) {
            List<Category> categories = Arrays.asList(
                    createCategory("政治", "🏛️", 0),
                    createCategory("历史", "📜", 0),
                    createCategory("地理", "🌍", 0),
                    createCategory("法律", "⚖️", 0),
                    createCategory("经济", "💰", 0),
                    createCategory("科技", "🔬", 0)
            );
            categories.forEach(categoryMapper::insert);
        }
    }

    private Category createCategory(String name, String icon, int count) {
        Category category = new Category();
        category.setName(name);
        category.setIcon(icon);
        category.setQuestionCount(count);
        return category;
    }
}