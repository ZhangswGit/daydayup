package com.swapServer.demo.mock;

import com.swapServer.model.request.CreateFeedbackRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileInputStream;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FeedBackControllerTest extends AbstractBeanTest {

    @Test
    public void CreateFeedBack() throws Exception {
        String filePath = FILE_BASH_PATH + "license.txt";
        File file = new File(filePath);
        String apiPath = "/user/v1/feedback/create";
        MockMultipartFile licenseFile = new MockMultipartFile("file", "license.txt", "text/plain", new FileInputStream(file));

        CreateFeedbackRequest createFeedbackRequest = new CreateFeedbackRequest();
        createFeedbackRequest.setTitle("标题");
        createFeedbackRequest.setDescription("详细内容");
        getMockMvc().perform(
                MockMvcRequestBuilders.fileUpload(apiPath)
                        .file(licenseFile)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, defaultToken())
                        .content(objectMapper.writeValueAsString(createFeedbackRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void UpdateFeedBack() throws Exception {
        String apiPath = "/user/v1/feedback/1";

        getMockMvc().perform(
                MockMvcRequestBuilders.put(apiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, defaultToken()))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
