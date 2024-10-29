package site.mygumi.goodbite.domain.menu.exception.detail;

import site.mygumi.goodbite.domain.menu.exception.MenuErrorCode;
import site.mygumi.goodbite.domain.menu.exception.MenuException;

public class MenuNotFoundException extends MenuException {

    public MenuNotFoundException(MenuErrorCode menuErrorCode) {
        super(menuErrorCode);
    }
}