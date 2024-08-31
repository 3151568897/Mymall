package com.example.mymall;


import com.example.mymall.product.MyMallProductApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest(classes = MyMallProductApplication.class)
class MyMallProductApplicationTests {

    @Test
    void contextLoads() {

    }

}
