package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Advertisement;
import com.example.tomatomall.repository.AdvertisementReposity;
import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.vo.AdvertisementVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    AdvertisementReposity advertisementReposity;

    @Override
    public List<AdvertisementVO> getAllAdvertisements(){
        List<Advertisement> advertisements = advertisementReposity.findAll();
        List<AdvertisementVO> advertisementVOs = new ArrayList<>();
        for(Advertisement advertisement : advertisements){
            AdvertisementVO advertisementVO = advertisement.toVO();
            advertisementVOs.add(advertisementVO);
        }
        return advertisementVOs;
    }

    @Override
    public boolean createAdvertisement(AdvertisementVO advertisementVO) {
        Advertisement advertisement = advertisementVO.toPO();
        advertisementReposity.save(advertisement);
        return true;
    }

    @Override
    public boolean updateAdvertisement(AdvertisementVO advertisementVO) {
        Advertisement advertisement = advertisementVO.toPO();
        Integer Id = advertisement.getAdvertisementId();
        Advertisement advertisement_ =  advertisementReposity.findById(Id).get();
        if(advertisement.getTitle() != null) {
            advertisement_.setTitle(advertisement.getTitle());
        }
        if(advertisement.getContent() != null){
            advertisement_.setContent(advertisement.getContent());
        }
        if(advertisement.getImgUrl() != null) {
            advertisement_.setImgUrl(advertisement.getImgUrl());
        }
        if(advertisement.getProductId() != null) {
            advertisement_.setProductId(advertisement.getProductId());
        }
        advertisementReposity.save(advertisement_);
        return true;
    }
}
