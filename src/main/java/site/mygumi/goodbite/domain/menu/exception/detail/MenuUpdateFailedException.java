package site.mygumi.goodbite.domain.menu.exception.detail;

import site.mygumi.goodbite.domain.menu.exception.MenuErrorCode;
import site.mygumi.goodbite.domain.menu.exception.MenuException;

public class MenuUpdateFailedException extends MenuException {

    public MenuUpdateFailedException(MenuErrorCode menuErrorCode) {
        super(menuErrorCode);
    }
}