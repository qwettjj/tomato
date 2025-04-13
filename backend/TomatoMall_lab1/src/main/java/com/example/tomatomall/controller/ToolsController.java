
package com.example.tomatomall.controller;


import com.example.tomatomall.service.ImageService;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ToolsController {
    @Autowired
    ImageService imageService;

    @PostMapping("/images")
    public Response<String> upload(@RequestParam MultipartFile file) {
        return Response.buildSuccess(imageService.upload(file));
    }
}

