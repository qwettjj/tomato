package com.example.tomatomall.controller;

import com.alipay.api.domain.Advert;
import com.example.tomatomall.repository.AdvertisementReposity;
import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.vo.AdvertisementVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    @Autowired
    AdvertisementService advertisementService;

    @Autowired
    AdvertisementReposity advertisementReposity;

    @GetMapping
    Response<List<AdvertisementVO>> getAdvertisements() {
        return Response.buildSuccess(advertisementService.getAllAdvertisements());
    }

    @PostMapping
    Response<Boolean> addAdvertisement(@RequestBody AdvertisementVO advertisementVO) {
        return Response.buildSuccess(advertisementService.createAdvertisement(advertisementVO));
    }

    @PutMapping
    Response<Boolean> updateAdvertisement(@RequestBody AdvertisementVO advertisementVO) {
        return Response.buildSuccess(advertisementService.updateAdvertisement(advertisementVO));
    }

    @DeleteMapping
    Response<Boolean> deleteAdvertisement(@RequestParam("id") Integer id) {
        if(!advertisementReposity.existsById(id)){
            return Response.buildFailure("订单不存在","404");
        }
        advertisementReposity.deleteById(id);
        return Response.buildSuccess(true);
    }


}
