package com.connecttoweChat.demo.mock;

import com.connecttoweChat.ConnecttoweChatApplication;
import com.connecttoweChat.demo.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConnecttoweChatApplication.class)
public class AbstractBeanTest extends CommonTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

}
