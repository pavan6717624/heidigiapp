package com.heidigi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heidigi.domain.ScanShopItem;

@Repository
public interface ScanShopItemRepository  extends JpaRepository<ScanShopItem, Long> {

}
