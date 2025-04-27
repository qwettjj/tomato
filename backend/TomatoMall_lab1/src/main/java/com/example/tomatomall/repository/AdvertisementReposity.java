package com.example.tomatomall.repository;

import com.example.tomatomall.po.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementReposity extends JpaRepository<Advertisement, Integer> {
}
