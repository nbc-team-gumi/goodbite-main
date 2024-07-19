package com.sparta.goodbite.exception.menu.detail;

import com.sparta.goodbite.exception.menu.MenuErrorCode;
import com.sparta.goodbite.exception.menu.MenuException;

public class MenuNotFoundException extends MenuException {

    public MenuNotFoundException(MenuErrorCode menuErrorCode) {
        super(menuErrorCode);
    }
}