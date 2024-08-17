package com.sparta.goodbite.exception.menu.detail;

import com.sparta.goodbite.exception.menu.MenuErrorCode;
import com.sparta.goodbite.exception.menu.MenuException;

public class MenuCreateFailedException extends MenuException {

    public MenuCreateFailedException(MenuErrorCode menuErrorCode) {
        super(menuErrorCode);
    }

}