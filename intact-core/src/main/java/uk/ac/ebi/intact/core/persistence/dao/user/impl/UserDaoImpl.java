package uk.ac.ebi.intact.core.persistence.dao.user.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.core.persistence.dao.impl.IntactObjectDaoImpl;
import uk.ac.ebi.intact.core.persistence.dao.user.UserDao;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;

import javax.persistence.Query;
import java.util.List;

/**
 * User DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Repository
@SuppressWarnings( {"unchecked"} )
public class UserDaoImpl extends IntactObjectDaoImpl<User> implements UserDao {

    public UserDaoImpl() {
        super( User.class );
    }

    public User getByLogin( String login ) {
        final Query query = getEntityManager().createQuery( "select u from User as u where lower(u.login) = :login" );
        query.setParameter( "login", login.toLowerCase() );
        List<User> users = query.getResultList();
        if ( users.isEmpty() ) {
            return null;
        }
        return users.get( 0 );
    }

    public User getByEmail( String email ) {
        final Query query = getEntityManager().createQuery( "select u from User as u where lower(u.email) = :email" );
        query.setParameter( "email", email.toLowerCase() );
        List<User> users = query.getResultList();
        if ( users.isEmpty() ) {
            return null;
        }
        return users.get( 0 );
    }

    @Override
    public List<User> getByRole(String roleName) {
        final Query query = getEntityManager().createQuery("select u from User as u join u.roles as role where role.name = :roleName");
        query.setParameter("roleName", roleName);

        return query.getResultList();
    }

    @Override
    public List<User> getCurators() {
        return getByRole(Role.ROLE_CURATOR);
    }

    @Override
    public List<User> getReviewers() {
        return getByRole(Role.ROLE_REVIEWER);
    }

    @Override
    public List<User> getAdmins() {
        return getByRole(Role.ROLE_ADMIN);
    }
}
