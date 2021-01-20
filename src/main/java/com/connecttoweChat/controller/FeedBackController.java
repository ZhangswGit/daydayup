package com.connecttoweChat.controller;

import com.connecttoweChat.bean.FeedBack;
import com.connecttoweChat.bean.User;
import com.connecttoweChat.constants.AuthConstant;
import com.connecttoweChat.constants.ErrorAlertMessages;
import com.connecttoweChat.error.BadRequestException;
import com.connecttoweChat.mapper.FeedBackMapper;
import com.connecttoweChat.mapper.UserMapper;
import com.connecttoweChat.model.request.CreateFeedbackRequest;
import com.connecttoweChat.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user/v1/feedback")
public class FeedBackController {

    @Autowired
    private FeedBackService feedBackService;

    @Autowired
    private FeedBackMapper feedBackMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<FeedBack>> findAllFeedback(HttpServletRequest request){
        List<FeedBack> allFeedBack = feedBackMapper.findAllFeedBack();
        return ResponseEntity.ok(allFeedBack);
    }
    @PostMapping("/create")
    public ResponseEntity<FeedBack> createFeedback(HttpServletRequest request, @Valid CreateFeedbackRequest createFeedbackRequest, @RequestParam(value = "files", required = false) MultipartFile[] files) throws Exception {
        FeedBack feedback = feedBackService.createFeedback(createFeedbackRequest, files);
        return ResponseEntity.ok(feedback);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedBack> createFeedback(HttpServletRequest request){
        FeedBack feedback = feedBackService.updateFeedback();
        return ResponseEntity.ok(feedback);
    }
}
