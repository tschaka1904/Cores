package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.utils.comparator.alias.UnambiguousAliasComparator;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;

import javax.persistence.*;

/**
 * IntAct implementation for alias
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18/12/13</pre>
 */
@Entity
@Table(name = "ia_alias")
public class IntactAlias extends AbstractIntactPrimaryObject implements Alias{

    private CvTerm type;
    private String name;

    public static final int MAX_ALIAS_NAME_LEN = 256;

    protected IntactAlias() {
        super();
    }

    public IntactAlias(CvTerm type, String name) {
        this(name);
        this.type = type;
    }

    public IntactAlias(String name) {
        if (name == null){
            throw new IllegalArgumentException("The alias name is required and cannot be null");
        }
        this.name = name;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "aliastype_ac" )
    @Target(IntactCvTerm.class)
    public CvTerm getType() {
        return this.type;
    }

    @Column( length = MAX_ALIAS_NAME_LEN, nullable = false)
    public String getName() {
        return this.name;
    }

    public void setType(CvTerm type) {
        this.type = type;
    }

    public void setName(String name) {
        if (name == null){
            throw new IllegalArgumentException("The alias name is required and cannot be null");
        }
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof Alias)){
            return false;
        }

        return UnambiguousAliasComparator.areEquals(this, (Alias) o);
    }

    @Override
    public int hashCode() {
        return UnambiguousAliasComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return name + (type != null ? "("+type.toString()+")" : "");
    }
}
