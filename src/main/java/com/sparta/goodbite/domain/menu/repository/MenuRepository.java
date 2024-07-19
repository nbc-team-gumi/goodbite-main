package com.sparta.goodbite.domain.menu.repository;

import com.sparta.goodbite.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

}