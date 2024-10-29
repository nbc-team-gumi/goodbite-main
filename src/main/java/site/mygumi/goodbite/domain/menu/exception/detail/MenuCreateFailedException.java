package site.mygumi.goodbite.domain.menu.exception.detail;

import site.mygumi.goodbite.domain.menu.exception.MenuErrorCode;
import site.mygumi.goodbite.domain.menu.exception.MenuException;

public class MenuCreateFailedException extends MenuException {

    public MenuCreateFailedException(MenuErrorCode menuErrorCode) {
        super(menuErrorCode);
    }
}