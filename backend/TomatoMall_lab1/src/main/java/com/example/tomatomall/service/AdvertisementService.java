package com.example.tomatomall.service;

import com.example.tomatomall.vo.AdvertisementVO;

import java.util.List;

public interface AdvertisementService {
    List<AdvertisementVO> getAllAdvertisements();
    boolean createAdvertisement(AdvertisementVO advertisementVO);
    boolean updateAdvertisement(AdvertisementVO advertisementVO);
}
