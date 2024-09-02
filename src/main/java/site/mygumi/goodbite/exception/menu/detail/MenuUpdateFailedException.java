package site.mygumi.goodbite.exception.menu.detail;

import site.mygumi.goodbite.exception.menu.MenuErrorCode;
import site.mygumi.goodbite.exception.menu.MenuException;

public class MenuUpdateFailedException extends MenuException {

    public MenuUpdateFailedException(MenuErrorCode menuErrorCode) {
        super(menuErrorCode);
    }
}