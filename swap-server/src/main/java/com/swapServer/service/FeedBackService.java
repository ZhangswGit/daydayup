package com.swapServer.service;

import com.swapServer.bean.FeedBack;
import com.swapServer.constants.FeedbackType;
import com.swapServer.mapper.FeedBackMapper;
import com.swapServer.mapper.UserMapper;
import com.swapServer.model.request.CreateFeedbackRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FeedBackService {

    @Autowired
    FeedBackMapper feedBackMapper;

    @Autowired
    UserMapper userMapper;

    public FeedBack createFeedback(CreateFeedbackRequest createFeedbackRequest, MultipartFile[] files) throws Exception {
        FeedBack feedback = FeedBack.builder()
                .description(StringUtils.isEmpty(createFeedbackRequest.getDescription()) ? "" : createFeedbackRequest.getDescription())
                .feedbackType(FeedbackType.NotDealt)
                .screenShots("")
                .title(createFeedbackRequest.getTitle())
                .build();
        feedBackMapper.insert(feedback);
        return feedback;
    }

    public FeedBack updateFeedback(){
        FeedBack feedback = FeedBack.builder()
                .description("")
                .feedbackType(FeedbackType.NotDealt)
                .screenShots("")
                .title("")
                .build();
        feedBackMapper.insert(feedback);
        return feedback;
    }
}
