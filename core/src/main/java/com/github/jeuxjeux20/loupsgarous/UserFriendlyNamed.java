package com.github.jeuxjeux20.loupsgarous;

public interface UserFriendlyNamed {
    String getUserFriendlyName();

    static String stringify(Object obj) {
        return obj instanceof UserFriendlyNamed ?
                ((UserFriendlyNamed) obj).getUserFriendlyName() :
                obj.toString();
    }
}
