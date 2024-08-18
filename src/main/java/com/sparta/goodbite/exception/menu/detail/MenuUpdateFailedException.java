package com.sparta.goodbite.exception.menu.detail;

import com.sparta.goodbite.exception.menu.MenuErrorCode;
import com.sparta.goodbite.exception.menu.MenuException;

public class MenuUpdateFailedException extends MenuException {

    public MenuUpdateFailedException(MenuErrorCode menuErrorCode) {
        super(menuErrorCode);
    }
}