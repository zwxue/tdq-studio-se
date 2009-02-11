package net.sourceforge.sqlexplorer;

import java.sql.SQLException;

import net.sourceforge.sqlexplorer.dbproduct.User;

public class SQLCannotConnectException extends SQLException {

    private Throwable cause;

    private User user;

    public SQLCannotConnectException(User user) {
        super(getDesc(user));
        this.user = user;
    }

    public SQLCannotConnectException(User user, Throwable cause) {
        super(getDesc(user) + "\n" + cause.getMessage()); //$NON-NLS-1$
        this.user = user;
        this.cause = cause;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns a description of the alias
     * 
     * @param user
     * @return
     */
    private static String getDesc(User user) {
        String result = Messages.getString("SQLCannotConnectException.CannotConnecTo"); //$NON-NLS-1$
        if (user != null) {
            if (user.getAlias() != null)
                result += user.getAlias().getName() + "/" + user.getUserName(); //$NON-NLS-1$
            else
                result = Messages.getString("SQLCannotConnectException.user", result, user.getUserName()); //$NON-NLS-1$
        } else
            result = Messages.getString("SQLCannotConnectException.noUser", result); //$NON-NLS-1$
        return Messages.getString("SQLCannotConnectException.checkURL", result); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#getCause()
     */
    @Override
    public Throwable getCause() {
        return cause;
    }
}
