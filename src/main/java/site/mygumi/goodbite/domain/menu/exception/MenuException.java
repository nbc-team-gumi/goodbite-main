package site.mygumi.goodbite.domain.menu.exception;

import lombok.Getter;

@Getter
public class MenuException extends RuntimeException {

    private final MenuErrorCode menuErrorCode;

    public MenuException(MenuErrorCode menuErrorCode) {
        super(menuErrorCode.getMessage());
        this.menuErrorCode = menuErrorCode;
    }
}