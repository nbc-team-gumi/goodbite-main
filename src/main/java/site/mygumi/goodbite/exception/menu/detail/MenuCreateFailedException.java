package site.mygumi.goodbite.exception.menu.detail;

import site.mygumi.goodbite.exception.menu.MenuErrorCode;
import site.mygumi.goodbite.exception.menu.MenuException;

public class MenuCreateFailedException extends MenuException {

    public MenuCreateFailedException(MenuErrorCode menuErrorCode) {
        super(menuErrorCode);
    }
}