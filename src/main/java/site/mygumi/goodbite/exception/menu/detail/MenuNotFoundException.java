package site.mygumi.goodbite.exception.menu.detail;

import site.mygumi.goodbite.exception.menu.MenuErrorCode;
import site.mygumi.goodbite.exception.menu.MenuException;

public class MenuNotFoundException extends MenuException {

    public MenuNotFoundException(MenuErrorCode menuErrorCode) {
        super(menuErrorCode);
    }
}