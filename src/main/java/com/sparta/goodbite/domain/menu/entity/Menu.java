package com.sparta.goodbite.domain.menu.entity;

import com.sparta.goodbite.common.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Menu extends Timestamped {

    @Id
    private long id;
}