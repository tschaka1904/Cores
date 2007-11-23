/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.Cascade;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a controlled vocabulary object. CvObject is derived from AnnotatedObject to allow to store annotation of
 * the term within the object itself, thus allowing to build an integrated dictionary.
 *
 * @author Henning Hermjakob
 * @version $Id$
 */
@Entity
@Table( name = "ia_controlledvocab",
        uniqueConstraints = {@UniqueConstraint(columnNames={"objclass", "shortlabel"})})
@DiscriminatorColumn( name = "objclass", discriminatorType = DiscriminatorType.STRING, length = 255 )
public abstract class CvObject extends AnnotatedObjectImpl<CvObjectXref, CvObjectAlias> implements Searchable {

    private String objClass;

    /**
     * PSI-MI Identifier for this object, which is a de-normalization of the
     * value contained in the 'identity' xref from the 'psimi' database 
     */
    private String miIdentifier;

    public CvObject() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that CvObjects cannot be created without at least a shortLabel and an
     * owner specified.
     *
     * @param shortLabel The memorable label to identify this CvObject
     * @param owner      The Institution which owns this CvObject
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    protected CvObject( Institution owner, String shortLabel ) {
        //super call sets up a valid AnnotatedObject (and also CvObject, as there is
        //nothing more to add)
        super( shortLabel, owner );
    }

    @Column( name = "objclass", insertable = false, updatable = false )
    public String getObjClass() {
        return objClass;
    }

    public void setObjClass( String objClass ) {
        this.objClass = objClass;
    }

    @ManyToMany( cascade = {CascadeType.PERSIST} )
    @JoinTable(
            name = "ia_cvobject2annot",
            joinColumns = {@JoinColumn( name = "cvobject_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "annotation_ac" )}
    )
    @Override
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<CvObjectXref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<CvObjectAlias> getAliases() {
        return super.getAliases();
    }

    /**
     * PSI-MI Identifier for this object, which is a de-normalization of the
     * value contained in the 'identity' xref from the 'psimi' database
     * @return the MI Identifier for this CVObject
     * @since 1.8
     */
    @Column(name = "mi_identifier", length = 10)
    public String getMiIdentifier() {
        return miIdentifier;
    }

    public void setMiIdentifier(String miIdentifier) {
        this.miIdentifier = miIdentifier;
    }

    /**
     * Equality for CvObject is currently based on equality for primary id of Xref having the qualifier of identity and
     * short label if there is xref of identity. We need to equals method to avoid circular references when invoking
     * equals methods
     *
     * @param obj The object to check
     *
     * @return true if given object has an identity xref and its primary id matches to this' object's primary id or
     *         short label if there is no identity xref.
     *
     * @see Xref
     */
    @Override
    public boolean equals( Object obj ) {
        if ( !super.equals( obj ) ) {
            return false;
        }

        if ( !( obj instanceof CvObject ) ) {
            return false;
        }

        final CvObject other = ( CvObject ) obj;

        if ( ac != null && other.getAc() != null ) {
            if ( ac.equals( other.getAc() ) ) {
                return true;
            }
        }

        // Check this object has an identity xref first.
        Xref idXref = CvObjectUtils.getPsiMiIdentityXref( this );
        Xref idOther = CvObjectUtils.getPsiMiIdentityXref( other );

        if ( ( idXref != null ) && ( idOther != null ) ) {
            // Both objects have the identity xrefs
            return idXref.getPrimaryId().equals( idOther.getPrimaryId() );
        }
        if ( ( idXref == null ) && ( idOther == null ) ) {
            // Revert to short labels.
            return getShortLabel().equals( other.getShortLabel() );
        }
        return false;
    }


    /**
     * This class overwrites equals. To ensure proper functioning of HashTable, hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();

        Xref idXref = CvObjectUtils.getPsiMiIdentityXref( this );

        //need check as we still have no-arg constructor...
        if ( idXref != null ) {
            result = 29 * result + idXref.getPrimaryId().hashCode();
        } else {
            result = 29 * result + ( ( getShortLabel() == null ) ? 31 : getShortLabel().hashCode() );
        }

        return result;
    }

} // end CvObject




